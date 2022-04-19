package org.jgine.render.graphic.material;

public class TextureAnimationHandler {

	public static final TextureAnimationHandler NONE = new TextureAnimationHandler(new AnimationFrame[] {
			new AnimationFrame(0, 1) });

	public final AnimationFrame[] animation;
	private int framePos;
	private long stepStartTime;

	public TextureAnimationHandler(AnimationFrame... animation) {
		this.animation = animation;
		framePos = 0;
		stepStartTime = System.currentTimeMillis();
	}

	public final int getTexturePosition() {
		AnimationFrame frame = animation[framePos];
		if (animation.length == 1)
			return frame.texturePos;

		long currentTime = System.currentTimeMillis();
		if (currentTime >= stepStartTime + frame.frameTime) {
			stepStartTime = currentTime;
			if (framePos >= animation.length - 1)
				framePos = 0;
			else
				framePos++;
			frame = animation[framePos];
		}
		return frame.texturePos;
	}

	public static class AnimationFrame {

		public int frameTime;
		public int texturePos;

		public AnimationFrame(int frameTime, int texturePos) {
			this.frameTime = frameTime;
			this.texturePos = texturePos;
		}
	}
}