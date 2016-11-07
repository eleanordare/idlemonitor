package idleMonitor;

import hudson.Extension;
import hudson.model.AsyncPeriodicWork;
import hudson.model.TaskListener;
import idleMonitorOptions.IdleMonitorOptions;

import java.io.IOException;
import java.util.ServiceLoader;

import javax.annotation.CheckForNull;

import jenkins.model.Jenkins;

import org.joda.time.DateTime;
import org.joda.time.Period;

import dataUtils.CheckStatus;
import dataUtils.RetrieveDataUtils;

/**
 * Performs checks periodically to see if instance should be up and running
 */
@Extension
public class PeriodicCheck extends AsyncPeriodicWork {

	private CheckStatus check;
	private RetrieveDataUtils retrieve;
	private IdleMonitorOptions options;

	public PeriodicCheck() {
		super("PeriodicCheck");
		check = new CheckStatus();
		retrieve = new RetrieveDataUtils();
		options = loadService(IdleMonitorOptions.class);
	}

	// injection of Constraints implementation
	private <T> T loadService(Class<T> service) {

		T result = null;

		@CheckForNull
		ClassLoader jenkinsClassLoader = Jenkins.getInstance()
				.getPluginManager().uberClassLoader;

		@CheckForNull
		ServiceLoader<T> impl = ServiceLoader.load(service, jenkinsClassLoader);

		for (T loadedImpl : impl) {
			result = loadedImpl;
			if (result != null)
				break;
		}

		if (result == null)
			throw new RuntimeException("Cannot find implementation for: "
					+ service);

		return result;
	}

	@Override
	protected void execute(TaskListener taskListener) throws IOException {

		long busyExecutors;
		try {
			busyExecutors = retrieve.getBusyExecutors();
			Period period = options.getTimeoutPeriod();
			DateTime latest = new DateTime(retrieve.getLatestHit());
			if (!check.checkStatus(busyExecutors, latest, period)) {
				options.shutdownJenkins();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * specified in implementation of Constraints interface, execute method is
	 * run this often
	 */
	@Override
	public long getRecurrencePeriod() {
		return options.getPollingInterval();
	}

}