package donatr;

import donatr.event.AccountCreatedEvent;
import io.resx.core.EventStore;
import io.resx.core.InMemoryEventStore;
import io.resx.core.MongoEventStore;
import io.resx.core.SQLiteEventStore;
import io.resx.core.command.Command;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.impl.codecs.JsonObjectMessageCodec;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notEmpty;

public class DonatrRouter extends AbstractVerticle {
	public void start() {
		final EventBus eventBus = vertx.eventBus();
		((io.vertx.core.eventbus.EventBus) eventBus.getDelegate())
				.registerDefaultCodec(AccountCreatedEvent.class, new EventMessageCodec());
		final EventStore eventStore = new InMemoryEventStore(eventBus);

		final HttpServer server = vertx.createHttpServer();

		final Router router = Router.router(vertx);
		final Router apiRouter = Router.router(vertx);

		router.route().handler(BodyHandler.create());

		final SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);

		final SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);

		final Map<String, MessageConsumer<String>> consumers = new HashMap<>();
		final WebsocketHandler websocketHandler = new WebsocketHandler(eventStore, consumers);
		sockJSHandler.socketHandler(websocketHandler);

		router.route("/socket*").handler(sockJSHandler);

		new CommandHandler(eventStore);
		apiRouter.get("/aggregate/dashboard/:id").handler(new DashboardAggregateHandler(eventStore));
		apiRouter.post("/account").handler(new AccountEventHandler(eventStore));

		router.mountSubRouter("/api", apiRouter);

		final StaticHandler staticHandler = StaticHandler.create();
		router.get().pathRegex("^(/.+\\.(js|css|ttf|png|jpg|woff|ico))").handler(staticHandler);

		router.get("/*").handler(routingContext -> {
			routingContext.response().sendFile("webroot/index.html");
		});

		server.requestHandler(router::accept).listen(8080);
	}

	public static <T extends Command, R> void publishCommand(final T payload, final EventStore eventStore, final RoutingContext routingContext, final Class<R> clazz) {
		final HttpServerResponse response = routingContext.response();

		eventStore.publish(payload, clazz)
				.onErrorResumeNext(message -> {
					response.setStatusCode(500).end(message.getMessage());
					return Observable.empty();
				})
				.subscribe(reply -> {
					response.setStatusCode(200).end(Json.encode(reply));
				});
	}
}
