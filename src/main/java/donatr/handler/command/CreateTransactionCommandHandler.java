package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.CreateTransactionCommand;
import donatr.event.TransactionCreatedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.util.UUID;

public class CreateTransactionCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public CreateTransactionCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final CreateTransactionCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, TransactionCreatedEvent.class);
	}

	private CreateTransactionCommand getCommand(final RoutingContext routingContext) {
		final CreateTransactionCommand createTransactionCommand = Json.decodeValue(routingContext.getBodyAsString(), CreateTransactionCommand.class);
		createTransactionCommand.setId(UUID.randomUUID().toString());
		return createTransactionCommand;
	}
}
