package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.UPDATE_ACCOUNT_NAME_COMMAND_ADDRESS;

@Getter
@Setter
public class UpdateAccountNameCommand extends Command {
	private String id;
	private String name;

	public UpdateAccountNameCommand() {
		super(UPDATE_ACCOUNT_NAME_COMMAND_ADDRESS);
	}
}
