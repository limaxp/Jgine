package org.jgine.core.gameLoop;

import org.jgine.core.Engine;

public class FixedTickGameLoop extends GameLoop {

	/**
	 * in millisecond (ms)
	 */
	public final int tickTime;
	protected double previousTime;
	protected double lag;
	protected double frameTime;
	protected int frames;
	protected int fps;

	public FixedTickGameLoop(Engine engine, int tickTime) {
		super(engine);
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
}