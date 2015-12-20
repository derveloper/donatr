package donatr;

import donatr.command.CreateAccountCommand;
import donatr.command.CreateTransactionCommand;
import donatr.command.CreditAccountCommand;
import donatr.command.DepositAccountCommand;
import donatr.event.AccountCreatedEvent;
import donatr.event.AccountCreditedEvent;
import donatr.event.AccountDepositedEvent;
import donatr.event.TransactionCreatedEvent;
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
			eventStore.publishSourcedEvent(createdEvent, AccountDepositedEvent.class)
					.subscribe(message::reply);
		});

		eventStore.consumer(CreateAccountCommand.class, message -> {
			CreateAccountCommand createCommand = Json.decodeValue(message.body(), CreateAccountCommand.class);
			AccountCreatedEvent createdEvent = new AccountCreatedEvent(createCommand.getId(), createCommand.getName());
			eventStore.publishSourcedEvent(createdEvent, AccountCreatedEvent.class)
					.subscribe(message::reply);
		});

		eventStore.consumer(CreditAccountCommand.class, message -> {
			CreditAccountCommand createCommand = Json.decodeValue(message.body(), CreditAccountCommand.class);
			AccountCreditedEvent createdEvent = new AccountCreditedEvent(createCommand.getId(), createCommand.getAmount());
			eventStore.publishSourcedEvent(createdEvent, AccountCreditedEvent.class)
					.subscribe(message::reply);
		});

		eventStore.consumer(CreateTransactionCommand.class, message -> {
			CreateTransactionCommand createCommand = Json.decodeValue(message.body(), CreateTransactionCommand.class);
			TransactionCreatedEvent createdEvent = new TransactionCreatedEvent(
					createCommand.getId(),
					createCommand.getAccountFrom(),
					createCommand.getAccountTo(),
					createCommand.getAmount()
			);
			eventStore.publishSourcedEvent(createdEvent, TransactionCreatedEvent.class)
					.subscribe(event -> {
						AccountDepositedEvent depositEvent = new AccountDepositedEvent();
						depositEvent.setId(event.getAccountTo());
						depositEvent.setAmount(event.getAmount());

						AccountCreditedEvent creditEvent = new AccountCreditedEvent();
						creditEvent.setId(event.getAccountFrom());
						creditEvent.setAmount(event.getAmount());

						eventStore.publishSourcedEvent(depositEvent, AccountDepositedEvent.class)
								.subscribe();
						eventStore.publishSourcedEvent(creditEvent, AccountCreditedEvent.class)
								.subscribe();
						message.reply(event);
					});
		});
	}
}
