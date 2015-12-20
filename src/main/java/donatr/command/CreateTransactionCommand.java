package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.CREATE_TRANSACTION_COMMAND_ADDRESS;

@Getter
@Setter
public class CreateTransactionCommand extends Command {
	private String id;
	private BigDecimal amount;
	private String accountFrom;
	private String accountTo;

	public CreateTransactionCommand() {
		super(CREATE_TRANSACTION_COMMAND_ADDRESS);
	}
}
