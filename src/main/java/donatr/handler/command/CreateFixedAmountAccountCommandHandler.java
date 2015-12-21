package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.CreateFixedAmountAccountCommand;
import donatr.event.FixedAmountAccountCreatedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateFixedAmountAccountCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public CreateFixedAmountAccountCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final CreateFixedAmountAccountCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, FixedAmountAccountCreatedEvent.class);
	}

	private CreateFixedAmountAccountCommand getCommand(final RoutingContext routingContext) {
		final CreateFixedAmountAccountCommand command = Json.decodeValue(routingContext.getBodyAsString(), CreateFixedAmountAccountCommand.class);
		command.setId(UUID.randomUUID().toString());
		command.setAmount(command.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
		return command;
	}
}
