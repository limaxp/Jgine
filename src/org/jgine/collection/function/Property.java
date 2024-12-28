package org.jgine.collection.function;

/**
 * A basic property.
 * 
 * @param <T> the type of property
 */
public interface Property<T> {

	public void setValue(T obj);

	public T getValue();
}
