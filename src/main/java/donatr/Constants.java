package donatr;

public final class Constants {
	public static final String CREATE_ACCOUNT_COMMAND_ADDRESS = "create.account.command";
	public static final String CREATE_DONATABLE_COMMAND_ADDRESS = "create.donatable.command";
	public static final String DEBIT_ACCOUNT_COMMAND_ADDRESS = "debit.account.command";
	public static final String CREDIT_ACCOUNT_COMMAND_ADDRESS = "credit.account.command";
	public static final String UPDATE_DONATABLE_AMOUNT_COMMAND_ADDRESS = "update.donatable.amount.command";
	public static final String UPDATE_DONATABLE_NAME_COMMAND_ADDRESS = "update.donatable.name.command";
	public static final String UPDATE_DONATABLE_IMAGEURL_COMMAND_ADDRESS = "update.donatable.imageurl.command";

	public static final String CREATE_TRANSACTION_COMMAND_ADDRESS = "create.transaction.command";

	public static final String ACCOUNT_CREATED_EVENT_ADDRESS = "created.account.event";
	public static final String DONATABLE_CREATED_EVENT_ADDRESS = "created.donatable.event";
	public static final String ACCOUNT_DEBITED_EVENT_ADDRESS = "debited.account.event";
	public static final String ACCOUNT_CREDITED_EVENT_ADDRESS = "credited.account.event";
	public static final String DONATABLE_AMOUNT_UPDATED_EVENT_ADDRESS = "updated.donatable.amount.event";
	public static final String DONATABLE_NAME_UPDATED_EVENT_ADDRESS = "updated.donatable.name.event";
	public static final String DONATABLE_IMAGEURL_UPDATED_EVENT_ADDRESS = "updated.donatable.imageurl.event";

	public static final String TRANSACTION_CREATED_EVENT_ADDRESS = "created.transaction.event";

	public static final String UPDATE_DASHBOARD_EVENT_ADDRESS = "updated.dashboard.event";
}
