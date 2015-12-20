package donatr;

import donatr.command.DepositAccountCommand;
import donatr.event.AccountCreatedEvent;
import donatr.event.AccountDepositedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

public class DepositAccountCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public DepositAccountCommandHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(RoutingContext routingContext) {
		DepositAccountCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, AccountDepositedEvent.class);
	}

	private DepositAccountCommand getCommand(RoutingContext routingContext) {
		return Json.decodeValue(routingContext.getBodyAsString(), DepositAccountCommand.class);
	}
}
