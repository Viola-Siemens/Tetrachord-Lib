package com.hexagram2021.tetrachordlib.core.container;

import com.hexagram2021.tetrachordlib.core.container.impl.ArrayFenwickTree2D;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
public interface FenwickTree2D<T> {
	/**
	 * Edit a value from index.
	 * @param delta		Amount of the edit.
	 * @param x			The x index of the value to be edited.
	 * @param y			The y index of the value to be edited.
	 */
	void edit(T delta, int x, int y);
	/**
	 * Query the combination of an area [beginX, endX)x[beginY, endY), notice that begin is inclusive while end is exclusive.
	 * @param beginX	Left bound (inclusive) of x-axis of the area.
	 * @param endX		Right bound (exclusive) of x-axis of the area.
	 * @param beginY	Top bound (inclusive) of y-axis of the area.
	 * @param endY		Bottom bound (exclusive) of y-axis of the area.
	 * @return			The combination of an area.
	 */
	default T query(int beginX, int endX, int beginY, int endY) {
		if(beginX == 0) {
			if(beginY == 0) {
				return this.query(endX - 1, endY - 1);
			}
			return this.getEditRule().subtract(this.query(endX - 1, endY - 1), this.query(endX - 1, beginY - 1));
		}
		if(beginY == 0) {
			return this.getEditRule().subtract(this.query(endX - 1, endY - 1), this.query(beginX - 1, endY - 1));
		}
		return this.getEditRule().subtract(
				this.getEditRule().combine(this.query(endX - 1, endY - 1), this.query(beginX - 1, beginY - 1)),
				this.getEditRule().combine(this.query(beginX - 1, endY - 1), this.query(endX - 1, beginY - 1))
		);
	}
	/**
	 * Query the prefix sum of an area [0, lengthX]x[0, lengthY], notice that length is inclusive.
	 * @param lengthX	x length of the prefix sum.
	 * @param lengthY	y length of the prefix sum.
	 * @return			The prefix sum.
	 */
	T query(int lengthX, int lengthY);

	/**
	 * <code>subtract(T left, T right)</code> method should be implemented.
	 */
	IEditRule<T> getEditRule();

	int sizeX();
	int sizeY();

	void visit(int lengthX, int lengthY, VisitConsumer2D<T> consumer);

	static <T> ArrayFenwickTree2D<T> newArrayFenwickTree2D(int lengthX, int lengthY, IEditRule<T> editRule, ToSized2DArray<T> sizedArray) {
		return new ArrayFenwickTree2D<>(lengthX, lengthY, editRule, sizedArray);
	}
	static <T> ArrayFenwickTree2D<T> newArrayFenwickTree2D(T[][] array, IEditRule<T> editRule, ToSized2DArray<T> sizedArray) {
		return new ArrayFenwickTree2D<>(array, editRule, sizedArray);
	}

	interface ToSized2DArray<T> extends BiFunction<Integer, Integer, T[][]> {
		T[][] get(int lengthX, int lengthY);

		@Override @Deprecated
		default T[][] apply(Integer x, Integer y) {
			return this.get(x, y);
		}
	}
}
