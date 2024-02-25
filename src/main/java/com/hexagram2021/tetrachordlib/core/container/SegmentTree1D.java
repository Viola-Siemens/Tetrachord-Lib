package com.hexagram2021.tetrachordlib.core.container;

import com.hexagram2021.tetrachordlib.core.container.impl.ArraySegmentTree1D;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

import java.util.function.Consumer;

/**
 * Segment Tree is an efficient data structure to query a combination of ranges of an array.
 * @param <T>	The type of value that this SegmentTree maintains.
 */
@SuppressWarnings("unused")
public interface SegmentTree1D<T> {
	/**
	 * Edit a segment [begin, end), notice that begin is inclusive while end is exclusive.
	 * @param delta		Amount of the edit.
	 * @param begin		Left bound (inclusive) of the segment.
	 * @param end		Right bound (exclusive) of the segment.
	 */
	void edit(T delta, int begin, int end);

	/**
	 * Query the combination of a segment [begin, end), notice that begin is inclusive while end is exclusive.
	 * @param begin		Left bound (inclusive) of the segment.
	 * @param end		Right bound (exclusive) of the segment.
	 * @return			The combination of a segment.
	 */
	T query(int begin, int end);

	IEditRule<T> getEditRule();

	int size();

	void visit(int begin, int end, Consumer<T> consumer);

	static <T> ArraySegmentTree1D<T> newArraySegmentTree1D(int length, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		return new ArraySegmentTree1D<>(length, editRule, sizedArray);
	}
	static <T> ArraySegmentTree1D<T> newArraySegmentTree1D(T[] array, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		return new ArraySegmentTree1D<>(array, editRule, sizedArray);
	}
}
