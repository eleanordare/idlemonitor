package idleMonitor;

import static org.junit.Assert.*;
import idleMonitor.idleMonitor.PeriodicCheck;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Test;

public class CheckStatusTest {
	
	@Test
	public void testFalse() {
		
		long busyExecutors = 0;
    	Period period = Period.days(7);
    	DateTime latest = new DateTime(new DateTime().minusDays(10));
    	
    	assertFalse(PeriodicCheck.checkStatus(busyExecutors, latest, period));
		
	}
	
	public void testTrue1() {
		
		long busyExecutors = 5;
    	Period period = Period.days(7);
    	DateTime latest = new DateTime(new DateTime().minusDays(10));
    	
    	assertTrue(PeriodicCheck.checkStatus(busyExecutors, latest, period));
		
	}
	
	public void testTrue2() {
		
		long busyExecutors = 0;
    	Period period = Period.days(7);
    	DateTime latest = new DateTime(new DateTime().minusDays(5));
    	
    	assertTrue(PeriodicCheck.checkStatus(busyExecutors, latest, period));
    	
	}

}
