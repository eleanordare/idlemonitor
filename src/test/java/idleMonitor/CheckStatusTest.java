package idleMonitor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import idleMonitor.idleMonitor.CheckStatus;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Test;


public class CheckStatusTest {
	
	CheckStatus check = new CheckStatus();
	
	@Test
	public void testFalse() {
		
		long busyExecutors = 0;
    	Period period = Period.days(7);
    	DateTime latest = new DateTime(new DateTime().minusDays(10));
    	
    	assertFalse(check.checkStatus(busyExecutors, latest, period));
		
	}
	
	@Test
	public void testTrue1() {
		
		long busyExecutors = 5;
    	Period period = Period.days(7);
    	DateTime latest = new DateTime(new DateTime().minusDays(10));
    	
    	assertTrue(check.checkStatus(busyExecutors, latest, period));
		
	}
	
	@Test
	public void testTrue2() {
		
		long busyExecutors = 0;
    	Period period = Period.days(7);
    	DateTime latest = new DateTime(new DateTime().minusDays(5));
    	
    	assertTrue(check.checkStatus(busyExecutors, latest, period));
    	
	}
	
	@Test
	public void testEdgeTrue() {
		
		long busyExecutors = 3;
    	Period period = Period.seconds(1);
    	DateTime latest = new DateTime(new DateTime().minusSeconds(1));
    	
    	assertTrue(check.checkStatus(busyExecutors, latest, period));
    	
	}
	
	@Test
	public void testEdgeFalse() {
		
		long busyExecutors = 0;
    	Period period = Period.seconds(1);
    	DateTime latest = new DateTime(new DateTime().minusSeconds(2));
    	
    	assertFalse(check.checkStatus(busyExecutors, latest, period));
    	
	}
	

}
