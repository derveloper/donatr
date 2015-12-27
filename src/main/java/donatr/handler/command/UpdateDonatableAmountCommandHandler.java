package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.DebitAccountCommand;
import donatr.command.UpdateDonatableAmountCommand;
import donatr.event.AccountDebitedEvent;
import donatr.event.DonatableAmountUpdatedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.math.BigDecimal;

public class UpdateDonatableAmountCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public UpdateDonatableAmountCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final UpdateDonatableAmountCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, DonatableAmountUpdatedEvent.class);
	}

	private UpdateDonatableAmountCommand getCommand(final RoutingContext routingContext) {
		final UpdateDonatableAmountCommand command = Json.decodeValue(routingContext.getBodyAsString(), UpdateDonatableAmountCommand.class);
		command.setAmount(command.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
		return command;
	}
}
