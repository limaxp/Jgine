package org.jgine.misc.utils;

import org.jgine.misc.math.FastMath;

public class GID {

	private final long timeStamp;
	private final int id;

	public GID() {
		timeStamp = System.currentTimeMillis();
		id = FastMath.random(Integer.MAX_VALUE);
	}

	public GID(GID gid) {
		timeStamp = gid.timeStamp;
		id = gid.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GID)
			return equals((GID) obj);
		return false;
	}

	public final boolean equals(GID gid) {
		if (id != gid.id)
			return false;
		if (timeStamp != gid.timeStamp)
			return false;
		return true;
	}

	public final long getTimeStamp() {
		return timeStamp;
	}

	public final int getId() {
		return id;
	}
}
