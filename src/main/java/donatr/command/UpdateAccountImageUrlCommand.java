package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import static donatr.Constants.UPDATE_ACCOUNT_IMAGEURL_COMMAND_ADDRESS;

@Getter
@Setter
public class UpdateAccountImageUrlCommand extends Command {
	private String id;
	private String imageUrl;

	public UpdateAccountImageUrlCommand() {
		super(UPDATE_ACCOUNT_IMAGEURL_COMMAND_ADDRESS);
	}
}
