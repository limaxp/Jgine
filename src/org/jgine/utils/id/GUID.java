package org.jgine.utils.id;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * An immutable globally unique identifier (GUID). A GUID represents a 128-bit
 * value.
 * <p>
 * Can be used as an alternative to {@link UUID}.
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

	private final long timeStamp;
	private final long id;

	public GUID() {
		timeStamp = System.currentTimeMillis();
		id = HOST_ID << 32 | random(Integer.MAX_VALUE);
	}

	public GUID(long timeStamp, long id) {
		this.timeStamp = timeStamp;
		this.id = id;
	}

	public GUID(GUID gid) {
		timeStamp = gid.timeStamp;
		id = gid.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GUID)
			return equals((GUID) obj);
		return false;
	}

	public final boolean equals(GUID gid) {
		if (timeStamp == gid.timeStamp && id == gid.id)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "" + timeStamp + id;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(timeStamp ^ id);
	}

	@Override
	public int compareTo(GUID val) {
		return (this.timeStamp < val.timeStamp ? -1
				: (this.timeStamp > val.timeStamp ? 1 : (this.id < val.id ? -1 : (this.id > val.id ? 1 : 0))));
	}

	public final long getTimeStamp() {
		return timeStamp;
	}

	public final long getId() {
		return id;
	}

	private static int random(int max) {
		return (int) Math.round(Math.random() * max);
	}
}
