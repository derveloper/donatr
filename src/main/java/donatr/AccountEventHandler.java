package donatr;

import donatr.command.CreateAccountCommand;
import io.resx.core.MongoEventStore;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import donatr.command.DepositAccountCommand;

import java.math.BigDecimal;

public class AccountEventHandler implements Handler<RoutingContext> {
	private final MongoEventStore eventStore;

	public AccountEventHandler(MongoEventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(RoutingContext routingContext) {
		CreateAccountCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, String.class);
	}

	private CreateAccountCommand getCommand(RoutingContext routingContext) {
		CreateAccountCommand command = new CreateAccountCommand();
		String name = routingContext.request().getFormAttribute("name");
		String id = routingContext.request().getParam("id");
		command.setName(name);
		command.setId(id);
		return command;
	}
}
