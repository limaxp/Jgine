package org.jgine.utils.logger;

import java.io.PrintStream;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Represents the base of an LoggerService implemented after the service
 * provider pattern.
 */
public interface ILoggerService {

	public void log(String msg);

	public void log(Supplier<String> msg);

	public void warn(String msg);

	public void warn(Supplier<String> msg);

	public void err(String msg);

	public void err(Supplier<String> msg);

	public void err(String msg, Throwable throwable);

	public void err(Supplier<String> msg, Throwable throwable);

	public void err(Level lvl, String msg, Throwable throwable);

	public void err(Level lvl, Supplier<String> msg, Throwable throwable);

	public PrintStream getLogPrintStream();

	public PrintStream getErrorPrintStream();
}
