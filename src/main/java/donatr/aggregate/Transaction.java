package donatr.aggregate;

import donatr.event.TransactionCreatedEvent;
import io.resx.core.Aggregate;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Transaction extends Aggregate {
	private String id;
	private String accountFrom;
	private String accountTo;
	private BigDecimal amount = BigDecimal.ZERO;

	public void on(final TransactionCreatedEvent event) {
		id = event.getId();
		amount = event.getAmount();
	}
}
