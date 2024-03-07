package com.hexagram2021.tetrachordlib.core.container;

import org.apache.logging.log4j.util.TriConsumer;

@FunctionalInterface
public interface VisitConsumer2D<T> extends TriConsumer<Integer, Integer, T> {
	void visit(int x, int y, T value);

	@Override @Deprecated
	default void accept(Integer x, Integer y, T v) {
		this.visit(x, y, v);
	}
}
