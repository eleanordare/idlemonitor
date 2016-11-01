package idleMonitor.idleMonitor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ParsingUtils {

	public long parseJenkinsData(JSONObject input) {
		JSONArray assignedLabels = (JSONArray) input.get("assignedLabels");
        JSONObject assignedLabelsObj = (JSONObject) assignedLabels.get(0);
        long busyExecutors = (long) assignedLabelsObj.get("busyExecutors");
        
        return busyExecutors;  
	}

	
	public Date parseMonitoringData(JSONObject input) {
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
	
	
}
