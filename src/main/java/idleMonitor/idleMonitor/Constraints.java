package idleMonitor.idleMonitor;

import org.joda.time.*;

public interface Constraints {

	public long getPollingInterval();
	
	public Period getTimeoutPeriod();
	
	public void shutdownJenkins();
	
}
