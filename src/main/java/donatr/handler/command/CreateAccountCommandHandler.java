package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.CreateAccountCommand;
import donatr.event.AccountCreatedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.util.UUID;

public class CreateAccountCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public CreateAccountCommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(final RoutingContext routingContext) {
		final CreateAccountCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, AccountCreatedEvent.class);
	}

	private CreateAccountCommand getCommand(final RoutingContext routingContext) {
		final CreateAccountCommand command = new CreateAccountCommand();
		final String name = routingContext.request().getFormAttribute("name");
		final String id = UUID.randomUUID().toString();
		command.setName(name);
		command.setId(id);
		return command;
	}
}
