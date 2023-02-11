package org.jgine.render.material;

public class TextureAnimationHandler {

	public static final TextureAnimationHandler NONE = new TextureAnimationHandler(
			new AnimationFrame[] { new AnimationFrame(0, 0.0f, 0.0f, 1.0f, 1.0f) });

	public final AnimationFrame[] animation;
	private int framePos;
	private long stepStartTime;

	public TextureAnimationHandler(AnimationFrame... animation) {
		this.animation = animation;
		framePos = 0;
		stepStartTime = System.currentTimeMillis();
	}

	public final AnimationFrame getAnimationFrame() {
		AnimationFrame frame = animation[framePos];
		if (animation.length == 1)
			return frame;

		long currentTime = System.currentTimeMillis();
		if (currentTime >= stepStartTime + frame.frameTime) {
			stepStartTime = currentTime;
			if (framePos >= animation.length - 1)
				framePos = 0;
			else
				framePos++;
			frame = animation[framePos];
		}
		return frame;
	}

	public static class AnimationFrame {

		public int frameTime;
		public float x;
		public float y;
		public float width;
		public float height;

		public AnimationFrame(int frameTime, float x, float y, float width, float height) {
			this.frameTime = frameTime;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public AnimationFrame(int frameTime, Texture texture, int texturePos) {
			this.frameTime = frameTime;
			setTexturePos(texture, texturePos);
		}

		public void setTexturePos(Texture texture, int texturePos) {
			int colums = texture.getColums();
			int rows = texture.getRows();
			int colum = (texturePos - 1) % colums;
			int row = (texturePos - 1) / colums;
			x = (float) colum / colums;
			y = (float) row / rows;
			width = 1.0f / colums;
			height = 1.0f / rows;
		}
	}
}