package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.UPDATE_DONATABLE_IMAGEURL_COMMAND_ADDRESS;

@Getter
@Setter
public class UpdateDonatableImageUrlCommand extends Command {
	private String id;
	private String imageUrl;

	public UpdateDonatableImageUrlCommand() {
		super(UPDATE_DONATABLE_IMAGEURL_COMMAND_ADDRESS);
	}
}
