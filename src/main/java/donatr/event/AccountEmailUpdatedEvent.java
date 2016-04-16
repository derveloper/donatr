package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.ACCOUNT_EMAIL_UPDATED_EVENT_ADDRESS;

@Getter
@Setter
public class AccountEmailUpdatedEvent extends SourcedEvent {
	private String email;

	public AccountEmailUpdatedEvent() {
		this(null, null);
	}

	public AccountEmailUpdatedEvent(final String id, final String email) {
		super(ACCOUNT_EMAIL_UPDATED_EVENT_ADDRESS, id);
		this.email = email;
	}
}
