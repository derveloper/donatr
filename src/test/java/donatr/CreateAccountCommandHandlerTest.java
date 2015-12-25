package donatr;

import donatr.command.*;
import donatr.event.AccountCreatedEvent;
import donatr.event.DonatableCreatedEvent;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CreateAccountCommandHandlerTest {
	@BeforeClass
	public static void beforeClass() throws InterruptedException {
		new DonatrMain().run(Vertx.vertx());
		Thread.sleep(2500);
	}

	@Test
	public void testCreditAccount() throws Exception {
		final String accountName = "foobar";
		final HttpResponse account = createAccount(accountName);
		final AccountCreatedEvent accountCreatedEvent = Json.decodeValue(responseString(account), AccountCreatedEvent.class);
		final HttpResponse credit = creditAccount(accountCreatedEvent.getId(), 13.37);
		assertThat(credit.getStatusLine().getStatusCode(), is(200));
		assertGetAccount(BigDecimal.valueOf(-13.37), accountCreatedEvent.getId());
	}

	@Test
	public void testCreateAccount() throws Exception {
		final String accountName = "foobar";
		final HttpResponse execute = createAccount(accountName);
		assertCreateAccount(execute, accountName);
	}

	@Test
	public void testDepositAccount() throws Exception {
		final String accountName = "foobar";
		final HttpResponse account = createAccount(accountName);
		final AccountCreatedEvent accountCreatedEvent = Json.decodeValue(responseString(account), AccountCreatedEvent.class);
		final HttpResponse deposit = depositAccount(accountCreatedEvent.getId(), 13.37);
		assertThat(deposit.getStatusLine().getStatusCode(), is(200));
		assertGetAccount(BigDecimal.valueOf(13.37), accountCreatedEvent.getId());
	}

	@Test
	public void testCreateTransaction() throws Exception {
		final String accountName = "foobar";
		final HttpResponse accountFrom = createAccount(accountName);
		final HttpResponse accountTo = createAccount(accountName);
		final AccountCreatedEvent fromAccountEvent = Json.decodeValue(responseString(accountFrom), AccountCreatedEvent.class);
		final AccountCreatedEvent toAccountEvent = Json.decodeValue(responseString(accountTo), AccountCreatedEvent.class);

		final HttpResponse transaction = createTransaction(
				fromAccountEvent.getId(),
				toAccountEvent.getId(),
				13.37);

		assertThat(transaction.getStatusLine().getStatusCode(), is(200));
		assertGetAccount(BigDecimal.valueOf(-13.37), fromAccountEvent.getId());
		assertGetAccount(BigDecimal.valueOf(13.37), toAccountEvent.getId());
	}

	@Test
	public void testCreateFixedAmountDonation() throws Exception {
		final HttpResponse transaction = createFixedAmountDonation("mate", 1.5);
		final DonatableCreatedEvent donatableCreatedEvent = Json.decodeValue(responseString(transaction), DonatableCreatedEvent.class);
		assertThat(transaction.getStatusLine().getStatusCode(), is(200));
		assertThat(donatableCreatedEvent.getAmount(), is(BigDecimal.valueOf(1.5).setScale(2, BigDecimal.ROUND_HALF_UP)));
	}

	@Test
	public void testTransactionWithFixedAmountDonation() throws Exception {
		final SecureRandom rng = new SecureRandom();
		for (int t = 0; t < 3; t++) {
			final double amount = BigDecimal.valueOf(rng.nextDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			final HttpResponse accountTo = createFixedAmountDonation("mate", amount);
			final DonatableCreatedEvent toAccountEvent = Json.decodeValue(responseString(accountTo), DonatableCreatedEvent.class);
			assertThat(accountTo.getStatusLine().getStatusCode(), is(200));
			assertThat(toAccountEvent.getAmount(), is(BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP)));

			final String accountName = "foobar";
			final HttpResponse accountFrom = createAccount(accountName);
			final AccountCreatedEvent fromAccountEvent = Json.decodeValue(responseString(accountFrom), AccountCreatedEvent.class);

			final int transactionCount = 3;
			for (int i = 0; i < transactionCount; i++) {
				final HttpResponse transaction = createTransactionWithDonation(
						fromAccountEvent.getId(),
						toAccountEvent.getId(),
						13.37);

				assertThat(transaction.getStatusLine().getStatusCode(), is(200));
			}

			assertGetAccount(BigDecimal.valueOf(-(amount * transactionCount)), fromAccountEvent.getId());
			assertGetFixedAmountAccount(BigDecimal.valueOf((amount * transactionCount)), toAccountEvent.getId());
		}
	}

	@Test
	public void testLogin() throws Exception {
		final HttpResponse execute = login("test", "test");
		assertThat(execute.getStatusLine().getStatusCode(), is(200));
		final String token = responseString(execute);
		assertThat(token.length(), not(0));
	}

	@Test
	public void testGetAccountAggregate() throws Exception {
		final String name = UUID.randomUUID().toString();
		final HttpResponse account = createAccount(name);
		final String accountJson = responseString(account);
		final AccountCreatedEvent decodeValue = Json.decodeValue(accountJson, AccountCreatedEvent.class);
		final BigDecimal balance = new BigDecimal(0);
		final String id = decodeValue.getId();
		assertGetAccount(balance, id);
	}

	@Test
	public void testGetAccountList() throws IOException {
		final String username = UUID.randomUUID().toString();
		final AccountCreatedEvent account = Json.decodeValue(responseString(createAccount(username)), AccountCreatedEvent.class);
		final HttpResponse response = get("/api/aggregate/account", responseString(login("test", "test")));
		final JsonObject accounts = new JsonObject(responseString(response));
		final JsonArray jsonArray = accounts.getJsonArray("accounts");
		assertThat(jsonArray.size(), not(0));

		assertTrue(jsonArray.stream()
				.map(o -> (JsonObject) o)
				.anyMatch(entries -> account.getName().equals(entries.getString("name"))));
	}

	private String responseString(final HttpResponse execute) throws IOException {
		return IOUtils.toString(execute.getEntity().getContent());
	}

	private void assertCreateAccount(final HttpResponse execute, final String name) throws IOException {
		assertThat(execute.getStatusLine().getStatusCode(), is(200));
		assertThat(responseString(execute),
				containsString(name));
	}

	private HttpResponse createAccount(final String username) throws IOException {
		final String token = responseString(login("test", "test"));
		final CreateAccountCommand command = new CreateAccountCommand();
		command.setName(username);
		return postJson("/api/account/", Json.encode(command), token);
	}

	private HttpResponse depositAccount(final String id, final double amount) throws IOException {
		final String token = responseString(login("test", "test"));
		final CreditAccountCommand command = new CreditAccountCommand();
		command.setId(id);
		command.setAmount(BigDecimal.valueOf(amount));
		return postJson("/api/account/credit", Json.encode(command), token);
	}

	private HttpResponse creditAccount(final String id, final double amount) throws IOException {
		final String token = responseString(login("test", "test"));
		final DebitAccountCommand command = new DebitAccountCommand();
		command.setId(id);
		command.setAmount(BigDecimal.valueOf(amount));
		return postJson("/api/account/debit", Json.encode(command), token);
	}

	private HttpResponse createTransaction(final String from, final String to, final double amount) throws IOException {
		final String token = responseString(login("test", "test"));
		final CreateTransactionCommand command = makeCreateTransactionCommand(from, to, amount);
		return postJson("/api/transaction", Json.encode(command), token);
	}

	private HttpResponse createTransactionWithDonation(final String from, final String to, final double amount) throws IOException {
		final String token = responseString(login("test", "test"));
		final CreateTransactionCommand command = makeCreateTransactionCommand(from, to, amount);
		return postJson("/api/transaction", Json.encode(command), token);
	}

	private CreateTransactionCommand makeCreateTransactionCommand(final String from, final String to, final double amount) {
		final CreateTransactionCommand command = new CreateTransactionCommand();
		command.setAccountFrom(from);
		command.setAccountTo(to);
		command.setAmount(BigDecimal.valueOf(amount));
		return command;
	}

	private HttpResponse createFixedAmountDonation(final String name, final double amount) throws IOException {
		final String token = responseString(login("test", "test"));
		final CreateDonatableCommand command = new CreateDonatableCommand();
		command.setName(name);
		command.setAmount(BigDecimal.valueOf(amount));
		return postJson("/api/donatable", Json.encode(command), token);
	}

	private HttpResponse login(final String username, final String password) throws IOException {
		final List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("username", username));
		parameters.add(new BasicNameValuePair("password", password));
		return post("/api/session", parameters);
	}

	private void assertGetAccount(final BigDecimal balance, final String id) throws IOException {
		final String token = responseString(login("test", "test"));
		final String path = "/api/aggregate/account/";
		assertAccountAggregate(balance, id, token, path);
	}

	private void assertGetFixedAmountAccount(final BigDecimal balance, final String id) throws IOException {
		final String token = responseString(login("test", "test"));
		final String path = "/api/aggregate/donatable/";
		assertAccountAggregate(balance, id, token, path);
	}

	private void assertAccountAggregate(final BigDecimal balance, final String id, final String token, final String path) throws IOException {
		final HttpResponse execute = get(path + id, token);
		final String actual = responseString(execute);
		assertThat(actual, execute.getStatusLine().getStatusCode(), is(200));
		final JsonObject jsonObject = new JsonObject(actual);
		assertThat(actual, jsonObject.getString("id"), is(id));
		assertThat(actual, jsonObject.getDouble("balance"), is(balance.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
	}

	private HttpResponse post(final String path, final List<NameValuePair> params, final String token) throws IOException {
		return executePost(path, new UrlEncodedFormEntity(params), token);
	}

	private HttpResponse postJson(final String path, final String json, final String token) throws IOException {
		return executePost(path, new StringEntity(json), token);
	}

	private HttpResponse executePost(final String path, final HttpEntity entity, final String token) throws IOException {
		final HttpClient httpclient = HttpClientBuilder.create().build();
		final HttpPost httpPost = new HttpPost("http://localhost:8080" + path);
		if (StringUtils.isNotEmpty(token)) httpPost.addHeader("Authorization", "Bearer " + token);
		httpPost.setEntity(entity);
		return httpclient.execute(httpPost);
	}

	private HttpResponse post(final String path, final List<NameValuePair> params) throws IOException {
		return post(path, params, null);
	}

	private HttpResponse get(final String path, final String token) throws IOException {
		final HttpClient httpclient = HttpClientBuilder.create().build();
		final String uri = "http://localhost:8080" + path;
		final HttpGet httpGet = new HttpGet(uri);
		if (StringUtils.isNotEmpty(token)) httpGet.addHeader("Authorization", "Bearer " + token);
		return httpclient.execute(httpGet);
	}
}