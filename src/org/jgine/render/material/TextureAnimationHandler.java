package org.jgine.render.material;

public class TextureAnimationHandler {

	public static final TextureAnimationHandler NONE = new TextureAnimationHandler(
			new TextureAnimation(0.0f, 0.0f, 0.0f, 1.0f, 1.0f));

	public final TextureAnimation animation;
	private int framePos;
	private long stepStartTime;

	public TextureAnimationHandler(TextureAnimation animation) {
		this.animation = animation;
		framePos = 0;
		stepStartTime = System.currentTimeMillis();
	}

	public final int getAnimationFrame() {
		if (animation.size == 1)
			return 0;

		long currentTime = System.currentTimeMillis();
		if (currentTime >= stepStartTime + animation.getTime(framePos)) {
			stepStartTime = currentTime;
			if (framePos >= animation.size - 1)
				framePos = 0;
			else
				framePos++;
		}
		return framePos;
	}

	public TextureAnimation getAnimation() {
		return animation;
	}

	public static class TextureAnimation {

		public float[] animation;
		private int size;

		public TextureAnimation(int size) {
			animation = new float[size * 5];
		}

		private TextureAnimation(float... animation) {
			this.animation = animation;
		}

		public void addFrame(int time, float x, float y, float width, float height) {
			int index = size * 5;
			if (index == animation.length)
				ensureCapacity(index + 5);
			animation[index] = time;
			animation[index + 1] = x;
			animation[index + 2] = y;
			animation[index + 3] = width;
			animation[index + 4] = height;
			size++;
		}

		public void addFrame(int time, int position, int colums, int rows) {
			int index = size * 5;
			if (index == animation.length)
				ensureCapacity(index + 5);
			int colum = (position - 1) % colums;
			int row = (position - 1) / colums;
			animation[index] = time;
			animation[index + 1] = (float) colum / colums;
			animation[index + 2] = (float) row / rows;
			animation[index + 3] = 1.0f / colums;
			animation[index + 4] = 1.0f / rows;
			size++;
		}

		public void setFrame(int index, int time, float x, float y, float width, float height) {
			index *= 5;
			animation[index] = time;
			animation[index + 1] = x;
			animation[index + 2] = y;
			animation[index + 3] = width;
			animation[index + 4] = height;
		}

		public void setFrame(int index, int time, int position, int colums, int rows) {
			index *= 5;
			int colum = (position - 1) % colums;
			int row = (position - 1) / colums;
			animation[index] = time;
			animation[index + 1] = (float) colum / colums;
			animation[index + 2] = (float) row / rows;
			animation[index + 3] = 1.0f / colums;
			animation[index + 4] = 1.0f / rows;
		}

		public void setTime(int index, int time) {
			animation[index * 5] = time;
		}

		public int getTime(int index) {
			return (int) animation[index * 5];
		}

		public void setX(int index, float x) {
			animation[index * 5 + 1] = x;
		}

		public float getX(int index) {
			return animation[index * 5 + 1];
		}

		public void setY(int index, float y) {
			animation[index * 5 + 2] = y;
		}

		public float getY(int index) {
			return animation[index * 5 + 2];
		}

		public void setWidth(int index, float width) {
			animation[index * 5 + 3] = width;
		}

		public float getWidth(int index) {
			return animation[index * 5 + 3];
		}

		public void setHeight(int index, float height) {
			animation[index * 5 + 4] = height;
		}

		public float getHeight(int index) {
			return animation[index * 5 + 4];
		}

		public int getSize() {
			return size;
		}

		protected void ensureCapacity(int minCapacity) {
			int length = animation.length;
			if (minCapacity > length)
				resize(minCapacity * 2);
		}

		protected void resize(int size) {
			float[] newArray = new float[size];
			System.arraycopy(animation, 0, newArray, 0, this.size * 5);
			animation = newArray;
		}
	}
}