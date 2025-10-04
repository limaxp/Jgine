package org.jgine.utils.collection.function;

@FunctionalInterface
public interface QuintConsumer<T1, T2, T3, T4, T5> {

	public void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
}
