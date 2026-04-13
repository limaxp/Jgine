package jgine.utils;

import java.io.PrintStream;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Represents an logger implemented after the service provider pattern. Should
 * be used as the standard way to log anything. The service can be changed with
 * provide().
 */
public class Logger {

	private static ILogger logger;
	private static boolean isLogging = Options.DEBUG;

	static {
		provide(null);
	}

	public static void provide(@Nullable ILogger service) {
		if (service == null)
			logger = new ConsoleLogger();
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
		logger.warn(msg.get());
	}

	public static void err(String msg) {
		logger.err(msg);
	}

	public static void err(Supplier<String> msg) {
		logger.err(msg.get());
	}

	public static void err(String msg, Throwable throwable) {
		logger.err(msg, throwable);
	}

	public void err(Supplier<String> msg, Throwable throwable) {
		logger.err(msg.get(), throwable);
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

	/**
	 * Represents the base of an LoggerService implemented after the service
	 * provider pattern.
	 */
	public static interface ILogger {

		public void log(String msg);

		public void warn(String msg);

		public void err(String msg);

		public void err(String msg, Throwable throwable);

		public PrintStream getLogPrintStream();

		public PrintStream getErrorPrintStream();
	}

	/**
	 * Represents an LoggerService that logs to the java console implemented after
	 * the service provider pattern.
	 */
	public static class ConsoleLogger implements ILogger {

		@Override
		public void log(String msg) {
			System.out.println(msg);
		}

		@Override
		public void warn(String msg) {
			System.out.println(msg);
		}

		@Override
		public void err(String msg) {
			System.err.println(msg);
		}

		@Override
		public void err(String msg, Throwable throwable) {
			System.err.println(msg);
			throwable.printStackTrace();
		}

		@Override
		public PrintStream getLogPrintStream() {
			return System.out;
		}

		@Override
		public PrintStream getErrorPrintStream() {
			return System.err;
		}
	}
}