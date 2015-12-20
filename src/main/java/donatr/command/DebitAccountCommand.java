package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.DEBIT_ACCOUNT_COMMAND_ADDRESS;

@Getter
@Setter
public class DebitAccountCommand extends Command {
	private String id;
	private BigDecimal amount;

	public DebitAccountCommand() {
		super(DEBIT_ACCOUNT_COMMAND_ADDRESS);
	}
}
