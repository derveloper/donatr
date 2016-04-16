package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.UpdateAccountNameCommand;
import donatr.event.AccountNameUpdatedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

public class UpdateAccountNameCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public UpdateAccountNameCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final UpdateAccountNameCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, AccountNameUpdatedEvent.class);
	}

	private UpdateAccountNameCommand getCommand(final RoutingContext routingContext) {
		return Json.decodeValue(routingContext.getBodyAsString(), UpdateAccountNameCommand.class);
	}
}
