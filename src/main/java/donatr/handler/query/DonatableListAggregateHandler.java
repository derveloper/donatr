package donatr.handler.query;

import donatr.aggregate.Donatable;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Observable;

public class DonatableListAggregateHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public DonatableListAggregateHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		eventStore.loadAll(Donatable.class).subscribe(donatables -> {
			final JsonArray array = new JsonArray();
			Observable.from(donatables)
					.flatMap(accountObservable -> accountObservable
							.doOnNext(donatable -> array.add(new JsonObject(Json.encode(donatable)))))
					.doOnCompleted(() -> {
						final JsonObject entries = new JsonObject().put("donatables", array);
						routingContext.response()
								.putHeader("content-type", "application/json")
								.end(entries.encode());
					}).subscribe();
		});
	}
}
