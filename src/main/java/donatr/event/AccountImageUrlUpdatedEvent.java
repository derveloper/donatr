package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.ACCOUNT_IMAGEURL_UPDATED_EVENT_ADDRESS;

@Getter
@Setter
public class AccountImageUrlUpdatedEvent extends SourcedEvent {
	private String imageUrl;

	public AccountImageUrlUpdatedEvent() {
		this(null, null);
	}

	public AccountImageUrlUpdatedEvent(final String id, final String imageUrl) {
		super(ACCOUNT_IMAGEURL_UPDATED_EVENT_ADDRESS, id);
		this.imageUrl = imageUrl;
	}
}
