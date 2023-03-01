package org.jgine.utils.logger;

import java.io.PrintStream;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Represents an LoggerService that logs to the java console implemented after
 * the service provider pattern.
 */
public class ConsoleLoggerService implements ILoggerService {

	@Override
	public void log(String msg) {
		System.out.println(msg);
	}

	@Override
	public void log(Supplier<String> msg) {
		System.out.println(msg.get());
	}

	@Override
	public void warn(String msg) {
		System.out.println(msg);
	}

	@Override
	public void warn(Supplier<String> msg) {
		System.out.println(msg.get());
	}

	@Override
	public void err(String msg) {
		System.err.println(msg);
	}

	@Override
	public void err(Supplier<String> msg) {
		System.err.println(msg.get());
	}

	@Override
	public void err(String msg, Throwable throwable) {
		System.err.println(msg);
		throwable.printStackTrace();
	}

	@Override
	public void err(Supplier<String> msg, Throwable throwable) {
		System.err.println(msg.get());
		throwable.printStackTrace();
	}

	@Override
	public void err(Level lvl, String msg, Throwable throwable) {
		System.err.println(msg);
		throwable.printStackTrace();
	}

	@Override
	public void err(Level lvl, Supplier<String> msg, Throwable throwable) {
		System.err.println(msg.get());
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
