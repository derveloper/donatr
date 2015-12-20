package donatr;

import donatr.command.CreateAccountCommand;
import donatr.command.DepositAccountCommand;
import donatr.event.AccountCreatedEvent;
import donatr.event.AccountDepositedEvent;
import io.resx.core.EventStore;
import io.vertx.core.json.Json;

public class CommandHandler {
	private EventStore eventStore;

	public CommandHandler(EventStore eventStore) {
		this.eventStore = eventStore;
		attachCommandHandlers();
	}

	private void attachCommandHandlers() {
		eventStore.consumer(DepositAccountCommand.class, message -> {
			DepositAccountCommand createCommand = Json.decodeValue(message.body(), DepositAccountCommand.class);
			AccountDepositedEvent createdEvent = new AccountDepositedEvent(createCommand.getId(), createCommand.getAmount());
			eventStore.publish(createdEvent, AccountDepositedEvent.class)
					.subscribe(accountDepositedEvent -> {
						message.reply(accountDepositedEvent);
					});
		});

		eventStore.consumer(CreateAccountCommand.class, message -> {
			CreateAccountCommand createCommand = Json.decodeValue(message.body(), CreateAccountCommand.class);
			AccountCreatedEvent createdEvent = new AccountCreatedEvent(createCommand.getId(), createCommand.getName());
			eventStore.publish(createdEvent, AccountCreatedEvent.class).subscribe(message::reply);
		});
	}
}
