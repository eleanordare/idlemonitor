package idleMonitor.idleMonitor;

public class GuiceModule extends com.google.inject.AbstractModule {

	@Override
	protected void configure() {
		bind(Constraints.class).to(Setup.class).in(com.google.inject.Singleton.class);
	}

	
	
}
