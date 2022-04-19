package org.jgine.core.gameLoop;

import org.jgine.core.Engine;

public abstract class GameLoop implements Runnable {

	public final Engine engine;

	public GameLoop(Engine engine) {
		this.engine = engine;
	}

	public abstract int getFps();

	public final void update() {
		engine.update();
	}

	public final void render() {
		engine.render();
	}
}
