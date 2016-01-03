package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.ACCOUNT_NAME_UPDATED_EVENT_ADDRESS;

@Getter
@Setter
public class AccountNameUpdatedEvent extends SourcedEvent {
	private String name;

	public AccountNameUpdatedEvent() {
		this(null, null);
	}

	public AccountNameUpdatedEvent(final String id, final String name) {
		super(ACCOUNT_NAME_UPDATED_EVENT_ADDRESS, id);
		this.name = name;
	}
}
