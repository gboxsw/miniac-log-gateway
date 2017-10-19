package com.gboxsw.miniac.gateways.log;

import java.util.*;

/**
 * Thread-safe queue of log records that allows push-back of logs.
 */
public class LogQueue {

	/**
	 * Internal queue.
	 */
	private final LinkedList<LogRecord> queue = new LinkedList<LogRecord>();

	/**
	 * Internal method that adds new record to the queue.
	 * 
	 * @param logRecord
	 *            the log record.
	 */
	void offer(LogRecord logRecord) {
		synchronized (queue) {
			queue.offer(logRecord);
		}
	}

	/**
	 * Retrieves and removes the head (first element) of the queue.
	 * 
	 * @return the head of the queue, or null if this queue is empty.
	 */
	public LogRecord poll() {
		synchronized (queue) {
			return queue.poll();
		}
	}

	/**
	 * Pushes a log records back to the head of the queue.
	 * 
	 * @param logRecord
	 *            the log record.
	 */
	public void pushBack(LogRecord logRecord) {
		if (logRecord == null) {
			throw new NullPointerException("Log record cannot be null.");
		}

		synchronized (queue) {
			queue.addFirst(logRecord);
		}
	}

	/**
	 * Returns whether the queue is empty.
	 * 
	 * @return true, if the queue is empty, false otherwise.
	 */
	public boolean isEmpty() {
		synchronized (queue) {
			return queue.isEmpty();
		}
	}

	/**
	 * Returns the size of (number of log records in) the queue.
	 * 
	 * @return the size of the queue.
	 */
	public int size() {
		synchronized (queue) {
			return queue.size();
		}
	}
}
