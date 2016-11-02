package idleMonitor.idleMonitor;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class CheckStatus {
	
	/*
	 * compares timeout period specified in Setup with last time UI was hit
	 * and checks if busy executors is 0, calls shutdown script if necessary
	 */
	public boolean main(long busyExecutors, DateTime latest, Period period) {
		
		// check if latest hit is before time limit
		if ((latest.isBefore(new DateTime(new DateTime().minus(period)))) && busyExecutors == 0) {
			System.out.println("sleepy time");
			return false;
		}
		else {
			System.out.println("stay awake");
		}
		
		return true;
	}
	
	

}
