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

import javax.xml.bind.DatatypeConverter;

/**
 * Performs checks periodically to see if instance should be up and running
 */
@Extension
public class PeriodicCheck extends AsyncPeriodicWork {
	
    public PeriodicCheck() {
        super("PeriodicCheck");
    }

    final static Jenkins jenkins = Jenkins.getActiveInstance();    
    final static String username = "admin";
	final static String password = "admin";
	final static Setup setup = new Setup();
	
	
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
            URL jsonURL = new URL("http://localhost:8080/jenkins/api/json?depth=1"); // URL to Parse
            URLConnection yc = jsonURL.openConnection();
            String header = "Basic " + new String(DatatypeConverter.parseBase64Binary(username + ":" + password), Charset.defaultCharset());
            yc.addRequestProperty("Authorization", header);
            in = new BufferedReader(new InputStreamReader(yc.getInputStream(), Charset.defaultCharset()));
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {    
            	JSONObject output = (JSONObject) parser.parse(inputLine);
            	
	            JSONArray assignedLabels = (JSONArray) output.get("assignedLabels");
	            JSONObject assignedLabelsObj = (JSONObject) assignedLabels.get(0);
	            busyExecutors = (long) assignedLabelsObj.get("busyExecutors");
	            
	            return busyExecutors;            		
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
	public static Date getLatestHit() {
		
		System.setProperty("file.encoding", "UTF-8");
		
		BufferedReader in = null;
		JSONParser parser = new JSONParser();
		
		try {         
            URL jsonURL = new URL("http://localhost:8080/jenkins/monitoring?format=json&period=tout"); // URL to Parse
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
            	            
        	ArrayList<Date> dates = new ArrayList<Date>();
            JSONArray list = (JSONArray) output.get("list");
            for (Object o : list) {
            	JSONObject out = (JSONObject) o;
            	String lineDate = (String) out.get("startDate");
            	String inputDate = lineDate.split(" ")[0];
            	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            	Date finalDate = dateFormat.parse(inputDate);
            	dates.add(finalDate);
            }
            
            return Collections.max(dates);   

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
        	e.printStackTrace();
        } catch (java.text.ParseException e) {
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
	
	
	/*
	 * compares timeout period specified in Setup with last time UI was hit
	 * and checks if busy executors is 0, calls shutdown script if necessary
	 */
	public static boolean checkStatus() {
		
		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication (username, password.toCharArray());
		    }
		});
		
		Date latest = getLatestHit();
		long busyExecutors = getBusyExecutors();
		
		Period period = setup.getTimeoutPeriod();
		
		Period change = new Period(new DateTime(latest), new DateTime());
		
		if ((change.getSeconds() > period.getSeconds()) && busyExecutors == 0) {
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
        
    	System.out.println("---------------------------------");
    	checkStatus();
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