package org.jgine.core.gameLoop;

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
