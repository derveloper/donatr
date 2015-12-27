package donatr.handler;

import donatr.aggregate.Donatable;
import donatr.command.*;
import donatr.event.*;
import io.resx.core.EventStore;
import io.vertx.core.json.Json;

public class CommandHandler {
	private EventStore eventStore;

	public CommandHandler(final EventStore eventStore) {
		this.eventStore = eventStore;
		attachCommandHandlers();
	}

	private void attachCommandHandlers() {
		eventStore.consumer(CreditAccountCommand.class, message -> {
			final CreditAccountCommand createCommand = Json.decodeValue(message.body(), CreditAccountCommand.class);
			final AccountCreditedEvent createdEvent = new AccountCreditedEvent(createCommand.getId(), createCommand.getAmount());
			eventStore.publishSourcedEvent(createdEvent, AccountCreditedEvent.class)
					.subscribe(message::reply);
		});

		eventStore.consumer(CreateAccountCommand.class, message -> {
			final CreateAccountCommand createCommand = Json.decodeValue(message.body(), CreateAccountCommand.class);
			final AccountCreatedEvent createdEvent = new AccountCreatedEvent(
					createCommand.getId(),
					createCommand.getName(),
					createCommand.getEmail());
			eventStore.publishSourcedEvent(createdEvent, AccountCreatedEvent.class)
					.subscribe(message::reply);
		});

		eventStore.consumer(DebitAccountCommand.class, message -> {
			final DebitAccountCommand createCommand = Json.decodeValue(message.body(), DebitAccountCommand.class);
			final AccountDebitedEvent createdEvent = new AccountDebitedEvent(createCommand.getId(), createCommand.getAmount());
			eventStore.publishSourcedEvent(createdEvent, AccountDebitedEvent.class)
					.subscribe(message::reply);
		});

		eventStore.consumer(CreateDonatableCommand.class, message -> {
			final CreateDonatableCommand createCommand = Json.decodeValue(message.body(), CreateDonatableCommand.class);
			final DonatableCreatedEvent createdEvent = new DonatableCreatedEvent(
					createCommand.getId(),
					createCommand.getName(),
					createCommand.getAmount());
			eventStore.publishSourcedEvent(createdEvent, DonatableCreatedEvent.class)
					.subscribe(message::reply);
		});

		eventStore.consumer(CreateTransactionCommand.class, message -> {
			final CreateTransactionCommand createCommand = Json.decodeValue(message.body(), CreateTransactionCommand.class);
			final TransactionCreatedEvent createdEvent = new TransactionCreatedEvent(
					createCommand.getId(),
					createCommand.getAccountFrom(),
					createCommand.getAccountTo(),
					createCommand.getAmount()
			);
			eventStore.publishSourcedEvent(createdEvent, TransactionCreatedEvent.class)
					.subscribe(event -> {
						final AccountCreditedEvent depositEvent = new AccountCreditedEvent();
						depositEvent.setId(event.getAccountTo());
						depositEvent.setAmount(event.getAmount());

						final AccountDebitedEvent creditEvent = new AccountDebitedEvent();
						creditEvent.setId(event.getAccountFrom());
						creditEvent.setAmount(event.getAmount());

						eventStore
								.load(event.getAccountTo(), Donatable.class)
								.subscribe(fixedAmountDonation1 -> {
									if (fixedAmountDonation1 != null && fixedAmountDonation1.getId() != null) {
										creditEvent.setAmount(fixedAmountDonation1.getAmount());
										depositEvent.setAmount(fixedAmountDonation1.getAmount());
									}
									eventStore.publishSourcedEvent(depositEvent, AccountCreditedEvent.class)
											.subscribe();
									eventStore.publishSourcedEvent(creditEvent, AccountDebitedEvent.class)
											.subscribe();
									event.setAmount(creditEvent.getAmount());
									message.reply(event);
								});
					});
		});
	}
}
