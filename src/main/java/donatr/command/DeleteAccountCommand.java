package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.CREDIT_ACCOUNT_COMMAND_ADDRESS;
import static donatr.Constants.DELETE_ACCOUNT_COMMAND_ADDRESS;

@Getter
@Setter
public class DeleteAccountCommand extends Command {
	private String id;

	public DeleteAccountCommand() {
		super(DELETE_ACCOUNT_COMMAND_ADDRESS);
	}
}
