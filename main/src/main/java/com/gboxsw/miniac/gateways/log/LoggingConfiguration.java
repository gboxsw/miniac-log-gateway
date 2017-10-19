package com.gboxsw.miniac.gateways.log;

/**
 * The logging configuration.
 */
public class LoggingConfiguration {

	/**
	 * The maximal number of unwritten logs.
	 */
	private int maxUnwrittenLogs = 0;

	/**
	 * Period in seconds for periodical writings of logs.
	 */
	private int writePeriodInSeconds = 60;

	/**
	 * Constructs the logging configuration.
	 * 
	 * @param maxUnwrittenLogs
	 *            the value for
	 *            {@link LoggingConfiguration#setMaxUnwrittenLogs(int)}.
	 * @param writePeriodInSeconds
	 *            the value for
	 *            {@link LoggingConfiguration#setWritePeriodInSeconds(int)}.
	 */
	public LoggingConfiguration(int maxUnwrittenLogs, int writePeriodInSeconds) {
		setMaxUnwrittenLogs(maxUnwrittenLogs);
		setWritePeriodInSeconds(writePeriodInSeconds);
	}

	/**
	 * Constructs the logging configuration with default values.
	 */
	public LoggingConfiguration() {

	}

	/**
	 * Returns the maximal number of unwritten logs.
	 * 
	 * @return the maximal number of unwritten logs.
	 */
	public int getMaxUnwrittenLogs() {
		return maxUnwrittenLogs;
	}

	/**
	 * Sets the maximal number of unwritten logs.
	 * 
	 * @param maxUnwrittenLogs
	 *            the maximal number of unwritten logs.
	 */
	public void setMaxUnwrittenLogs(int maxUnwrittenLogs) {
		this.maxUnwrittenLogs = maxUnwrittenLogs;
	}

	/**
	 * Returns the period for periodical writings of logs.
	 * 
	 * @return the period in seconds. If the period is 0 or negative value, no
	 *         periodical writing is schedules.
	 */
	public int getWritePeriodInSeconds() {
		return writePeriodInSeconds;
	}

	/**
	 * Sets the period for periodical writings of logs.
	 * 
	 * @param writePeriodInSeconds
	 *            the period in seconds. If the period is 0 or negative value,
	 *            no periodical writing is schedules.
	 */
	public void setWritePeriodInSeconds(int writePeriodInSeconds) {
		this.writePeriodInSeconds = writePeriodInSeconds;
	}
}
