package jgine.system;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

public interface SystemObject extends Cloneable {

	public void load(Map<String, Object> data);

	public void save(Map<String, Object> data);

	public void load(DataInput in) throws IOException;

	public void save(DataOutput out) throws IOException;

	public int system();

	public Object clone();
}
