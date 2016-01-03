package donatr.aggregate;

import donatr.event.AccountCreatedEvent;
import donatr.event.AccountEmailUpdatedEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account extends BaseAccount {
	private String email;

	public void on(final AccountCreatedEvent event) {
		id = event.getId();
		name = event.getName();
		email = event.getEmail();
	}

	public void on(final AccountEmailUpdatedEvent event) {
		email = event.getEmail();
	}
}
