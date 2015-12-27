package donatr.command;

import io.resx.core.command.Command;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static donatr.Constants.UPDATE_DONATABLE_AMOUNT_COMMAND_ADDRESS;

@Getter
@Setter
public class UpdateDonatableAmountCommand extends Command {
	private String id;
	private BigDecimal amount;

	public UpdateDonatableAmountCommand() {
		super(UPDATE_DONATABLE_AMOUNT_COMMAND_ADDRESS);
	}
}
