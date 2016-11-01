package idleMonitor.idleMonitor;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.google.inject.Guice;
import com.google.inject.Inject;

public class CheckStatus {
	
	private transient Constraints constraints;
	
	@Inject
	public void setConstraints(Constraints constraints) {
		this.constraints = constraints;
	}
	
	/*
	 * compares timeout period specified in Setup with last time UI was hit
	 * and checks if busy executors is 0, calls shutdown script if necessary
	 */
	public boolean main(long busyExecutors, DateTime latest, Period period) {
		
		if (constraints == null) {
			Guice.createInjector(new GuiceModule()).injectMembers(this);
		}
		
		// check if latest hit is before time limit
		if ((latest.isBefore(new DateTime(new DateTime().minus(period)))) && busyExecutors == 0) {
			System.out.println("sleepy time");
			constraints.shutdownJenkins();
			return false;
		}
		else {
			System.out.println("stay awake");
		}
		
		return true;
	}
	
	

}
