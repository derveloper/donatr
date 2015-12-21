package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.CreditAccountCommand;
import donatr.event.AccountCreditedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.math.BigDecimal;

public class CreditAccountCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public CreditAccountCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final CreditAccountCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, AccountCreditedEvent.class);
	}

	private CreditAccountCommand getCommand(final RoutingContext routingContext) {
		final CreditAccountCommand command = Json.decodeValue(routingContext.getBodyAsString(), CreditAccountCommand.class);
		command.setAmount(command.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
		return command;
	}
}
