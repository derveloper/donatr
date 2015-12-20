package donatr.handler;

import donatr.aggregate.FixedAmountAccount;
import donatr.command.*;
import donatr.event.*;
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

		eventStore.consumer(CreateFixedAmountAccountCommand.class, message -> {
			CreateFixedAmountAccountCommand createCommand = Json.decodeValue(message.body(), CreateFixedAmountAccountCommand.class);
			FixedAmountAccountCreatedEvent createdEvent = new FixedAmountAccountCreatedEvent(
					createCommand.getId(),
					createCommand.getName(),
					createCommand.getAmount());
			eventStore.publishSourcedEvent(createdEvent, FixedAmountAccountCreatedEvent.class)
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

						eventStore
								.load(event.getAccountTo(), FixedAmountAccount.class)
								.subscribe(fixedAmountDonation1 -> {
									if (fixedAmountDonation1 != null && fixedAmountDonation1.getId() != null) {
										creditEvent.setAmount(fixedAmountDonation1.getAmount());
										depositEvent.setAmount(fixedAmountDonation1.getAmount());
									}
									eventStore.publishSourcedEvent(depositEvent, AccountDepositedEvent.class)
											.subscribe();
									eventStore.publishSourcedEvent(creditEvent, AccountCreditedEvent.class)
											.subscribe();
									message.reply(event);
								});
					});
		});
	}
}
