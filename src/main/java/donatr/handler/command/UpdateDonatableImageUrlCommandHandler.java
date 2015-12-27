package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.UpdateDonatableImageUrlCommand;
import donatr.event.DonatableImageUrlUpdatedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

public class UpdateDonatableImageUrlCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public UpdateDonatableImageUrlCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final UpdateDonatableImageUrlCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, DonatableImageUrlUpdatedEvent.class);
	}

	private UpdateDonatableImageUrlCommand getCommand(final RoutingContext routingContext) {
		return Json.decodeValue(routingContext.getBodyAsString(), UpdateDonatableImageUrlCommand.class);
	}
}
