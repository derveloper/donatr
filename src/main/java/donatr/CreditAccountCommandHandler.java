package donatr;

import donatr.command.CreditAccountCommand;
import donatr.command.DepositAccountCommand;
import donatr.event.AccountDepositedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

public class CreditAccountCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public CreditAccountCommandHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(RoutingContext routingContext) {
		CreditAccountCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, AccountDepositedEvent.class);
	}

	private CreditAccountCommand getCommand(RoutingContext routingContext) {
		return Json.decodeValue(routingContext.getBodyAsString(), CreditAccountCommand.class);
	}
}
