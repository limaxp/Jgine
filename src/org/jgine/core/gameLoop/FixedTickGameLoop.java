package org.jgine.core.gameLoop;

/**
 * {@link GameLoop} implementation that updates at given time interval in
 * milliseconds and renders as often as possible.
 */
public class FixedTickGameLoop extends GameLoop {

	/**
	 * in millisecond (ms)
	 */
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
			update((float) passedtime * 0.001f);

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

	public void setTickTime(int tickTime) {
		this.tickTime = tickTime;
	}

	public int getTickTime() {
		return tickTime;
	}
}