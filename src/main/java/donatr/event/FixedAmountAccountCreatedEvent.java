package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.FIXEDAMOUNTACCOUNT_CREATED_EVENT_ADDRESS;

@Getter
@Setter
public class FixedAmountAccountCreatedEvent extends SourcedEvent {
	private String name;
	private BigDecimal amount;

	public FixedAmountAccountCreatedEvent() {
		this(null, null, null);
	}

	public FixedAmountAccountCreatedEvent(String id, String name, BigDecimal amount) {
		super(FIXEDAMOUNTACCOUNT_CREATED_EVENT_ADDRESS, id);
		this.name = name;
		this.amount = amount;
	}
}
