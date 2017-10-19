package com.gboxsw.miniac.gateways.log;

/**
 * Log writer interface.
 */
public interface LogWriter {

	/**
	 * Opens the writer.
	 */
	public void open();

	/**
	 * Writes logs from queue.
	 * 
	 * @param queue
	 *            the queue of logs to be written.
	 */
	public void write(LogQueue queue);

	/**
	 * Closes the writer.
	 */
	public void close();

}
