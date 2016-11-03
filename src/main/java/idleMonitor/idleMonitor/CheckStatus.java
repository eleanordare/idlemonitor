package idleMonitor.idleMonitor;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class CheckStatus {
	
	/*
	 * compares timeout period specified in Setup with last time UI was hit
	 * and checks if busy executors is 0, calls shutdown script if necessary
	 */
	public boolean checkStatus(long busyExecutors, DateTime latest, Period period) {
		
		// check if latest hit is before time limit
		if ((latest.isBefore(new DateTime(new DateTime().minus(period)))) && busyExecutors == 0) {
			return false;
		}
		
		return true;
	}
	
	

}
