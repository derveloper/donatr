package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.UPDATE_ACCOUNT_EMAIL_COMMAND_ADDRESS;

@Getter
@Setter
public class UpdateAccountEmailCommand extends Command {
	private String id;
	private String email;

	public UpdateAccountEmailCommand() {
		super(UPDATE_ACCOUNT_EMAIL_COMMAND_ADDRESS);
	}
}
