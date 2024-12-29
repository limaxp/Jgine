package org.jgine.core.gameLoop;

import org.jgine.core.Engine;

import it.unimi.dsi.fastutil.floats.FloatConsumer;

/**
 * Abstract game loop class. Extend to make a custom game loop and overwrite
 * {@link Engine}.createGameLoop() to use it. Use update() and render() methods
 * to tell the {@link Engine} to do the respective action.
 */
public abstract class GameLoop implements Runnable {

	private static FloatConsumer NULL_FLOAT_CONSUMER = new FloatConsumer() {

		@Override
		public void accept(float f) {
		}
	};

	private FloatConsumer updateFunction = NULL_FLOAT_CONSUMER;
	private FloatConsumer renderFunction = NULL_FLOAT_CONSUMER;

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
}
