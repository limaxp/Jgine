package org.jgine.core.gameLoop;

import org.jgine.core.Engine;

/**
 * Abstract game loop class. Extend to make a custom game loop and overwrite
 * {@link Engine}.createGameLoop() to use it. Use update() and render() methods
 * to tell the {@link Engine} to do the respective action.
 */
public abstract class GameLoop implements Runnable {

	private static Runnable NULL = new Runnable() {

		@Override
		public void run() {
		}
	};

	private Runnable updateFunction = NULL;
	private Runnable renderFunction = NULL;

	public abstract int getFps();

	public final void update() {
		updateFunction.run();
	}

	public final void render() {
		renderFunction.run();
	}

	public final void setUpdateFunction(Runnable updateFunction) {
		this.updateFunction = updateFunction;
	}

	public final Runnable getUpdateFunction() {
		return updateFunction;
	}

	public final void setRenderFunction(Runnable renderFunction) {
		this.renderFunction = renderFunction;
	}

	public final Runnable getRenderFunction() {
		return renderFunction;
	}
}
