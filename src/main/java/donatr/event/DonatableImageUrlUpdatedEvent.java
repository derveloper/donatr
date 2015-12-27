package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.DONATABLE_IMAGEURL_UPDATED_EVENT_ADDRESS;

@Getter
@Setter
public class DonatableImageUrlUpdatedEvent extends SourcedEvent {
	private String imageUrl;

	public DonatableImageUrlUpdatedEvent() {
		this(null, null);
	}

	public DonatableImageUrlUpdatedEvent(final String id, final String imageUrl) {
		super(DONATABLE_IMAGEURL_UPDATED_EVENT_ADDRESS, id);
		this.imageUrl = imageUrl;
	}
}
