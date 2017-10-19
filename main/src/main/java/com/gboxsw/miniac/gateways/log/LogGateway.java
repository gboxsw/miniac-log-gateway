package com.gboxsw.miniac.gateways.log;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gboxsw.miniac.*;

/**
 * The gateway that manages asynchronous writing of logs.
 */
public class LogGateway extends Gateway {

	/**
	 * Default (pre-defined) name of the gateway.
	 */
	public static final String DEFAULT_ID = "log";

	/**
	 * Logger.
	 */
	private static final Logger logger = Logger.getLogger(LogGateway.class.getName());

	/**
	 * Handler of log records for a log writer.
	 */
	private class LogHandler {
		/**
		 * The writer used by the handler.
		 */
		private final LogWriter writer;

		/**
		 * The filter of logs.
		 */
		private final LogFilter filter;

		/**
		 * The queue of logs.
		 */
		private final LogQueue queue = new LogQueue();

		/**
		 * Maximal acceptable number of unwritten logs.
		 */
		private final int maxUnwrittenLogs;

		/**
		 * Periods in seconds that defines period between two periodical
		 * writings of log messages in the queue.
		 */
		final int writePeriodInSeconds;

		/**
		 * Asynchronous task that realizes writing of logs in the queue.
		 */
		private final Runnable writeTask = new Runnable() {

			@Override
			public void run() {
				try {
					writer.write(queue);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Write operation by " + writer.toString() + " failed.", e);
				} finally {
					synchronized (LogHandler.this) {
						writeTaskFuture = null;
					}
				}
			}
		};

		/**
		 * Future of submitted task that realizes writing.
		 */
		private Future<?> writeTaskFuture = null;

		/**
		 * The task to be executed in the application thread to check whether
		 * there are logs in the queue to be written.
		 */
		private final Runnable checkTask = new Runnable() {

			@Override
			public void run() {
				synchronized (LogHandler.this) {
					if (writeTaskFuture != null) {
						return;
					}

					if (queue.isEmpty()) {
						return;
					}

					writeTaskFuture = executor.submit(writeTask);
				}
			}
		};

		/**
		 * Cancellable for the check task (updates must be synchronized using
		 * the monitor associated with the list of handlers).
		 */
		private Cancellable checkTaskCancellable;

		/**
		 * Constructs the handler.
		 * 
		 * @param writer
		 *            the managed/controlled log writer.
		 * @param filter
		 *            the filter of logs.
		 * @param configuration
		 *            the configuration of logging.
		 */
		private LogHandler(LogWriter writer, LogFilter filter, LoggingConfiguration configuration) {
			this.writer = writer;
			this.filter = filter;
			this.maxUnwrittenLogs = configuration.getMaxUnwrittenLogs();
			this.writePeriodInSeconds = configuration.getWritePeriodInSeconds();
		}

		/**
		 * Adds log record to internal queue associated with the handled writer.
		 * 
		 * @param logRecord
		 *            the log record.
		 */
		private void addLogRecord(LogRecord logRecord) {
			// apply filter
			if (filter != null) {
				try {
					if (!filter.accept(logRecord.getType())) {
						return;
					}
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Log filter raised an exception.", e);
					return;
				}
			}

			// append record to queue
			synchronized (this) {
				queue.offer(logRecord);
				if (writeTaskFuture != null) {
					return;
				}

				if (queue.size() > maxUnwrittenLogs) {
					writeTaskFuture = executor.submit(writeTask);
				}
			}
		}
	}

	/**
	 * List of handlers for registered log writers.
	 */
	private final List<LogHandler> handlers = new ArrayList<>();

	/**
	 * Executor used for executing asynchronous actions during execution of
	 * commands.
	 */
	private ExecutorService executor;

	/**
	 * Registers a log writer with a log filter.
	 * 
	 * @param logWriter
	 *            the log writer.
	 * @param logFilter
	 *            the log filter.
	 * @param configuration
	 *            the logging configuration.
	 */
	public void registerWriter(LogWriter logWriter, LogFilter logFilter, LoggingConfiguration configuration) {
		if (logWriter == null) {
			throw new NullPointerException("The log writer cannot be null.");
		}

		if (configuration == null) {
			throw new NullPointerException("The logging configuration cannot be null.");
		}

		synchronized (handlers) {
			if (isRunning()) {
				throw new IllegalStateException("The gateway is started. No writer can be registered.");
			}

			for (LogHandler state : handlers) {
				if (state.writer == logWriter) {
					throw new IllegalArgumentException("The writer is already registered.");
				}
			}

			handlers.add(new LogHandler(logWriter, logFilter, configuration));
		}
	}

	/**
	 * Registers a log writer.
	 * 
	 * @param logWriter
	 *            the log writer.
	 * @param configuration
	 *            the logging configuration.
	 */
	public void registerWriter(LogWriter logWriter, LoggingConfiguration configuration) {
		registerWriter(logWriter, null, configuration);
	}

	@Override
	protected void onStart(Map<String, Bundle> bundles) {
		Application app = getApplication();

		// retrieve executor service
		executor = app.getExecutorService();

		// setup handlers
		List<LogWriter> logWriters = new ArrayList<>();
		synchronized (handlers) {
			for (LogHandler handler : handlers) {
				if (handler.writePeriodInSeconds > 0) {
					handler.checkTaskCancellable = app.invokeWithFixedDelay(handler.checkTask, 0,
							handler.writePeriodInSeconds, TimeUnit.SECONDS);
				}

				logWriters.add(handler.writer);
			}
		}

		if (logWriters.isEmpty()) {
			logger.log(Level.WARNING, "No log writers are registered to be used by the gateway " + getId());
		}

		// open log writers
		for (LogWriter writer : logWriters) {
			try {
				writer.open();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Log writer " + writer.toString() + " cannot be open.", e);
				throw e;
			}
		}
	}

	@Override
	protected void onAddTopicFilter(String topicFilter) {
		// nothing to do
	}

	@Override
	protected void onRemoveTopicFilter(String topicFilter) {
		// nothing to do
	}

	@Override
	protected void onPublish(Message message) {
		LogRecord logRecord = new LogRecord(message.getTopic(), message.getContent());
		synchronized (handlers) {
			for (LogHandler handler : handlers) {
				handler.addLogRecord(logRecord);
			}
		}
	}

	@Override
	protected void onSaveState(Map<String, Bundle> outBundles) {
		// nothing to do
	}

	@Override
	protected void onStop() {
		// unregister tasks
		List<LogWriter> logWriters = new ArrayList<>();
		synchronized (handlers) {
			for (LogHandler handler : handlers) {
				if (handler.checkTaskCancellable != null) {
					handler.checkTaskCancellable.cancel();
				}

				handler.checkTaskCancellable = null;
				logWriters.add(handler.writer);
			}
		}

		// close log writers
		for (LogWriter writer : logWriters) {
			try {
				writer.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Closing of log writer " + writer.toString() + " failed.", e);
			}
		}

		executor = null;
	}

	@Override
	protected boolean isValidTopicName(String topicName) {
		if ((topicName == null) || topicName.isEmpty()) {
			return false;
		}

		return true;
	}
}
