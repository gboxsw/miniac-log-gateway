package com.gboxsw.miniac.gateways.log;

/**
 * Log record.
 */
public class LogRecord {

	/**
	 * System time when the log was received by the gateway.
	 */
	private final long time;

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
		this.time = System.currentTimeMillis();
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

	/**
	 * Returns the unix timestamp of time when the message was received by the
	 * gateway.
	 * 
	 * @return the unix timestamp.
	 */
	public long getTime() {
		return time;
	}
}
