package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.DEPOSIT_ACCOUNT_COMMAND_ADDRESS;

@Getter
@Setter
public class DepositAccountCommand extends Command {
	private String id;
	private BigDecimal amount;

	public DepositAccountCommand() {
		super(DEPOSIT_ACCOUNT_COMMAND_ADDRESS);
	}
}
