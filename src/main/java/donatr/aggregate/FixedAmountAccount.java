package donatr.aggregate;

import donatr.event.AccountCreditedEvent;
import donatr.event.AccountDebitedEvent;
import donatr.event.FixedAmountAccountCreatedEvent;
import io.resx.core.Aggregate;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FixedAmountAccount extends Aggregate {
	private String id;
	private String name;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal balance = BigDecimal.ZERO;

	public void on(final FixedAmountAccountCreatedEvent event) {
		id = event.getId();
		name = event.getName();
		amount = event.getAmount();
	}

	public void on(final AccountCreditedEvent event) {
		balance = balance.add(event.getAmount());
	}

	public void on(final AccountDebitedEvent event) {
		balance = balance.subtract(event.getAmount());
	}
}
