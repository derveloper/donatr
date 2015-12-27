package donatr.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.DONATABLE_NAME_UPDATED_EVENT_ADDRESS;

@Getter
@Setter
public class DonatableNameUpdatedEvent extends SourcedEvent {
	private String name;

	public DonatableNameUpdatedEvent() {
		this(null, null);
	}

	public DonatableNameUpdatedEvent(final String id, final String name) {
		super(DONATABLE_NAME_UPDATED_EVENT_ADDRESS, id);
		this.name = name;
	}
}
