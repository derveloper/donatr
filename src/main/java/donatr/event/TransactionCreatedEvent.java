package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.TRANSACTION_CREATED_EVENT_ADDRESS;

@Getter
@Setter
public class TransactionCreatedEvent extends SourcedEvent {
	private BigDecimal amount;
	private String accountFrom;
	private String accountTo;

	public TransactionCreatedEvent() {
		super(null, null);
	}

	public TransactionCreatedEvent(final String id, final String accountFrom, final String accountTo, final BigDecimal amount) {
		super(TRANSACTION_CREATED_EVENT_ADDRESS, id);
		this.accountFrom = accountFrom;
		this.accountTo = accountTo;
		this.amount = amount;
	}
}
