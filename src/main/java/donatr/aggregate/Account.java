package donatr.aggregate;

import donatr.event.AccountCreatedEvent;
import donatr.event.AccountDebitedEvent;
import donatr.event.AccountCreditedEvent;
import io.resx.core.Aggregate;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Account extends Aggregate {
	private String id;
	private String name;
	private BigDecimal balance = BigDecimal.ZERO;

	public void on(AccountCreatedEvent event) {
		id = event.getId();
		name = event.getName();
	}

	public void on(AccountCreditedEvent event) {
		balance = balance.add(event.getAmount());
	}

	public void on(AccountDebitedEvent event) {
		balance = balance.subtract(event.getAmount());
	}
}
