package donatr.handler.query;

import donatr.aggregate.Donatable;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

public class DonatableAggregateHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public DonatableAggregateHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");

		eventStore.load(id, Donatable.class).subscribe(dashboard -> {
			if (dashboard.getId() == null)
				routingContext.response().setStatusCode(404).end("aggregate not found");
			else routingContext.response()
					.putHeader("content-type", "application/json").end(Json.encode(dashboard));
		});
	}
}
