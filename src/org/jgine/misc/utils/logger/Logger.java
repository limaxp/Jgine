package org.jgine.misc.utils.logger;

import java.io.PrintStream;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Represents an logger implemented after the service provider pattern. Should
 * be used as the standard way to log anything. The service can be changed with
 * provide().
 * 
 * @author Maximilian Paar
 */
public class Logger {

	private static ILoggerService logger;
	private static boolean isLogging = true;

	static {
		provide(null);
	}

	public static void provide(ILoggerService service) {
		if (service == null)
			logger = new ConsoleLoggerService();
		else
			logger = service;
	}

	public static void log(String msg) {
		if (isLogging)
			logger.log(msg);
	}

	public static void log(Supplier<String> supplier) {
		if (isLogging)
			logger.log(supplier.get());
	}

	public static void log(Object obj) {
		if (isLogging)
			logger.log(obj.toString());
	}

	public static void warn(String msg) {
		logger.warn(msg);
	}

	public static void warn(Supplier<String> msg) {
		logger.warn(msg);
	}

	public static void err(String msg) {
		logger.err(msg);
	}

	public static void err(Supplier<String> msg) {
		logger.err(msg);
	}

	public static void err(String msg, Throwable throwable) {
		logger.err(msg, throwable);
	}

	public void err(Supplier<String> msg, Throwable throwable) {
		logger.err(msg, throwable);
	}

	public void err(Level lvl, String msg, Throwable throwable) {
		logger.err(lvl, msg, throwable);
	}

	public void err(Level lvl, Supplier<String> msg, Throwable throwable) {
		logger.err(lvl, msg, throwable);
	}

	public static void setLogging(boolean isLogging) {
		Logger.isLogging = isLogging;
	}

	public static boolean isLogging() {
		return isLogging;
	}

	public static PrintStream getLogPrintStream() {
		return logger.getLogPrintStream();
	}

	public static PrintStream getErrorPrintStream() {
		return logger.getErrorPrintStream();
	}
}
