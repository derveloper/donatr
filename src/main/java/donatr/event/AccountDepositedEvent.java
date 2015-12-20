package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.ACCOUNT_DEPOSITED_EVENT_ADDRESS;

@Getter
@Setter
public class AccountDepositedEvent extends SourcedEvent {
	private BigDecimal amount;

	public AccountDepositedEvent() {
		this(null, null);
	}

	public AccountDepositedEvent(String id, BigDecimal amount) {
		super(ACCOUNT_DEPOSITED_EVENT_ADDRESS, id);
		this.amount = amount;
	}
}
