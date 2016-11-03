package idleMonitor.idleMonitor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Date;

import javax.annotation.CheckForNull;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
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
    	WebTarget routing = client.target(url + "api/json?depth=1");
    	Response get = routing.request().get();
    	
    	
    	System.out.println("=============================");
    	System.out.println(get.toString());
    	System.out.println("=============================");
    	
    	client.close();
    	
		BufferedReader in = null;
		JSONParser parser = new JSONParser();
		long busyExecutors = 0;
		
		try {
            URL jsonURL = new URL(url + "api/json?depth=1"); // URL to Parse
            URLConnection yc = jsonURL.openConnection();
            in = new BufferedReader(new InputStreamReader(yc.getInputStream(), Charset.defaultCharset()));
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {    
            	JSONObject output = (JSONObject) parser.parse(inputLine);
	            return parsing.parseJenkinsData(output);           		
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
        	e.printStackTrace();
        } finally {
        	if (in != null) {
        		try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
		return busyExecutors;
	}
	
	
	
	/*
	 * checks exposed data from Monitoring plugin at {JENKINS}/monitoring
	 * for last time UI was hit
	 */
	public Date getLatestHit() {
		
		if (jenkins != null) { url = jenkins.getRootUrl(); };
				
		BufferedReader in = null;
		JSONParser parser = new JSONParser();
		
		try {         
            URL jsonURL = new URL(url + "monitoring?format=json&period=tout"); // URL to Parse
            URLConnection yc = jsonURL.openConnection();
            InputStreamReader inStream = new InputStreamReader(yc.getInputStream(), Charset.defaultCharset());
            in = new BufferedReader(inStream);
            
            String inputLine;
            StringBuffer sb = new StringBuffer();
            while ((inputLine = in.readLine()) != null) { 
            	sb.append(inputLine);
            	if ( inputLine.contains(("\n"))) {
            		in.close();
            		break;
            	}
            }
            
            String fullLine = sb.toString();
            JSONObject output = (JSONObject) parser.parse(fullLine);
            	            
        	return parsing.parseMonitoringData(output);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
        	e.printStackTrace();
        } finally {
        	if (in != null) {
        		try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
		}
			
		return null;
	}
	
    

}
