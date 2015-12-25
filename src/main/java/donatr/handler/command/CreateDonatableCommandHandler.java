package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.CreateDonatableCommand;
import donatr.event.DonatableCreatedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateDonatableCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public CreateDonatableCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final CreateDonatableCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, DonatableCreatedEvent.class);
	}

	private CreateDonatableCommand getCommand(final RoutingContext routingContext) {
		final CreateDonatableCommand command = Json.decodeValue(routingContext.getBodyAsString(), CreateDonatableCommand.class);
		command.setId(UUID.randomUUID().toString());
		command.setAmount(command.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
		return command;
	}
}
