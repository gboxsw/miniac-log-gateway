package com.gboxsw.miniac.gateways.log;

/**
 * Log record.
 */
public class LogRecord {

	/**
	 * The type of log.
	 */
	private final String type;

	/**
	 * The message/content of the log.
	 */
	private final String message;

	/**
	 * Constructs the log record.
	 * 
	 * @param type
	 *            the type.
	 * @param message
	 *            the message.
	 */
	public LogRecord(String type, String message) {
		this.type = type;
		this.message = message;
	}

	/**
	 * Returns the type of the log.
	 * 
	 * @return the type of the log.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the message of the log.
	 * 
	 * @return the message of the log.
	 */
	public String getMessage() {
		return message;
	}
}
