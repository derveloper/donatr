package donatr;

import donatr.event.AccountCreatedEvent;
import donatr.event.AccountCreditedEvent;
import donatr.event.AccountDepositedEvent;
import donatr.event.TransactionCreatedEvent;
import io.resx.core.EventStore;
import io.resx.core.InMemoryEventStore;
import io.resx.core.command.Command;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.auth.jwt.JWTAuth;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.CorsHandler;
import io.vertx.rxjava.ext.web.handler.JWTAuthHandler;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DonatrRouter extends AbstractVerticle {
	public void start() {
		final EventBus eventBus = vertx.eventBus();
		((io.vertx.core.eventbus.EventBus) eventBus.getDelegate())
				.registerDefaultCodec(AccountCreatedEvent.class,
						new DistributedEventMessageCodec<>(AccountCreatedEvent.class))
				.registerDefaultCodec(AccountDepositedEvent.class,
						new DistributedEventMessageCodec<>(AccountDepositedEvent.class))
				.registerDefaultCodec(AccountCreditedEvent.class,
						new DistributedEventMessageCodec<>(AccountCreditedEvent.class))
				.registerDefaultCodec(TransactionCreatedEvent.class,
						new DistributedEventMessageCodec<>(TransactionCreatedEvent.class))
		;
		//final EventStore eventStore = new SQLiteEventStore(vertx, eventBus, null);
		final EventStore eventStore = new InMemoryEventStore(eventBus);
		new CommandHandler(eventStore);

		final JWTAuth authProvider = getJwtAuth();

		final HttpServer server = vertx.createHttpServer();

		final Router router = Router.router(vertx);
		final Router apiRouter = Router.router(vertx);

		router.route().handler(CorsHandler.create("*"));
		router.route().handler(BodyHandler.create());

		apiRouter.post("/session").handler(loginHandler(authProvider));

		apiRouter
				.routeWithRegex("^((?!/session).)*$")
				.handler(routingContext -> {
					routingContext.cookies().stream()
							.filter(cookie1 -> "auth".equals(cookie1.getName()))
							.findFirst().ifPresent(cookie2 -> {
						if ("auth".equals(cookie2.getName()) && StringUtils.isNotBlank(cookie2.getValue())) {
							routingContext.request().headers()
									.add("Authorization", "Bearer " + cookie2.getValue());
						}
					});
					JWTAuthHandler.create(authProvider).handle(routingContext);
				});

		apiRouter.get("/session").handler(routingContext2 -> {
			JWTAuthHandler.create(authProvider).handle(routingContext2);
			if(!routingContext2.response().ended()) {
				routingContext2.response().end();
			}
		});

		apiRouter.delete("/session")
				.handler(routingContext -> {
					JWTAuthHandler.create(authProvider).handle(routingContext);
					if(!routingContext.response().ended()) {
						Cookie cookie = Cookie.cookie("auth", "");
						cookie.setMaxAge(TimeUnit.DAYS.toSeconds(-1));
						routingContext.addCookie(cookie);
						routingContext.response().end("Bye!");
					}
				});

		final SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);

		final SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);

		final Map<String, MessageConsumer<String>> consumers = new HashMap<>();
		final WebsocketHandler websocketHandler = new WebsocketHandler(eventStore, consumers);
		sockJSHandler.socketHandler(websocketHandler);

		router.route("/socket*").handler(sockJSHandler);

		apiRouter.get("/aggregate/account/:id").handler(new AccountAggregateHandler(eventStore));
		apiRouter.post("/account").handler(new CreateAccountCommandHandler(eventStore));
		apiRouter.post("/account/deposit").handler(new DepositAccountCommandHandler(eventStore));
		apiRouter.post("/account/credit").handler(new CreditAccountCommandHandler(eventStore));
		apiRouter.post("/transaction").handler(new CreateTransactionCommandHandler(eventStore));

		router.mountSubRouter("/api", apiRouter);

		final StaticHandler staticHandler = StaticHandler.create();
		router.get().pathRegex("^(/.+\\.(js|css|ttf|gif|png|jpg|woff|ico))").handler(staticHandler);

		router.get("/*").handler(routingContext -> {
			routingContext.response().sendFile("webroot/index.html");
		});

		server.requestHandler(router::accept).listen(8080);
	}

	public static <T extends Command, R> void publishCommand(final T payload, final EventStore eventStore, final RoutingContext routingContext, final Class<R> clazz) {
		final HttpServerResponse response = routingContext.response();

		eventStore.publish(payload, clazz)
				.onErrorResumeNext(message -> {
					System.out.println(message.getMessage());
					response.setStatusCode(500).end(message.getMessage());
					return Observable.empty();
				})
				.subscribe(reply -> {
					response.setStatusCode(200).end(Json.encode(reply));
				});
	}

	private Handler<RoutingContext> loginHandler(final JWTAuth authProvider) {
		return routingContext -> {
			final HttpServerRequest request = routingContext.request();
			final HttpServerResponse response = routingContext.response();
			final String username = request.getFormAttribute("username");
			final String password = request.getFormAttribute("password");

			if ("test".equals(username) && "test".equals(password)) {
				final String token = authProvider.generateToken(new JsonObject().put("username", username), new JWTOptions());
				Cookie cookie = Cookie.cookie("auth", token);
				cookie.setMaxAge(TimeUnit.HOURS.toSeconds(12));
				cookie.setHttpOnly(true);
				routingContext.addCookie(cookie);
				response.setStatusCode(200).end(token);
			} else {
				response.setStatusCode(401).end();
			}
		};
	}

	private JWTAuth getJwtAuth() {
		final JsonObject config = new JsonObject().put("keyStore", new JsonObject()
				.put("path", "keystore.jceks")
				.put("type", "jceks")
				.put("password", "secret"));

		return JWTAuth.create(vertx, config);
	}
}
