package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.DeleteAccountCommand;
import donatr.event.AccountDeletedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

public class DeleteAccountCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public DeleteAccountCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final DeleteAccountCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, AccountDeletedEvent.class);
	}

	private DeleteAccountCommand getCommand(final RoutingContext routingContext) {
		return Json.decodeValue(routingContext.getBodyAsString(), DeleteAccountCommand.class);
	}
}
