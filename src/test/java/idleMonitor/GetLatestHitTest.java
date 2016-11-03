package idleMonitor;

import static org.junit.Assert.assertEquals;
import idleMonitor.idleMonitor.ParsingUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

public class GetLatestHitTest {
	
	ParsingUtils parsing = new ParsingUtils();
	
	@Test
	public void testMaxAndDuplicates() {		
		
		String str = "{'list': [{'startDate': '2000-01-01 15:06:27.807 UTC'},{'startDate': '1998-10-27 19:23:32.338 UTC'},{'startDate': '2016-10-27 19:23:32.338 UTC'},{'startDate': '2008-06-14 19:23:32.338 UTC'},{'startDate': '2016-10-27 19:23:32.338 UTC'},{'startDate': '2015-12-07 19:23:32.338 UTC'}]}";
				
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject input = (JSONObject) parser.parse(str);
			
			Date output = parsing.parseMonitoringData(input);
						
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date compare = sdf.parse("27/10/2016");
			
			assertEquals(compare, output);
			
		} catch (ParseException | java.text.ParseException e) {
			e.printStackTrace();
		}
	}

}
