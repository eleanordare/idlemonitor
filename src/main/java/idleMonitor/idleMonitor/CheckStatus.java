package idleMonitor.idleMonitor;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ServiceLoader;

public class CheckStatus {
		
	
	public static <T> T loadService(Class<T> service) {
		
		T result = null;
		ServiceLoader<T> impl = ServiceLoader.load(service);
		
		for (T loadedImpl : impl) {
			result = loadedImpl;
			if (result != null) break;
		}
		
		if (result == null) throw new RuntimeException(
				"Cannot find implementation for: " + service);
		
		return result;
		
	}
	
	public static final Constraints constraints = loadService(Constraints.class);
	
	/*
	 * compares timeout period specified in Setup with last time UI was hit
	 * and checks if busy executors is 0, calls shutdown script if necessary
	 */
	public boolean main(long busyExecutors, DateTime latest, Period period) {
		
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
