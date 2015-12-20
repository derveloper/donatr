package donatr;

public final class Constants {
	public static final String CREATE_ACCOUNT_COMMAND_ADDRESS = "create.account.command";
	public static final String CREATE_FIXEDAMOUNTACCOUNT_COMMAND_ADDRESS = "create.fixedamountaccount.command";
	public static final String DEBIT_ACCOUNT_COMMAND_ADDRESS = "debit.account.command";
	public static final String CREDIT_ACCOUNT_COMMAND_ADDRESS = "credit.account.command";

	public static final String CREATE_TRANSACTION_COMMAND_ADDRESS = "create.transaction.command";

	public static final String ACCOUNT_CREATED_EVENT_ADDRESS = "created.account.event";
	public static final String FIXEDAMOUNTACCOUNT_CREATED_EVENT_ADDRESS = "created.fixedamountaccount.event";
	public static final String ACCOUNT_DEBITED_EVENT_ADDRESS = "debited.account.event";
	public static final String ACCOUNT_CREDITED_EVENT_ADDRESS = "credited.account.event";

	public static final String TRANSACTION_CREATED_EVENT_ADDRESS = "created.transaction.event";

	public static final String UPDATE_DASHBOARD_EVENT_ADDRESS = "updated.dashboard.event";
}
