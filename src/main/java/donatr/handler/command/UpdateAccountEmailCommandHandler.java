package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.UpdateAccountEmailCommand;
import donatr.event.AccountEmailUpdatedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

public class UpdateAccountEmailCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public UpdateAccountEmailCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final UpdateAccountEmailCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, AccountEmailUpdatedEvent.class);
	}

	private UpdateAccountEmailCommand getCommand(final RoutingContext routingContext) {
		return Json.decodeValue(routingContext.getBodyAsString(), UpdateAccountEmailCommand.class);
	}
}
