package idleMonitorOptions;

import jenkins.model.Jenkins;

import org.joda.time.Period;
import org.kohsuke.stapler.StaplerRequest;

public class Setup implements IdleMonitorOptions {

	public Setup() {
	};

	final Jenkins jenkins = Jenkins.getInstance();
	final StaplerRequest req = null;

	@Override
	/*
	 * used in PeriodCheck's getRecurrencePeriod in milliseconds
	 */
	public long getPollingInterval() {
		return 10000;
	}

	@Override
	/*
	 * used in PeriodCheck's checkStatus to decide how long an instance is left
	 * unused before shutting it down
	 */
	public Period getTimeoutPeriod() {
		Period period = Period.days(7);
		return period;
	}

	@Override
	public void shutdownJenkins() {

	}

}
