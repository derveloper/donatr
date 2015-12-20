package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.CREATE_ACCOUNT_COMMAND_ADDRESS;

@Getter
@Setter
public class CreateAccountCommand extends Command {
	private String id;
	private String name;

	public CreateAccountCommand() {
		super(CREATE_ACCOUNT_COMMAND_ADDRESS);
	}
}
