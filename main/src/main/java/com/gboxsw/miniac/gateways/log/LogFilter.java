package com.gboxsw.miniac.gateways.log;

/**
 * Log filter based on type of log.
 */
public interface LogFilter {

	/**
	 * Determines whether given type of logs is accepted.
	 * 
	 * @param type
	 *            the type of log.
	 * @return true, if the logs of given type are accepted, false otherwise.
	 */
	public boolean accept(String type);

}
