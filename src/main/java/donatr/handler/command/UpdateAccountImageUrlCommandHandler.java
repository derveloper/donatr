package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.UpdateAccountImageUrlCommand;
import donatr.event.AccountImageUrlUpdatedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

public class UpdateAccountImageUrlCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public UpdateAccountImageUrlCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final UpdateAccountImageUrlCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, AccountImageUrlUpdatedEvent.class);
	}

	private UpdateAccountImageUrlCommand getCommand(final RoutingContext routingContext) {
		return Json.decodeValue(routingContext.getBodyAsString(), UpdateAccountImageUrlCommand.class);
	}
}
