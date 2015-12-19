package donatr.aggregate;

import io.resx.core.Aggregate;
import lombok.Getter;
import lombok.Setter;
import donatr.event.AccountCreatedEvent;
import donatr.event.AccountDepositedEvent;

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

	public void on(AccountDepositedEvent event) {
		balance = balance.add(event.getAmount());
	}
}
