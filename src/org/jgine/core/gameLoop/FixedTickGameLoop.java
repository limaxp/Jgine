package org.jgine.core.gameLoop;

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
		previousTime = System.nanoTime() / 1000000.0;
	}

	@Override
	public void run() {
		double currentTime = System.nanoTime() / 1000000.0;
		double passedtime = currentTime - previousTime;
		previousTime = currentTime;
		lag += passedtime;
		frameTime += passedtime;

		while (lag >= tickTime) {
			lag -= tickTime;
			update();

			if (frameTime >= 1000) {
				fps = frames;
				frameTime = 0;
				frames = 0;
			}
		}
		render();
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