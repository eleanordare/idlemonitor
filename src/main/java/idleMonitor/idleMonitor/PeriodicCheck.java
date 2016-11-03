package idleMonitor.idleMonitor;

import hudson.Extension;
import hudson.model.AsyncPeriodicWork;
import hudson.model.TaskListener;

import java.io.IOException;
import java.util.ServiceLoader;

import javax.annotation.CheckForNull;

import jenkins.model.Jenkins;

import org.joda.time.DateTime;
import org.joda.time.Period;


/**
 * Performs checks periodically to see if instance should be up and running
 */
@Extension
public class PeriodicCheck extends AsyncPeriodicWork {
	
    public PeriodicCheck() {
        super("PeriodicCheck");
    }

	CheckStatus check = new CheckStatus();
	RetrieveDataUtils retrieve = new RetrieveDataUtils();
	
	
	@CheckForNull
	ClassLoader jenkins = Jenkins.getInstance().getPluginManager().uberClassLoader;
	
	//injection of Constraints implementation
	public <T> T loadService(Class<T> service) {
		
		T result = null;
		@CheckForNull
		ServiceLoader<T> impl = ServiceLoader.load(service, jenkins);
		
		for (T loadedImpl : impl) {
			result = loadedImpl;
			if (result != null) break;
		}
		
		if (result == null) throw new RuntimeException(
				"Cannot find implementation for: " + service);
		
		return result;
	}
	
	public final Constraints constraints = loadService(Constraints.class);
	
	
    @Override
    protected void execute(TaskListener taskListener) throws IOException {
    			    	    	
    	long busyExecutors = retrieve.getBusyExecutors();
		Period period = constraints.getTimeoutPeriod();
		DateTime latest = new DateTime(retrieve.getLatestHit());
    	
		if (!check.checkStatus(busyExecutors, latest, period)) {
			constraints.shutdownJenkins();
		}
		    		
    }

    /*
     * specified in implementation of Constraints interface,
     * execute method is run this often
     */
    @Override
    public long getRecurrencePeriod() {
		return constraints.getPollingInterval();
    }
    
}