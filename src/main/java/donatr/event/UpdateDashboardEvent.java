package donatr.event;

import io.resx.core.event.DistributedEvent;
import lombok.Getter;
import lombok.Setter;
import donatr.aggregate.Account;

import static donatr.Constants.UPDATE_DASHBOARD_EVENT_ADDRESS;

@Getter
@Setter
public class UpdateDashboardEvent extends DistributedEvent {
	private Account account;

	public UpdateDashboardEvent() {
		this(null);
	}

	public UpdateDashboardEvent(Account account) {
		super(UPDATE_DASHBOARD_EVENT_ADDRESS);
		this.account = account;
	}
}
