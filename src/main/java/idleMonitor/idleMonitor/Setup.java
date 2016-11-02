package idleMonitor.idleMonitor;

import jenkins.model.Jenkins;

import org.joda.time.Period;
import org.kohsuke.stapler.StaplerRequest;

public class Setup implements Constraints {
	
	public Setup() { };

	final static Jenkins jenkins = Jenkins.getInstance();
	final static StaplerRequest req = null;
	
	@Override
	/*
	 * used in PeriodCheck's getRecurrencePeriod
	 * in milliseconds
	 */
	public long getPollingInterval() {
		return 10000;
	}

	@Override
	/*
	 * used in PeriodCheck's checkStatus
	 * to decide how long an instance is left unused
	 * before shutting it down
	 */
	public Period getTimeoutPeriod() {
		Period period = Period.days(7);
		return period;
	}

	@Override
	public void shutdownJenkins() {
//		try {
//			jenkins.doSafeExit(req);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	
	
}
