package donatr.aggregate;

import donatr.event.AccountCreditedEvent;
import donatr.event.AccountDebitedEvent;
import donatr.event.AccountImageUrlUpdatedEvent;
import donatr.event.AccountNameUpdatedEvent;
import io.resx.core.Aggregate;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public abstract class BaseAccount extends Aggregate {
	protected String id;
	protected String name;
	protected String imageUrl;
	protected BigDecimal balance = BigDecimal.ZERO;

	public void on(final AccountCreditedEvent event) {
		balance = balance.add(event.getAmount());
	}

	public void on(final AccountDebitedEvent event) {
		balance = balance.subtract(event.getAmount());
	}

	public void on(final AccountImageUrlUpdatedEvent event) {
		imageUrl = event.getImageUrl();
	}

	public void on(final AccountNameUpdatedEvent event) {
		name = event.getName();
	}
}
