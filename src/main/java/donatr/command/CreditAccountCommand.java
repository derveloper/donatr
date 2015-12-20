package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.CREDIT_ACCOUNT_COMMAND_ADDRESS;

@Getter
@Setter
public class CreditAccountCommand extends Command {
	private String id;
	private BigDecimal amount;

	public CreditAccountCommand() {
		super(CREDIT_ACCOUNT_COMMAND_ADDRESS);
	}
}
