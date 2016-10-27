package idleMonitor;

import static org.junit.Assert.*;

import java.util.Date;

import idleMonitor.idleMonitor.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

public class GetLatestHitTest {
	
	@SuppressWarnings("deprecation")
	@Test
	public void test() {		
		
		String str = "{'list': [{'startDate': '2000-01-01 15:06:27.807 UTC'},{'startDate': '1998-10-27 19:23:32.338 UTC'},{'startDate': '2016-10-27 19:23:32.338 UTC'},{'startDate': '2008-06-14 19:23:32.338 UTC'},{'startDate': '2016-10-27 19:23:32.338 UTC'},{'startDate': '2015-12-07 19:23:32.338 UTC'}]}";
				
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject input = (JSONObject) parser.parse(str);
			
			Date output = PeriodicCheck.parseMonitoringData(input);
			
			assertEquals(27, output.getDay());
			assertEquals(10, output.getMonth());
			assertEquals(2016, output.getYear());
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
