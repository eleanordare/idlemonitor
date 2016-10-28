# idlemonitor

Constraints interface includes:
  - getPollingInterval() -- used in PeriodCheck's getRecurrencePeriod as frequency of checking instance, return long in milliseconds
  - getTimeoutPeriod() -- used in PeriodCheck's checkStatus to determine how long since the last UI hit that an instance can stay up, return Period in any measure of time (months, days, hours, seconds)
  - shutdownJenkins() -- called in PeriodCheck's checkStatus when an instance should be shut down
