package donatr;

import donatr.aggregate.Account;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

public class AccountAggregateHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public AccountAggregateHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");

		eventStore.load(id, Account.class).subscribe(dashboard -> {
			if(dashboard.getId() == null)
				routingContext.response().setStatusCode(404).end("aggregate not found");
			else routingContext.response().end(Json.encode(dashboard));
		});
	}
}
