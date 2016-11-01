package idleMonitor.idleMonitor;

import hudson.Extension;
import hudson.model.AsyncPeriodicWork;
import hudson.model.TaskListener;

import org.joda.time.*;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ServiceLoader;


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
	
	final static String username = "admin";
	final static String password = "admin";
	
	
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
	
	
    @Override
    protected void execute(TaskListener taskListener) throws IOException {
    	
		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication (username, password.toCharArray());
		    }
		});
		    	
    	System.out.println("---------------------------------");
    	
    	long busyExecutors = retrieve.getBusyExecutors();
		Period period = constraints.getTimeoutPeriod();
		DateTime latest = new DateTime(retrieve.getLatestHit());
    	
    	check.main(busyExecutors, latest, period);
    	
    	System.out.println("---------------------------------");
	
    }

    /*
     * specified in Setup, execute method is run this often
     */
    @Override
    public long getRecurrencePeriod() {
		return constraints.getPollingInterval();
    }
    
}