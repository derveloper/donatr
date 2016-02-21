package donatr.handler.query;

import donatr.aggregate.Transaction;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Observable;

public class TransactionListAggregateHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public TransactionListAggregateHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		eventStore.loadAll(Transaction.class).subscribe(transactions -> {
			final JsonArray array = new JsonArray();
			Observable.from(transactions)
					.flatMap(transactionObservable -> transactionObservable
							.doOnNext(transaction -> array.add(new JsonObject(Json.encode(transaction)))))
					.doOnCompleted(() -> {
						final JsonObject entries = new JsonObject().put("transactions", array);
						routingContext.response()
								.putHeader("content-type", "application/json")
								.end(entries.encode());
					}).subscribe();
		});
	}
}
