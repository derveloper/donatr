package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.DebitAccountCommand;
import donatr.event.AccountDebitedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.math.BigDecimal;

public class DebitAccountCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public DebitAccountCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final DebitAccountCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, AccountDebitedEvent.class);
	}

	private DebitAccountCommand getCommand(final RoutingContext routingContext) {
		final DebitAccountCommand command = Json.decodeValue(routingContext.getBodyAsString(), DebitAccountCommand.class);
		command.setAmount(command.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
		return command;
	}
}
