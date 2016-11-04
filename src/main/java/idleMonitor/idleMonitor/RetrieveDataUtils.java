package idleMonitor.idleMonitor;

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
	
	ParsingUtils parsing = new ParsingUtils();
	
    @CheckForNull
    final Jenkins jenkins = Jenkins.getInstance();
    
	// can't be null for tests, execute method changes to real instance url
	String url = "http://localhost:8080/";
	
	/*
	 * parses instance's exposed data at {JENKINS}/api
	 * to check for current activity
	 */
	public long getBusyExecutors() {
		
		if (jenkins != null) { url = jenkins.getRootUrl(); };
		
    	Client client = ClientBuilder.newClient();
    	String destUrl = url + "/api/json?depth=1";
    	WebTarget routing = client.target(destUrl);
    	final Response response = routing.request().accept(MediaType.APPLICATION_JSON).get();
    	final InputStream stream = response.readEntity(InputStream.class);
    	
    	long busyExecutors = 0;
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, Charset.defaultCharset()));
    	JSONParser parser = new JSONParser();
        
    	String inputLine;
	    try {
	    	while ((inputLine = in.readLine()) != null) {    
	    		JSONObject output = (JSONObject) parser.parse(inputLine);
			  	return parsing.parseJenkinsData(output);        		
			  }
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	    
    	client.close();
		
    	return busyExecutors;
	}
	
	
	
	/*
	 * checks exposed data from Monitoring plugin at {JENKINS}/monitoring
	 * for last time UI was hit
	 */
	public Date getLatestHit() {
		
		if (jenkins != null) { url = jenkins.getRootUrl(); };
				
		Client client = ClientBuilder.newClient();
    	String destUrl = url + "monitoring?format=json&period=tout";
    	WebTarget routing = client.target(destUrl);
    	final Response response = routing.request().accept(MediaType.APPLICATION_JSON).get();
    	final InputStream stream = response.readEntity(InputStream.class);
		
    	BufferedReader in = new BufferedReader(new InputStreamReader(stream, Charset.defaultCharset()));
		
     	StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = in.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        
        String fullLine = sb.toString();
        JSONParser parser = new JSONParser();
        JSONObject output;
		try {
			output = (JSONObject) parser.parse(fullLine);
	        return parsing.parseMonitoringData(output);
		} catch (ParseException e) {
			e.printStackTrace();
		}     	
     
		return null;
	}
	
    

}
