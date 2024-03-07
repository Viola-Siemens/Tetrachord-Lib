package com.hexagram2021.tetrachordlib.core.container;

import com.hexagram2021.tetrachordlib.core.container.impl.ArrayFenwickTree1D;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface FenwickTree1D<T> {
	/**
	 * Edit a value from index.
	 * @param delta		Amount of the edit.
	 * @param index		The index of the value to be edited.
	 */
	void edit(T delta, int index);
	/**
	 * Query the combination of a segment [begin, end), notice that begin is inclusive while end is exclusive.
	 * @param begin		Left bound (inclusive) of the segment.
	 * @param end		Right bound (exclusive) of the segment.
	 * @return			The combination of a segment.
	 */
	default T query(int begin, int end) {
		if(begin == 0) {
			return this.query(end - 1);
		}
		return this.getEditRule().subtract(this.query(end - 1), this.query(begin - 1));
	}
	/**
	 * Query the prefix sum of a segment [0, length], notice that length is inclusive.
	 * @param length	Length of the prefix sum.
	 * @return			The prefix sum.
	 */
	T query(int length);

	/**
	 * <code>subtract(T left, T right)</code> method should be implemented.
	 */
	IEditRule<T> getEditRule();

	int size();

	void visit(int length, Consumer<T> consumer);

	static <T> ArrayFenwickTree1D<T> newArrayFenwickTree1D(int length, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		return new ArrayFenwickTree1D<>(length, editRule, sizedArray);
	}
	static <T> ArrayFenwickTree1D<T> newArrayFenwickTree1D(T[] array, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		return new ArrayFenwickTree1D<>(array, editRule, sizedArray);
	}
}
