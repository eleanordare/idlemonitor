package idleMonitor.idleMonitor;

import hudson.Extension;
import hudson.model.AsyncPeriodicWork;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;

import org.joda.time.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.annotation.CheckForNull;
import javax.xml.bind.DatatypeConverter;

/**
 * Performs checks periodically to see if instance should be up and running
 */
@Extension
public class PeriodicCheck extends AsyncPeriodicWork {
	
    public PeriodicCheck() {
        super("PeriodicCheck");
    }

    @CheckForNull
    final static Jenkins jenkins = Jenkins.getInstance();
    final static String username = "admin";
	final static String password = "admin";
	final static Setup setup = new Setup();
	
	// can't be null for tests, execute method changes to real instance url
	static String url = "http://localhost:8080/";
	
	
	/*
	 * parses instance's exposed data at {JENKINS}/api
	 * to check for current activity
	 */
	public static long getBusyExecutors() {
			
		System.setProperty("file.encoding", "UTF-8");
		
		BufferedReader in = null;
		JSONParser parser = new JSONParser();
		long busyExecutors = 0;
		
		try {
            URL jsonURL = new URL(url + "api/json?depth=1"); // URL to Parse
            URLConnection yc = jsonURL.openConnection();
            String header = "Basic " + new String(DatatypeConverter.parseBase64Binary(username + ":" + password), Charset.defaultCharset());
            yc.addRequestProperty("Authorization", header);
            in = new BufferedReader(new InputStreamReader(yc.getInputStream(), Charset.defaultCharset()));
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {    
            	JSONObject output = (JSONObject) parser.parse(inputLine);
	            return parseJenkinsData(output);           		
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
	
	
	public static long parseJenkinsData(JSONObject input) {
		JSONArray assignedLabels = (JSONArray) input.get("assignedLabels");
        JSONObject assignedLabelsObj = (JSONObject) assignedLabels.get(0);
        long busyExecutors = (long) assignedLabelsObj.get("busyExecutors");
        
        return busyExecutors;  
	}

	
	
	/*
	 * checks exposed data from Monitoring plugin at {JENKINS}/monitoring
	 * for last time UI was hit
	 */
	public static Date getLatestHit() {
		
		System.setProperty("file.encoding", "UTF-8");
		
		BufferedReader in = null;
		JSONParser parser = new JSONParser();
		
		try {         
            URL jsonURL = new URL(url + "monitoring?format=json&period=tout"); // URL to Parse
            URLConnection yc = jsonURL.openConnection();
            String header = "Basic " + new String(DatatypeConverter.parseBase64Binary(username + ":" + password), Charset.defaultCharset());
            yc.addRequestProperty("Authorization", header);
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
            	            
        	return parseMonitoringData(output);

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
	
	
	public static Date parseMonitoringData(JSONObject input) {
		ArrayList<Date> dates = new ArrayList<Date>();
        JSONArray list = (JSONArray) input.get("list");
        for (Object o : list) {
        	JSONObject out = (JSONObject) o;
        	String lineDate = (String) out.get("startDate");
        	String inputDate = lineDate.split(" ")[0];
        	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        	Date finalDate = null;
			try {
				finalDate = dateFormat.parse(inputDate);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
        	dates.add(finalDate);
        }
        
        return Collections.max(dates);
	}
	
	
	
	/*
	 * compares timeout period specified in Setup with last time UI was hit
	 * and checks if busy executors is 0, calls shutdown script if necessary
	 */
	public static boolean checkStatus(long busyExecutors, DateTime latest, Period period) {
		
		// check if latest hit is before time limit
		if ((latest.isBefore(new DateTime(new DateTime().minus(period)))) && busyExecutors == 0) {
			System.out.println("sleepy time");
			setup.shutdownJenkins();
			return false;
		}
		else {
			System.out.println("stay awake");
		}
		
		return true;
	}
	
	
    
    @Override
    protected void execute(TaskListener taskListener) throws IOException {

    	if (jenkins != null) { url = jenkins.getRootUrl(); };
    	
		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication (username, password.toCharArray());
		    }
		});
    	
    	System.out.println("---------------------------------");
    	
    	long busyExecutors = getBusyExecutors();
    	Period period = setup.getTimeoutPeriod();
    	DateTime latest = new DateTime(getLatestHit());
    	
    	checkStatus(busyExecutors, latest, period);
    	
    	System.out.println("---------------------------------");
	
    }

    /*
     * specified in Setup, execute method is run this often
     */
    @Override
    public long getRecurrencePeriod() {
        return setup.getPollingInterval();
    }
    
}