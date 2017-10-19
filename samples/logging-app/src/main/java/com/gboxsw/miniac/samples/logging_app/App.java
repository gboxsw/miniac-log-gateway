package com.gboxsw.miniac.samples.logging_app;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.gboxsw.miniac.*;
import com.gboxsw.miniac.gateways.log.LogGateway;
import com.gboxsw.miniac.gateways.log.LogWriter;
import com.gboxsw.miniac.gateways.log.LoggingConfiguration;
import com.gboxsw.miniac.gateways.log.writers.FileLogWriter;

/**
 * Simple test application for logging that utilizes LogGateway.
 */
public class App {

	public static void main(String[] args) {
		// create application
		Application app = Application.createSimpleApplication();

		// create log gateway
		LogGateway logGateway = new LogGateway();

		// logging configuration
		LoggingConfiguration logConfig = new LoggingConfiguration();
		logConfig.setMaxUnwrittenLogs(10);
		logConfig.setWritePeriodInSeconds(5);

		// log writer
		LogWriter logWriter = new FileLogWriter(new File("log.txt"));

		logGateway.registerWriter(logWriter, logConfig);
		app.addGateway(LogGateway.DEFAULT_ID, logGateway);

		// launch the application
		app.launch();

		// publish logs
		app.publishAtFixedRate(new MessageFactory() {

			@Override
			public Message createMessage() {
				return new Message(Message.createTopic(LogGateway.DEFAULT_ID, "test"),
						"Message " + System.currentTimeMillis());
			}
		}, 0, 1, TimeUnit.SECONDS);

		// wait for enter to exit the application
		try (Scanner s = new Scanner(System.in)) {
			s.nextLine();
		}

		app.publish(new Message("$SYS/exit"));
	}
}