package org.jgine.misc.utils.id;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.jgine.misc.math.FastMath;

/**
 * A class that represents an immutable globally unique identifier (GUID). A
 * GUID represents a 128-bit value.
 * <p>
 * Can be used as an alternatice to {@link UUID}.
 */
public class GUID implements Serializable, Comparable<GUID> {

	private static final long serialVersionUID = 7823925157660388902L;
	private static final int HOST_ID;

	static {
		String hostName;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Can't access LocalHost!");
		}
		HOST_ID = hostName.hashCode();
	}

	private final long id;
	private final long timeStamp;

	public GUID() {
		id = HOST_ID << 32 | FastMath.random(Integer.MAX_VALUE);
		timeStamp = System.currentTimeMillis();
	}

	public GUID(GUID gid) {
		id = gid.id;
		timeStamp = gid.timeStamp;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GUID)
			return equals((GUID) obj);
		return false;
	}

	public final boolean equals(GUID gid) {
		if (id == gid.id && timeStamp == gid.timeStamp)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "" + id + timeStamp;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id ^ timeStamp);
	}

	@Override
	public int compareTo(GUID val) {
		return (this.id < val.id ? -1
				: (this.id > val.id ? 1
						: (this.timeStamp < val.timeStamp ? -1 : (this.timeStamp > val.timeStamp ? 1 : 0))));
	}

	public final long getId() {
		return id;
	}

	public final long getTimeStamp() {
		return timeStamp;
	}
}
