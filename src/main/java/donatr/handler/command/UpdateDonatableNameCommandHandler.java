package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.UpdateDonatableNameCommand;
import donatr.event.DonatableNameUpdatedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

public class UpdateDonatableNameCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public UpdateDonatableNameCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final UpdateDonatableNameCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, DonatableNameUpdatedEvent.class);
	}

	private UpdateDonatableNameCommand getCommand(final RoutingContext routingContext) {
		return Json.decodeValue(routingContext.getBodyAsString(), UpdateDonatableNameCommand.class);
	}
}
