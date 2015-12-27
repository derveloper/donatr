package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.ACCOUNT_CREATED_EVENT_ADDRESS;

@Getter
@Setter
public class AccountCreatedEvent extends SourcedEvent {
	private String name;
	private String email;

	public AccountCreatedEvent() {
		this(null, null, null);
	}

	public AccountCreatedEvent(final String id, final String name, final String email) {
		super(ACCOUNT_CREATED_EVENT_ADDRESS, id);
		this.name = name;
		this.email = email;
	}
}
