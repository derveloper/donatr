package donatr.aggregate;

import donatr.event.AccountCreatedEvent;
import donatr.event.AccountCreditedEvent;
import donatr.event.AccountDebitedEvent;
import io.resx.core.Aggregate;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Account extends Aggregate {
	private String id;
	private String name;
	private String email;
	private String imageUrl;
	private BigDecimal balance = BigDecimal.ZERO;

	public void on(final AccountCreatedEvent event) {
		id = event.getId();
		name = event.getName();
		email = event.getEmail();
	}

	public void on(final AccountCreditedEvent event) {
		balance = balance.add(event.getAmount());
	}

	public void on(final AccountDebitedEvent event) {
		balance = balance.subtract(event.getAmount());
	}
}
