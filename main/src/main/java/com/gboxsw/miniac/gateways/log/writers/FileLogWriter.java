package com.gboxsw.miniac.gateways.log.writers;

import java.io.*;
import com.gboxsw.miniac.gateways.log.*;

/**
 * Simple text based logger.
 */
public class FileLogWriter implements LogWriter {

	/**
	 * Output file.
	 */
	private final File outputFile;

	/**
	 * Constructs the file logger.
	 * 
	 * @param outputFile
	 *            the output file where log messages are written.
	 * 
	 */
	public FileLogWriter(File outputFile) {
		if (outputFile == null) {
			throw new NullPointerException("The output file cannot be null.");
		}

		this.outputFile = outputFile;
	}

	@Override
	public void open() {
		// nothing to do
	}

	@Override
	public void write(LogQueue queue) {
		LogRecord record = null;

		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)))) {
			while (true) {
				record = queue.poll();
				if (record == null) {
					break;
				}

				// sanitize type
				String type = record.getType();
				if (type.indexOf('\t') >= 0) {
					type = type.replace('\t', ' ');
				}
				if (type.indexOf('\n') >= 0) {
					type = type.replace('\n', ' ');
				}

				// sanitize message
				String message = record.getMessage();
				if (message.indexOf('\n') >= 0) {
					message = message.replace('\n', ' ');
				}

				pw.println(type + "\t" + message);
				record = null;
			}
		} catch (Exception e) {
			if (record != null) {
				queue.pushBack(record);
			}

			throw new RuntimeException("Writing to file " + outputFile.getAbsolutePath() + " failed.", e);
		}	
	}

	@Override
	public void close() {
		// nothing to do
	}

}
