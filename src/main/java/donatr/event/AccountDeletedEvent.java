package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.ACCOUNT_CREDITED_EVENT_ADDRESS;
import static donatr.Constants.ACCOUNT_DELETED_EVENT_ADDRESS;

@Getter
@Setter
public class AccountDeletedEvent extends SourcedEvent {
	public AccountDeletedEvent() {
		this(null);
	}

	public AccountDeletedEvent(final String id) {
		super(ACCOUNT_DELETED_EVENT_ADDRESS, id);
	}
}
