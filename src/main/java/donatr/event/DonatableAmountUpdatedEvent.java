package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.DONATABLE_AMOUNT_UPDATED_EVENT_ADDRESS;

@Getter
@Setter
public class DonatableAmountUpdatedEvent extends SourcedEvent {
	private BigDecimal amount;

	public DonatableAmountUpdatedEvent() {
		this(null, null);
	}

	public DonatableAmountUpdatedEvent(final String id, final BigDecimal amount) {
		super(DONATABLE_AMOUNT_UPDATED_EVENT_ADDRESS, id);
		this.amount = amount;
	}
}
