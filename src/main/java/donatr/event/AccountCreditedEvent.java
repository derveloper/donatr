package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.ACCOUNT_CREDITED_EVENT_ADDRESS;

@Getter
@Setter
public class AccountCreditedEvent extends SourcedEvent {
	private BigDecimal amount;

	public AccountCreditedEvent() {
		this(null, null);
	}

	public AccountCreditedEvent(String id, BigDecimal amount) {
		super(ACCOUNT_CREDITED_EVENT_ADDRESS, id);
		this.amount = amount;
	}
}
