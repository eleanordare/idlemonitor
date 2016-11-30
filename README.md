Jenkins IdleMonitor plugin
===========================

Installation
------------
  - download entire idleMonitor directory and `mvn clean install` to get idleMonitor/target/idleMonitor.hpi
  - copy target/idleMonitor.hpi to $JENKINS_HOME/plugins directory
  - make sure that the Monitoring plugin is also included in the $JENKINS_HOME/plugins directory
  - restart Jenkins instance and plugin will be installed


Implementing IdleMonitorOptions interface
--------------------------------------------
  - getPollingInterval() -- used in PeriodCheck's getRecurrencePeriod as frequency of checking instance, return long in milliseconds
  - getTimeoutPeriod() -- used in PeriodCheck's checkStatus to determine how long since the last UI hit that an instance can stay up, return Period in any measure of time (months, days, hours, seconds)
  - shutdownJenkins() -- called in PeriodCheck's checkStatus when an instance should be shut down
  
ServiceLoader injects custom implementation of IdleMonitorOptions:
  - loadServices() in PeriodicCheck will reference src/main/resources/META-INF/services/idleMonitorOptions.IdleMonitorOptions
  - change the text in that file to the path of your implementation (i.e., idleMonitorOptions.Setup)

