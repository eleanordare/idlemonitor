package idleMonitor.idleMonitor;

import org.joda.time.Period;

public interface Constraints {

	public long getPollingInterval();
	
	public Period getTimeoutPeriod();
	
	public void shutdownJenkins();
	
}
