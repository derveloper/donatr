package donatr;

import donatr.command.CreateFixedAmountAccountCommand;
import donatr.command.CreateTransactionCommand;
import donatr.command.DebitAccountCommand;
import donatr.command.CreditAccountCommand;
import donatr.event.AccountCreatedEvent;
import donatr.event.FixedAmountAccountCreatedEvent;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class CreateAccountCommandHandlerTest {
	@BeforeClass
	public static void beforeClass() throws InterruptedException {
		new DonatrMain().run(Vertx.vertx());
		Thread.sleep(2000);
	}

	@Test
	public void testCreditAccount() throws Exception {
		String accountName = "foobar";
		HttpResponse account = createAccount(accountName);
		final AccountCreatedEvent accountCreatedEvent = Json.decodeValue(responseString(account), AccountCreatedEvent.class);
		HttpResponse credit = creditAccount(accountCreatedEvent.getId(), 13.37);
		assertThat(credit.getStatusLine().getStatusCode(), is(200));
		assertGetAccount(BigDecimal.valueOf(-13.37), accountCreatedEvent.getId());
	}

	@Test
	public void testCreateAccount() throws Exception {
		String accountName = "foobar";
		HttpResponse execute = createAccount(accountName);
		assertCreateAccount(execute, accountName);
	}

	@Test
	public void testDepositAccount() throws Exception {
		String accountName = "foobar";
		HttpResponse account = createAccount(accountName);
		final AccountCreatedEvent accountCreatedEvent = Json.decodeValue(responseString(account), AccountCreatedEvent.class);
		HttpResponse deposit = depositAccount(accountCreatedEvent.getId(), 13.37);
		assertThat(deposit.getStatusLine().getStatusCode(), is(200));
		assertGetAccount(BigDecimal.valueOf(13.37), accountCreatedEvent.getId());
	}

	@Test
	public void testCreateTransaction() throws Exception {
		String accountName = "foobar";
		HttpResponse accountFrom = createAccount(accountName);
		HttpResponse accountTo = createAccount(accountName);
		final AccountCreatedEvent fromAccountEvent = Json.decodeValue(responseString(accountFrom), AccountCreatedEvent.class);
		final AccountCreatedEvent toAccountEvent = Json.decodeValue(responseString(accountTo), AccountCreatedEvent.class);

		HttpResponse transaction = createTransaction(
				fromAccountEvent.getId(),
				toAccountEvent.getId(),
				13.37);

		assertThat(transaction.getStatusLine().getStatusCode(), is(200));
		assertGetAccount(BigDecimal.valueOf(-13.37), fromAccountEvent.getId());
		assertGetAccount(BigDecimal.valueOf(13.37), toAccountEvent.getId());
	}

	@Test
	public void testCreateFixedAmountDonation() throws Exception {
		HttpResponse transaction = createFixedAmountDonation("mate", 1.5);
		final FixedAmountAccountCreatedEvent fixedAmountAccountCreatedEvent = Json.decodeValue(responseString(transaction), FixedAmountAccountCreatedEvent.class);
		assertThat(transaction.getStatusLine().getStatusCode(), is(200));
		assertThat(fixedAmountAccountCreatedEvent.getAmount(), is(BigDecimal.valueOf(1.5)));
	}

	@Test
	public void testTransactionWithFixedAmountDonation() throws Exception {
		HttpResponse accountTo = createFixedAmountDonation("mate", 1.5);
		final FixedAmountAccountCreatedEvent toAccountEvent = Json.decodeValue(responseString(accountTo), FixedAmountAccountCreatedEvent.class);
		assertThat(accountTo.getStatusLine().getStatusCode(), is(200));
		assertThat(toAccountEvent.getAmount(), is(BigDecimal.valueOf(1.5)));

		String accountName = "foobar";
		HttpResponse accountFrom = createAccount(accountName);
		final AccountCreatedEvent fromAccountEvent = Json.decodeValue(responseString(accountFrom), AccountCreatedEvent.class);

		HttpResponse transaction = createTransactionWithDonation(
				fromAccountEvent.getId(),
				toAccountEvent.getId(),
				13.37);

		assertThat(transaction.getStatusLine().getStatusCode(), is(200));
		assertGetAccount(BigDecimal.valueOf(-1.5), fromAccountEvent.getId());
		assertGetFixedAmountAccount(BigDecimal.valueOf(1.5), toAccountEvent.getId());
	}

	@Test
	public void testLogin() throws Exception {
		HttpResponse execute = login("test", "test");
		assertThat(execute.getStatusLine().getStatusCode(), is(200));
		final String token = responseString(execute);
		assertThat(token.length(), not(0));
	}

	private String responseString(HttpResponse execute) throws IOException {
		return IOUtils.toString(execute.getEntity().getContent());
	}

	private void assertCreateAccount(HttpResponse execute, String name) throws IOException {
		assertThat(execute.getStatusLine().getStatusCode(), is(200));
		assertThat(responseString(execute),
				containsString(name));
	}

	private HttpResponse createAccount(String username) throws IOException {
		final String token = responseString(login("test", "test"));
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("name", username));
		return post("/api/account/", parameters, token);
	}

	private HttpResponse depositAccount(String id, double amount) throws IOException {
		final String token = responseString(login("test", "test"));
		final CreditAccountCommand command = new CreditAccountCommand();
		command.setId(id);
		command.setAmount(BigDecimal.valueOf(amount));
		return postJson("/api/account/credit", Json.encode(command), token);
	}

	private HttpResponse creditAccount(String id, double amount) throws IOException {
		final String token = responseString(login("test", "test"));
		final DebitAccountCommand command = new DebitAccountCommand();
		command.setId(id);
		command.setAmount(BigDecimal.valueOf(amount));
		return postJson("/api/account/debit", Json.encode(command), token);
	}

	private HttpResponse createTransaction(String from, String to, double amount) throws IOException {
		final String token = responseString(login("test", "test"));
		final CreateTransactionCommand command = makeCreateTransactionCommand(from, to, amount);
		return postJson("/api/transaction", Json.encode(command), token);
	}

	private HttpResponse createTransactionWithDonation(String from, String to, double amount) throws IOException {
		final String token = responseString(login("test", "test"));
		final CreateTransactionCommand command = makeCreateTransactionCommand(from, to, amount);
		return postJson("/api/transaction", Json.encode(command), token);
	}

	private CreateTransactionCommand makeCreateTransactionCommand(String from, String to, double amount) {
		final CreateTransactionCommand command = new CreateTransactionCommand();
		command.setAccountFrom(from);
		command.setAccountTo(to);
		command.setAmount(BigDecimal.valueOf(amount));
		return command;
	}

	private HttpResponse createFixedAmountDonation(String name, double amount) throws IOException {
		final String token = responseString(login("test", "test"));
		final CreateFixedAmountAccountCommand command = new CreateFixedAmountAccountCommand();
		command.setName(name);
		command.setAmount(BigDecimal.valueOf(amount));
		return postJson("/api/donation", Json.encode(command), token);
	}

	private HttpResponse login(String username, String password) throws IOException {
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("username", username));
		parameters.add(new BasicNameValuePair("password", password));
		return post("/api/session", parameters);
	}

	@Test
	public void testGetAccountAggregate() throws Exception {
		String name = UUID.randomUUID().toString();
		final HttpResponse account = createAccount(name);
		final String accountJson = responseString(account);
		final AccountCreatedEvent decodeValue = Json.decodeValue(accountJson, AccountCreatedEvent.class);
		final BigDecimal balance = new BigDecimal(0);
		final String id = decodeValue.getId();
		assertGetAccount(balance, id);
	}

	private void assertGetAccount(BigDecimal balance, String id) throws IOException {
		final String token = responseString(login("test", "test"));
		final String path = "/api/aggregate/account/";
		assertAccountAggregate(balance, id, token, path);
	}

	private void assertGetFixedAmountAccount(BigDecimal balance, String id) throws IOException {
		final String token = responseString(login("test", "test"));
		final String path = "/api/aggregate/fixedamountaccount/";
		assertAccountAggregate(balance, id, token, path);
	}

	private void assertAccountAggregate(BigDecimal balance, String id, String token, String path) throws IOException {
		HttpResponse execute = get(path + id, token);
		String actual = responseString(execute);
		assertThat(actual, execute.getStatusLine().getStatusCode(), is(200));
		final JsonObject jsonObject = new JsonObject(actual);
		assertThat(actual, jsonObject.getString("id"), is(id));
		assertThat(actual, jsonObject.getDouble("balance"), is(balance.doubleValue()));
	}

	private HttpResponse post(String path, List<NameValuePair> params, String token) throws IOException {
		return executePost(path, new UrlEncodedFormEntity(params), token);
	}

	private HttpResponse postJson(String path, String json, String token) throws IOException {
		return executePost(path, new StringEntity(json), token);
	}

	private HttpResponse executePost(String path, HttpEntity entity, String token) throws IOException {
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost("http://localhost:8080" + path);
		if (StringUtils.isNotEmpty(token)) httpPost.addHeader("Authorization", "Bearer " + token);
		httpPost.setEntity(entity);
		return httpclient.execute(httpPost);
	}

	private HttpResponse post(String path, List<NameValuePair> params) throws IOException {
		return post(path, params, null);
	}

	private HttpResponse get(String path, String token) throws IOException {
		HttpClient httpclient = HttpClientBuilder.create().build();
		String uri = "http://localhost:8080" + path;
		HttpGet httpGet = new HttpGet(uri);
		if (StringUtils.isNotEmpty(token)) httpGet.addHeader("Authorization", "Bearer " + token);
		return httpclient.execute(httpGet);
	}

	private HttpResponse get(String path) throws IOException {
		return get(path, null);
	}
}