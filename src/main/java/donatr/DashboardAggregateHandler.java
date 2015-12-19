package donatr;

import io.resx.core.MongoEventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import donatr.aggregate.Account;

import static donatr.Constants.getTodaysEventsQueryFor;

public class DashboardAggregateHandler implements Handler<RoutingContext> {
	private final MongoEventStore eventStore;

	public DashboardAggregateHandler(MongoEventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");

		JsonObject query = getTodaysEventsQueryFor(id);

		eventStore.load(query, Account.class).subscribe(dashboard -> {
			if(dashboard.getId() == null)
				routingContext.response().setStatusCode(404).end("aggregate not found");
			else routingContext.response().end(Json.encode(dashboard));
		});
	}
}
