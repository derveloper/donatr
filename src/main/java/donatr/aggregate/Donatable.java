package donatr.aggregate;

import donatr.event.*;
import io.resx.core.Aggregate;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Donatable extends BaseAccount {
	private BigDecimal amount = BigDecimal.ZERO;
	private int timesDonated = 0;

	public void on(final DonatableCreatedEvent event) {
		id = event.getId();
		name = event.getName();
		imageUrl = event.getImageUrl();
		amount = event.getAmount();
	}

	public void on(final DonatableAmountUpdatedEvent event) {
		amount = event.getAmount();
	}

	@Override
	public void on(AccountCreditedEvent event) {
		super.on(event);
		timesDonated++;
	}
}
