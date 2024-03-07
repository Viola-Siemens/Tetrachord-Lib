package com.hexagram2021.tetrachordlib.core.container.impl;

import com.hexagram2021.tetrachordlib.core.container.IEditRule;
import com.hexagram2021.tetrachordlib.core.container.SegmentTree2D;
import com.hexagram2021.tetrachordlib.core.container.VisitConsumer2D;

/**
 * Unfinished. Please use Quad version.
 * @see ArrayQuadSegmentTree2D
 */
@SuppressWarnings("unused")
public class ArraySegmentTreeOfSegmentTrees2D<T> implements SegmentTree2D<T> {
	@Override
	public void edit(T delta, int beginX, int endX, int beginY, int endY) {
		throw new UnsupportedOperationException("Unfinished");
	}

	@Override
	public T query(int beginX, int endX, int beginY, int endY) {
		throw new UnsupportedOperationException("Unfinished");
	}

	@Override
	public IEditRule<T> getEditRule() {
		throw new UnsupportedOperationException("Unfinished");
	}

	@Override
	public int sideSize(int dimension) {
		throw new UnsupportedOperationException("Unfinished");
	}

	@Override
	public int totalSize() {
		throw new UnsupportedOperationException("Unfinished");
	}

	@Override
	public void visit(int beginX, int endX, int beginY, int endY, VisitConsumer2D<T> consumer) {
		throw new UnsupportedOperationException("Unfinished");
	}
}
