package org.jgine.system;

import org.jgine.misc.utils.Benchmark;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.misc.utils.options.Options;

@FunctionalInterface
public interface SystemTask extends Runnable {

	@Override
	default void run() {
		if (Options.DEBUG) {
			Benchmark.start(this);
			runTask();
			Benchmark.stop(this);
			Logger.log(this.getClass().getPackage() + ": " + Benchmark.get(this).getTime() + "ns");
		}
		else
			runTask();
	}

	public void runTask();
}
