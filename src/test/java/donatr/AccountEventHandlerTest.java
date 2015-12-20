package donatr;

import donatr.aggregate.Account;
import donatr.event.AccountCreatedEvent;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AccountEventHandlerTest {
	@BeforeClass
	public static void beforeClass() throws InterruptedException {
		new DonatrMain().run(Vertx.vertx());
		Thread.sleep(1000);
	}

	@Test
	public void testCreateAccount() throws Exception {
		String accountName = "foobar";
		HttpResponse execute = createAccount(accountName);
		assertCreateAccount(execute, accountName);
	}

	private void assertCreateAccount(HttpResponse execute, String name) throws IOException {
		assertThat(execute.getStatusLine().getStatusCode(), is(200));
		assertThat(IOUtils.toString(execute.getEntity().getContent()),
				containsString(name));
	}

	private HttpResponse createAccount(String username) throws IOException {
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("name", username));
		return post("/api/account/", parameters);
	}

	@Test
	public void testGetDashboardAggregate() throws Exception {
			String name = UUID.randomUUID().toString();
		final HttpResponse account = createAccount(name);
		final String accountJson = IOUtils.toString(account.getEntity().getContent());
		final AccountCreatedEvent decodeValue = Json.decodeValue(accountJson, AccountCreatedEvent.class);
		HttpResponse execute = get("/api/aggregate/account/" + decodeValue.getId());
		String actual = IOUtils.toString(execute.getEntity().getContent());
		assertThat(actual, execute.getStatusLine().getStatusCode(), is(200));
		final JsonObject jsonObject = new JsonObject(actual);
		assertThat(actual, jsonObject.getString("id"), is(decodeValue.getId()));
		assertThat(actual, jsonObject.getInteger("balance"), is(0));
	}

	private HttpResponse post(String path, List<NameValuePair> params) throws IOException {
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost("http://localhost:8080" + path);
		httpPost.setEntity(new UrlEncodedFormEntity(params));
		return httpclient.execute(httpPost);
	}

	private HttpResponse get(String path) throws IOException {
		HttpClient httpclient = HttpClientBuilder.create().build();
		String uri = "http://localhost:8080" + path;
		HttpGet httpGet = new HttpGet(uri);
		return httpclient.execute(httpGet);
	}
}