package jgine.core;

import it.unimi.dsi.fastutil.floats.FloatConsumer;

/**
 * Abstract game loop class. Extend to make a custom game loop and overwrite
 * {@link Engine}.createGameLoop() to use it. Use update() and render() methods
 * to tell the {@link Engine} to do the respective task.
 */
public abstract class GameLoop implements Runnable {

	private FloatConsumer updateFunction = (_) -> {
	};
	private FloatConsumer renderFunction = (_) -> {
	};

	public abstract int getFps();

	public final void update(float dt) {
		updateFunction.accept(dt);
	}

	public final void render(float dt) {
		renderFunction.accept(dt);
	}

	public final void setUpdateFunction(FloatConsumer updateFunction) {
		this.updateFunction = updateFunction;
	}

	public final FloatConsumer getUpdateFunction() {
		return updateFunction;
	}

	public final void setRenderFunction(FloatConsumer renderFunction) {
		this.renderFunction = renderFunction;
	}

	public final FloatConsumer getRenderFunction() {
		return renderFunction;
	}

	/**
	 * {@link GameLoop} implementation that updates on a given time interval in
	 * milliseconds and renders as often as possible.
	 */
	public static class FixedTickGameLoop extends GameLoop {

		protected int tickTime;
		protected double previousTime;
		protected double lag;
		protected double frameTime;
		protected int frames;
		protected int fps;

		public FixedTickGameLoop(int tickTime) {
			this.tickTime = tickTime;
			previousTime = System.nanoTime() * 0.000001;
		}

		@Override
		public void run() {
			double currentTime = System.nanoTime() * 0.000001;
			double passedtime = currentTime - previousTime;
			previousTime = currentTime;
			lag += passedtime;
			frameTime += passedtime;

			while (lag >= tickTime) {
				lag -= tickTime;
				update((float) tickTime * 0.001f);

				if (frameTime >= 1000) {
					fps = frames;
					frameTime = 0;
					frames = 0;
				}
			}
			render((float) passedtime * 0.001f);
			frames++;
		}

		@Override
		public int getFps() {
			return fps;
		}

		/**
		 * @param tickTime in ms (milliseconds)
		 */
		public void setTickTime(int tickTime) {
			this.tickTime = tickTime;
		}

		/**
		 * @return tickTime in ms (milliseconds)
		 */
		public int getTickTime() {
			return tickTime;
		}
	}
}
