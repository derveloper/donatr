package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.CREATE_DONATABLE_COMMAND_ADDRESS;

@Getter
@Setter
public class CreateDonatableCommand extends Command {
	private String id;
	private String name;
	private String imageUrl;
	private BigDecimal amount;

	public CreateDonatableCommand() {
		super(CREATE_DONATABLE_COMMAND_ADDRESS);
	}
}
