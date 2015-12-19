package donatr;

import donatr.command.CreateAccountCommand;
import donatr.command.DepositAccountCommand;
import donatr.event.AccountCreatedEvent;
import donatr.event.AccountDepositedEvent;
import io.resx.core.MongoEventStore;
import io.vertx.core.json.Json;

public class CommandHandler {
	private MongoEventStore eventStore;

	public CommandHandler(MongoEventStore eventStore) {
		this.eventStore = eventStore;
		attachCommandHandlers();
	}

	private void attachCommandHandlers() {
		eventStore.consumer(DepositAccountCommand.class, message -> {
			DepositAccountCommand createCommand = Json.decodeValue(message.body(), DepositAccountCommand.class);
			AccountDepositedEvent createdEvent = new AccountDepositedEvent(createCommand.getId(), createCommand.getAmount());
			eventStore.publish(createdEvent, AccountDepositedEvent.class).subscribe();
		});

		eventStore.consumer(CreateAccountCommand.class, message -> {
			CreateAccountCommand createCommand = Json.decodeValue(message.body(), CreateAccountCommand.class);
			AccountCreatedEvent createdEvent = new AccountCreatedEvent(createCommand.getId(), createCommand.getName());
			eventStore.publish(createdEvent, AccountCreatedEvent.class).subscribe();
		});
	}
}
