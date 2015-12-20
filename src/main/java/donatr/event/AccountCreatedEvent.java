package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.ACCOUNT_CREATED_EVENT_ADDRESS;

@Getter
@Setter
public class AccountCreatedEvent extends SourcedEvent {
	private String name;

	public AccountCreatedEvent() {
		this(null, null);
	}

	public AccountCreatedEvent(String id, String name) {
		super(ACCOUNT_CREATED_EVENT_ADDRESS, id);
		this.name = name;
	}
}
