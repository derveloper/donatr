package donatr.handler.command;

import donatr.DonatrRouter;
import donatr.command.DebitAccountCommand;
import donatr.event.AccountDebitedEvent;
import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

public class DebitAccountCommandHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public DebitAccountCommandHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(RoutingContext routingContext) {
		DebitAccountCommand command = getCommand(routingContext);
		DonatrRouter.publishCommand(command, eventStore, routingContext, AccountDebitedEvent.class);
	}

	private DebitAccountCommand getCommand(RoutingContext routingContext) {
		return Json.decodeValue(routingContext.getBodyAsString(), DebitAccountCommand.class);
	}
}
