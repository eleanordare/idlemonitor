package dataUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;

import javax.annotation.CheckForNull;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jenkins.model.Jenkins;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RetrieveDataUtils {

	public RetrieveDataUtils() {
		parsing = new ParsingUtils();
	}

	private ParsingUtils parsing;

	@CheckForNull
	final Jenkins jenkins = Jenkins.getInstance();

	// can't be null for tests, execute method changes to real instance url
	String url = "http://localhost:8080/";

	/*
	 * parses instance's exposed data at {JENKINS}/api to check for current
	 * activity
	 */
	public long getBusyExecutors() throws Exception {

		if (jenkins != null) {
			url = jenkins.getRootUrl();
		}
		;

		Client client = ClientBuilder.newClient();
		String destUrl = url + "/api/json?depth=1";
		WebTarget routing = client.target(destUrl);
		Response response = routing.request()
				.accept(MediaType.APPLICATION_JSON).get();
		String out = response.readEntity(String.class);
		client.close();
		
		JSONParser parser = new JSONParser();
		JSONObject output = (JSONObject) parser.parse(out);
		return parsing.parseJenkinsData(output);

	}

	/*
	 * checks exposed data from Monitoring plugin at {JENKINS}/monitoring for
	 * last time UI was hit
	 */
	public Date getLatestHit() throws ParseException, java.text.ParseException {

		if (jenkins != null) {
			url = jenkins.getRootUrl();
		}
		;

		Client client = ClientBuilder.newClient();
		String destUrl = url + "monitoring?format=json&period=tout";
		WebTarget routing = client.target(destUrl);
		Response response = routing.request()
				.accept(MediaType.APPLICATION_JSON).get();
		String out = response.readEntity(String.class);

		JSONParser parser = new JSONParser();
		JSONObject output = (JSONObject) parser.parse(out);
		return parsing.parseMonitoringData(output);
	}

}
