package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.ACCOUNT_DEBITED_EVENT_ADDRESS;

@Getter
@Setter
public class AccountDebitedEvent extends SourcedEvent {
	private BigDecimal amount;

	public AccountDebitedEvent() {
		this(null, null);
	}

	public AccountDebitedEvent(String id, BigDecimal amount) {
		super(ACCOUNT_DEBITED_EVENT_ADDRESS, id);
		this.amount = amount;
	}
}
