package idleMonitorOptions;

import org.joda.time.Period;

public interface IdleMonitorOptions {

	public long getPollingInterval();

	public Period getTimeoutPeriod();

	public void shutdownJenkins();

}
