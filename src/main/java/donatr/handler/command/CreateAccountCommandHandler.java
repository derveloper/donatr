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

	public CreateAccountCommandHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(RoutingContext routingContext) {
		CreateAccountCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, AccountCreatedEvent.class);
	}

	private CreateAccountCommand getCommand(RoutingContext routingContext) {
		CreateAccountCommand command = new CreateAccountCommand();
		String name = routingContext.request().getFormAttribute("name");
		String id = UUID.randomUUID().toString();
		command.setName(name);
		command.setId(id);
		return command;
	}
}
