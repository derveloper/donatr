package donatr;

public final class Constants {
	/*
	* Account commands
	*/
	public static final String CREATE_ACCOUNT_COMMAND_ADDRESS = "create.account.command";
	public static final String DELETE_ACCOUNT_COMMAND_ADDRESS = "delete.account.command";
	public static final String DEBIT_ACCOUNT_COMMAND_ADDRESS = "debit.account.command";
	public static final String CREDIT_ACCOUNT_COMMAND_ADDRESS = "credit.account.command";
	public static final String UPDATE_ACCOUNT_NAME_COMMAND_ADDRESS = "update.account.name.command";
	public static final String UPDATE_ACCOUNT_EMAIL_COMMAND_ADDRESS = "update.account.email.command";
	public static final String UPDATE_ACCOUNT_IMAGEURL_COMMAND_ADDRESS = "update.account.imageurl.command";

	/*
	* Donatable commands
	*/
	public static final String CREATE_DONATABLE_COMMAND_ADDRESS = "create.donatable.command";
	public static final String UPDATE_DONATABLE_AMOUNT_COMMAND_ADDRESS = "update.donatable.amount.command";

	/*
	* Transaction commands
	*/
	public static final String CREATE_TRANSACTION_COMMAND_ADDRESS = "create.transaction.command";

	/*
	* Account events
	*/
	public static final String ACCOUNT_CREATED_EVENT_ADDRESS = "created.account.event";
	public static final String ACCOUNT_DELETED_EVENT_ADDRESS = "deleted.account.event";
	public static final String ACCOUNT_DEBITED_EVENT_ADDRESS = "debited.account.event";
	public static final String ACCOUNT_CREDITED_EVENT_ADDRESS = "credited.account.event";
	public static final String ACCOUNT_NAME_UPDATED_EVENT_ADDRESS = "updated.account.name.event";
	public static final String ACCOUNT_EMAIL_UPDATED_EVENT_ADDRESS = "updated.account.email.event";
	public static final String ACCOUNT_IMAGEURL_UPDATED_EVENT_ADDRESS = "updated.account.imageurl.event";

	/*
	* Donatable events
	*/
	public static final String DONATABLE_CREATED_EVENT_ADDRESS = "created.donatable.event";
	public static final String DONATABLE_AMOUNT_UPDATED_EVENT_ADDRESS = "updated.donatable.amount.event";

	/*
	* Transaction events
	*/
	public static final String TRANSACTION_CREATED_EVENT_ADDRESS = "created.transaction.event";

	/*
	* Misc events
	*/
	public static final String UPDATE_DASHBOARD_EVENT_ADDRESS = "updated.dashboard.event";
}
