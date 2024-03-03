package com.hexagram2021.tetrachordlib.core.container;

import com.hexagram2021.tetrachordlib.core.container.impl.ArrayQuadSegmentTree2D;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

/**
 * Different from Segment Tree 1D, this segment tree maintains data of a 2D area.
 * @param <T>	The type of value that this SegmentTree maintains.
 * @see SegmentTree1D
 */
@SuppressWarnings("unused")
public interface SegmentTree2D<T> {
	/**
	 * Edit an area [beginX, endX)x[beginY, endY), notice that begin is inclusive while end is exclusive.
	 * @param delta		Amount of the edit.
	 * @param beginX	Left bound (inclusive) of x-axis of the area.
	 * @param endX		Right bound (exclusive) of x-axis of the area.
	 * @param beginY	Top bound (inclusive) of y-axis of the area.
	 * @param endY		Bottom bound (exclusive) of y-axis of the area.
	 */
	void edit(T delta, int beginX, int endX, int beginY, int endY);

	/**
	 * Query the combination of an area [beginX, endX)x[beginY, endY), notice that begin is inclusive while end is exclusive.
	 * @param beginX	Left bound (inclusive) of x-axis of the area.
	 * @param endX		Right bound (exclusive) of x-axis of the area.
	 * @param beginY	Top bound (inclusive) of y-axis of the area.
	 * @param endY		Bottom bound (exclusive) of y-axis of the area.
	 * @return			The combination of an area.
	 */
	T query(int beginX, int endX, int beginY, int endY);

	IEditRule<T> getEditRule();

	int sideSize(int dimension);
	int totalSize();

	void visit(int beginX, int endX, int beginY, int endY, VisitConsumer<T> consumer);

	static <T> ArrayQuadSegmentTree2D<T> newArrayQuadSegmentTree2D(int length, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		return new ArrayQuadSegmentTree2D<>(length, editRule, sizedArray);
	}
	static <T> ArrayQuadSegmentTree2D<T> newArrayQuadSegmentTree2D(T[][] array, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		return new ArrayQuadSegmentTree2D<>(array, editRule, sizedArray);
	}

	@FunctionalInterface
	interface VisitConsumer<T> {
		void accept(int x, int y, T value);
	}
}
