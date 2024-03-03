package com.hexagram2021.tetrachordlib.core.container.impl;

import com.hexagram2021.tetrachordlib.core.container.IMultidimensional;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;

public class DoublePosition implements IMultidimensional<Double> {
	final double[] dimensions;

	public DoublePosition(double... dimensions) {
		this.dimensions = Arrays.copyOf(dimensions, dimensions.length);
	}

	@Override
	public int getDimensionSize() {
		return this.dimensions.length;
	}

	@Override
	public Double getDimension(int dimension) {
		return this.dimensions[dimension];
	}

	@Override
	public void setDimension(int dimension, Double value) {
		this.dimensions[dimension] = value;
	}

	@Override
	public double distanceWith(IMultidimensional<Double> md) {
		if(this.getDimensionSize() != md.getDimensionSize()) {
			throw new IllegalArgumentException(String.format("This %d-dimension object cannot compare distance with %d-dimension object.", this.getDimensionSize(), md.getDimensionSize()));
		}
		double ret = 0;
		for(int i = 0; i < this.getDimensionSize(); ++i) {
			double diff = this.dimensions[i] - md.getDimension(i);
			ret += diff * diff;
		}
		return Math.sqrt(ret);
	}

	@Override
	public double lowerboundDistanceWith(IMultidimensional<Double> max, IMultidimensional<Double> min) {
		if(this.getDimensionSize() != max.getDimensionSize() || this.getDimensionSize() != min.getDimensionSize()) {
			throw new IllegalArgumentException(String.format("This %d-dimension object cannot compare lowerbound distance with hyper-rectangle of %d/%d-dimension.", this.getDimensionSize(), max.getDimensionSize(), min.getDimensionSize()));
		}
		double ret = 0;
		for(int i = 0; i < this.getDimensionSize(); ++i) {
			double diff1 = this.dimensions[i] - max.getDimension(i);
			double diff2 = min.getDimension(i) - this.dimensions[i];
			if(diff1 < 0) diff1 = 0;
			if(diff2 < 0) diff2 = 0;
			double diff = diff1 + diff2;
			ret += diff * diff;
		}
		return Math.sqrt(ret);
	}
	@Override
	public double upperboundDistanceWith(IMultidimensional<Double> max, IMultidimensional<Double> min) {
		if(this.getDimensionSize() != max.getDimensionSize() || this.getDimensionSize() != min.getDimensionSize()) {
			throw new IllegalArgumentException(String.format("This %d-dimension object cannot compare upperbound distance with hyper-rectangle of %d/%d-dimension.", this.getDimensionSize(), max.getDimensionSize(), min.getDimensionSize()));
		}
		double ret = 0;
		for(int i = 0; i < this.getDimensionSize(); ++i) {
			double diff1 = this.dimensions[i] - max.getDimension(i);
			double diff2 = min.getDimension(i) - this.dimensions[i];
			diff1 = diff1 * diff1;
			diff2 = diff2 * diff2;
			ret += Math.max(diff1, diff2);
		}
		return Math.sqrt(ret);
	}

	@Override
	public Iterator<Double> iterator() {
		return Arrays.stream(this.dimensions).iterator();
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public DoublePosition clone() {
		return new DoublePosition(this.dimensions);
	}
	@Override
	public boolean equals(@Nullable Object obj) {
		if(obj instanceof DoublePosition) {
			DoublePosition md = (DoublePosition)obj;
			if (this.getDimensionSize() == md.getDimensionSize()) {
				for (int i = 0; i < this.getDimensionSize(); ++i) {
					if (this.dimensions[i] != md.getDimension(i)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.dimensions);
	}

	@Override
	public DoublePosition add(IMultidimensional<Double> other) {
		DoublePosition ret = new DoublePosition(this.dimensions);
		for(int i = 0; i < ret.dimensions.length; ++i) {
			ret.dimensions[i] += other.getDimension(i);
		}
		return ret;
	}
	@Override
	public DoublePosition minus(IMultidimensional<Double> other) {
		DoublePosition ret = new DoublePosition(this.dimensions);
		for(int i = 0; i < ret.dimensions.length; ++i) {
			ret.dimensions[i] -= other.getDimension(i);
		}
		return ret;
	}
	@Override
	public DoublePosition hadamard(IMultidimensional<Double> other) {
		DoublePosition ret = new DoublePosition(this.dimensions);
		for(int i = 0; i < ret.dimensions.length; ++i) {
			ret.dimensions[i] *= other.getDimension(i);
		}
		return ret;
	}
	@Override
	public Double dot(IMultidimensional<Double> other) {
		double ret = 0;
		for(int i = 0; i < this.dimensions.length; ++i) {
			ret += this.dimensions[i] * other.getDimension(i);
		}
		return ret;
	}
	@Override
	public DoublePosition multiply(Double multiplier) {
		DoublePosition ret = new DoublePosition(this.dimensions);
		for(int i = 0; i < ret.dimensions.length; ++i) {
			ret.dimensions[i] *= multiplier;
		}
		return ret;
	}
	@Override
	public DoublePosition divide(double divider) {
		DoublePosition ret = new DoublePosition(this.dimensions);
		for(int i = 0; i < ret.dimensions.length; ++i) {
			ret.dimensions[i] /= divider;
		}
		return ret;
	}
	@Override
	public DoublePosition asDouble() {
		return this.clone();
	}

	@Override
	public void setMin() {
		Arrays.fill(this.dimensions, Double.NEGATIVE_INFINITY);
	}
	@Override
	public void setMax() {
		Arrays.fill(this.dimensions, Double.POSITIVE_INFINITY);
	}
}
