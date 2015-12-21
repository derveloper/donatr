package donatr;

import donatr.event.*;
import donatr.handler.CommandHandler;
import donatr.handler.WebsocketHandler;
import donatr.handler.command.*;
import donatr.handler.query.AccountAggregateHandler;
import donatr.handler.query.AccountListAggregateHandler;
import donatr.handler.query.FixedAmountAccountAggregateHandler;
import io.resx.core.EventStore;
import io.resx.core.SQLiteEventStore;
import io.resx.core.command.Command;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
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
import io.vertx.rxjava.ext.web.handler.*;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DonatrRouter extends AbstractVerticle {

	private JWTAuthHandler jwtAuthHandler;
	private JWTAuth authProvider;

	public static <T extends Command, R> void publishCommand(final T payload, final EventStore eventStore, final RoutingContext routingContext, final Class<R> clazz) {
		final HttpServerResponse response = routingContext.response();

		eventStore.publish(payload, clazz)
				.onErrorResumeNext(message -> {
					System.out.println(message.getMessage());
					response.putHeader("content-type", "application/json")
							.setStatusCode(500).end(message.getMessage());
					return Observable.empty();
				})
				.subscribe(reply -> {
					response.putHeader("content-type", "application/json")
							.setStatusCode(200).end(Json.encode(reply));
				});
	}

	public void start() {
		final EventBus eventBus = vertx.eventBus();
		((io.vertx.core.eventbus.EventBus) eventBus.getDelegate())
				.registerDefaultCodec(AccountCreatedEvent.class,
						new DistributedEventMessageCodec<>(AccountCreatedEvent.class))
				.registerDefaultCodec(AccountCreditedEvent.class,
						new DistributedEventMessageCodec<>(AccountCreditedEvent.class))
				.registerDefaultCodec(AccountDebitedEvent.class,
						new DistributedEventMessageCodec<>(AccountDebitedEvent.class))
				.registerDefaultCodec(TransactionCreatedEvent.class,
						new DistributedEventMessageCodec<>(TransactionCreatedEvent.class))
				.registerDefaultCodec(FixedAmountAccountCreatedEvent.class,
						new DistributedEventMessageCodec<>(FixedAmountAccountCreatedEvent.class))
		;
		final EventStore eventStore = new SQLiteEventStore(vertx, eventBus, null);

		new CommandHandler(eventStore);

		authProvider = getJwtAuth();

		final HttpServer server = vertx.createHttpServer();

		final Router router = Router.router(vertx);
		final Router apiRouter = Router.router(vertx);

		final CorsHandler corsHandler = CorsHandler
				.create("http://localhost:3000")
				.allowedMethod(HttpMethod.GET)
				.allowedMethod(HttpMethod.POST)
				.allowedMethod(HttpMethod.PUT)
				.allowedMethod(HttpMethod.PATCH)
				.allowedMethod(HttpMethod.OPTIONS)
				.allowedMethod(HttpMethod.HEAD)
				.allowedMethod(HttpMethod.DELETE)
				.allowedHeaders(new HashSet<>(Arrays.asList("authorization", "content-type", "content-length", "accept", "cookie")))
				.allowCredentials(true);
		router.route().handler(corsHandler);
		apiRouter.route().handler(corsHandler);
		router.route().handler(CookieHandler.create());
		router.route().handler(BodyHandler.create());

		apiRouter.post("/session").handler(loginHandler(authProvider));

		jwtAuthHandler = JWTAuthHandler.create(authProvider);

		apiRouter.get("/session").handler(this::mapAuthCookieToHeader);
		apiRouter.get("/session").handler(routingContext -> {
			if (!routingContext.response().ended()) {
				routingContext.response().end();
			}
				});

		apiRouter.delete("/session").handler(this::mapAuthCookieToHeader);
		apiRouter.delete("/session")
				.handler(routingContext -> {
					if (!routingContext.response().ended()) {
						final Cookie cookie = Cookie.cookie("auth", "");
						cookie.setMaxAge(TimeUnit.DAYS.toSeconds(-1));
						routingContext.addCookie(cookie);
						routingContext.response().end("Bye!");
					}
				});

		apiRouter
				.routeWithRegex("^(?!/session)$")
				.handler(this::mapAuthCookieToHeader);

		apiRouter.get("/aggregate/account/:id").handler(new AccountAggregateHandler(eventStore));
		apiRouter.get("/aggregate/account").handler(new AccountListAggregateHandler(eventStore));
		apiRouter.get("/aggregate/fixedamountaccount/:id").handler(new FixedAmountAccountAggregateHandler(eventStore));
		apiRouter.post("/account").handler(new CreateAccountCommandHandler(eventStore));
		apiRouter.post("/account/credit").handler(new CreditAccountCommandHandler(eventStore));
		apiRouter.post("/account/debit").handler(new DebitAccountCommandHandler(eventStore));
		apiRouter.post("/transaction").handler(new CreateTransactionCommandHandler(eventStore));
		apiRouter.post("/donation").handler(new CreateFixedAmountAccountCommandHandler(eventStore));

		router.mountSubRouter("/api", apiRouter);

		final SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);

		final SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);

		final Map<String, MessageConsumer<String>> consumers = new HashMap<>();
		final WebsocketHandler websocketHandler = new WebsocketHandler(eventStore, consumers);
		sockJSHandler.socketHandler(websocketHandler);

		router.route("/socket*").handler(sockJSHandler);

		final StaticHandler staticHandler = StaticHandler.create();
		router.get().pathRegex("^(/.+\\.(js|css|ttf|gif|png|jpg|woff|ico))$").handler(staticHandler::handle);

		router.get("/").handler(routingContext -> routingContext.response().sendFile("webroot/index.html"));

		server.requestHandler(router::accept).listen(8080);
	}

	private void mapAuthCookieToHeader(RoutingContext routingContext) {
		routingContext.cookies().stream()
				.filter(cookie1 -> "auth".equals(cookie1.getName()))
				.findFirst().ifPresent(cookie2 -> {
			if ("auth".equals(cookie2.getName()) && StringUtils.isNotBlank(cookie2.getValue())) {
				routingContext.request().headers()
						.add("Authorization", "Bearer " + cookie2.getValue());
			}
		});
		JWTAuthHandler.create(authProvider).handle(routingContext);
	}

	private Handler<RoutingContext> loginHandler(final JWTAuth authProvider) {
		return routingContext -> {
			final HttpServerRequest request = routingContext.request();
			final HttpServerResponse response = routingContext.response();
			final String username = request.getFormAttribute("username");
			final String password = request.getFormAttribute("password");

			if ("test".equals(username) && "test".equals(password)) {
				final String token = authProvider.generateToken(new JsonObject().put("username", username), new JWTOptions());
				final Cookie cookie = Cookie.cookie("auth", token);
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
