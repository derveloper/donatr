package donatr.handler.query;

import donatr.aggregate.Account;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Observable;

public class AccountListAggregateHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public AccountListAggregateHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		eventStore.loadAll(Account.class).subscribe(accounts -> {
			final JsonArray array = new JsonArray();
			Observable.from(accounts)
					.flatMap(accountObservable -> accountObservable
							.filter(account -> !account.isDeleted())
							.doOnNext(account -> array.add(new JsonObject(Json.encode(account)))))
					.doOnCompleted(() -> {
						final JsonObject entries = new JsonObject().put("accounts", array);
						routingContext.response()
								.putHeader("content-type", "application/json")
								.end(entries.encode());
					}).subscribe();
		});
	}
}
