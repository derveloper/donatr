package donatr.aggregate;

import donatr.event.*;
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
	protected boolean deleted = false;

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

	public void on(@SuppressWarnings("UnusedParameters") final AccountDeletedEvent event) {
		deleted = true;
	}
}
