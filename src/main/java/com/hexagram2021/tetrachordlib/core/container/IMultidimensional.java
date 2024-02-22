package com.hexagram2021.tetrachordlib.core.container;

import java.util.Iterator;

@SuppressWarnings("unused")
public interface IMultidimensional<T extends Comparable<T>> extends Iterable<T> {
	/**
	 * @return	Size of dimension.<br>
	 * 			For example, point (x, y, z) has 3 dimensions.
	 */
	int getDimensionSize();

	/**
	 * @param dimension		Dimension of the object.
	 * @return				The value of the dimension.<br>
	 * 						For example, dimension 0 of point (x, y, z) is x, and dimension 2 of (x, y, h, w) is h.
	 */
	T getDimension(int dimension);

	/**
	 * @param dimension		Dimension of the object.
	 * @param value 		New value of this dimension.
	 */
	void setDimension(int dimension, T value);

	@Override
	Iterator<T> iterator();

	/**
	 * Distance between two IMultiDimensional objects.
	 * @param md	Another object.
	 * @return		Distance between this and md.
	 */
	double distanceWith(IMultidimensional<T> md);

	/**
	 * The lowerbound distance to a hyper-rectangle.
	 * @param max 	Max value of each dimension, i.e. the right top point of the rectangle.
	 * @param min 	Min value of each dimension, i.e. the left bottom point of the rectangle.
	 * @return		Lowerbound distance.
	 */
	double lowerboundDistanceWith(IMultidimensional<T> max, IMultidimensional<T> min);
	/**
	 * The upperbound distance to a hyper-rectangle.
	 * @param max 	Max value of each dimension, i.e. the right top point of the rectangle.
	 * @param min 	Min value of each dimension, i.e. the left bottom point of the rectangle.
	 * @return		Upperbound distance.
	 */
	double upperboundDistanceWith(IMultidimensional<T> max, IMultidimensional<T> min);

	@Override
	boolean equals(Object obj);
	@Override
	int hashCode();

	IMultidimensional<T> clone();

	IMultidimensional<T> add(IMultidimensional<T> other);
	IMultidimensional<T> minus(IMultidimensional<T> other);
	IMultidimensional<T> hadamard(IMultidimensional<T> other);
	T dot(IMultidimensional<T> other);
	IMultidimensional<T> multiply(T multiplier);
	IMultidimensional<Double> divide(double divider);
	IMultidimensional<Double> asDouble();

	void setMin();
	void setMax();
}
