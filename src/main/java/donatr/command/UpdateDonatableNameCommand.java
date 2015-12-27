package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.UPDATE_DONATABLE_NAME_COMMAND_ADDRESS;

@Getter
@Setter
public class UpdateDonatableNameCommand extends Command {
	private String id;
	private String name;

	public UpdateDonatableNameCommand() {
		super(UPDATE_DONATABLE_NAME_COMMAND_ADDRESS);
	}
}
