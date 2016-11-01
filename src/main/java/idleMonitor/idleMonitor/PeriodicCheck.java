package idleMonitor.idleMonitor;

import hudson.Extension;
import hudson.model.AsyncPeriodicWork;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;

import org.joda.time.*;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import javax.annotation.CheckForNull;

/**
 * Performs checks periodically to see if instance should be up and running
 */
@Extension
public class PeriodicCheck extends AsyncPeriodicWork {
	
    public PeriodicCheck() {
        super("PeriodicCheck");
    }

	final Setup setup = new Setup();
	CheckStatus check = new CheckStatus();
	RetrieveDataUtils retrieve = new RetrieveDataUtils();
	
	final static String username = "admin";
	final static String password = "admin";
	
	
    @Override
    protected void execute(TaskListener taskListener) throws IOException {
    	
		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication (username, password.toCharArray());
		    }
		});
    	
    	System.out.println("---------------------------------");
    	
    	long busyExecutors = retrieve.getBusyExecutors();
    	Period period = setup.getTimeoutPeriod();
    	DateTime latest = new DateTime(retrieve.getLatestHit());
    	
    	check.main(busyExecutors, latest, period);
    	
    	System.out.println("---------------------------------");
	
    }

    /*
     * specified in Setup, execute method is run this often
     */
    @Override
    public long getRecurrencePeriod() {
        return setup.getPollingInterval();
    }
    
}