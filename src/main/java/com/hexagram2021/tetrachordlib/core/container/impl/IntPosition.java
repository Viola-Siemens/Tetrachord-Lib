package com.hexagram2021.tetrachordlib.core.container.impl;

import com.hexagram2021.tetrachordlib.core.container.IMultidimensional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;

public class IntPosition implements IMultidimensional<Integer> {
	final int[] dimensions;

	public IntPosition(int... dimensions) {
		this.dimensions = Arrays.copyOf(dimensions, dimensions.length);
	}

	@Override
	@Contract(pure = true)
	public int getDimensionSize() {
		return this.dimensions.length;
	}

	@Override
	public Integer getDimension(int dimension) {
		return this.dimensions[dimension];
	}

	@Override
	public void setDimension(int dimension, Integer value) {
		this.dimensions[dimension] = value;
	}

	@Override
	@Contract(pure = true)
	public double distanceWith(IMultidimensional<Integer> md) {
		if(this.getDimensionSize() != md.getDimensionSize()) {
			throw new IllegalArgumentException("This %d-dimension object cannot compare distance with %d-dimension object.".formatted(this.getDimensionSize(), md.getDimensionSize()));
		}
		double ret = 0;
		for(int i = 0; i < this.getDimensionSize(); ++i) {
			double diff = this.dimensions[i] - md.getDimension(i);
			ret += diff * diff;
		}
		return Math.sqrt(ret);
	}

	@Override
	@Contract(pure = true)
	public double lowerboundDistanceWith(IMultidimensional<Integer> max, IMultidimensional<Integer> min) {
		if(this.getDimensionSize() != max.getDimensionSize() || this.getDimensionSize() != min.getDimensionSize()) {
			throw new IllegalArgumentException("This %d-dimension object cannot compare lowerbound distance with hyper-rectangle of %d/%d-dimension.".formatted(this.getDimensionSize(), max.getDimensionSize(), min.getDimensionSize()));
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
	@Contract(pure = true)
	public double upperboundDistanceWith(IMultidimensional<Integer> max, IMultidimensional<Integer> min) {
		if(this.getDimensionSize() != max.getDimensionSize() || this.getDimensionSize() != min.getDimensionSize()) {
			throw new IllegalArgumentException("This %d-dimension object cannot compare upperbound distance with hyper-rectangle of %d/%d-dimension.".formatted(this.getDimensionSize(), max.getDimensionSize(), min.getDimensionSize()));
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
	public Iterator<Integer> iterator() {
		return Arrays.stream(this.dimensions).iterator();
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public IntPosition clone() {
		return new IntPosition(this.dimensions);
	}
	@Override
	@Contract(value = "null -> false", pure = true)
	public boolean equals(@Nullable Object obj) {
		if(obj instanceof IntPosition md && this.getDimensionSize() == md.getDimensionSize()) {
			for (int i = 0; i < this.getDimensionSize(); ++i) {
				if(this.dimensions[i] != md.getDimension(i)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	@Override
	@Contract(pure = true)
	public int hashCode() {
		return Arrays.hashCode(this.dimensions);
	}

	@Override
	@Contract(pure = true)
	public IntPosition add(IMultidimensional<Integer> other) {
		IntPosition ret = new IntPosition(this.dimensions);
		for(int i = 0; i < ret.dimensions.length; ++i) {
			ret.dimensions[i] += other.getDimension(i);
		}
		return ret;
	}
	@Override
	@Contract(pure = true)
	public IntPosition minus(IMultidimensional<Integer> other) {
		IntPosition ret = new IntPosition(this.dimensions);
		for(int i = 0; i < ret.dimensions.length; ++i) {
			ret.dimensions[i] -= other.getDimension(i);
		}
		return ret;
	}
	@Override
	@Contract(pure = true)
	public IntPosition hadamard(IMultidimensional<Integer> other) {
		IntPosition ret = new IntPosition(this.dimensions);
		for(int i = 0; i < ret.dimensions.length; ++i) {
			ret.dimensions[i] *= other.getDimension(i);
		}
		return ret;
	}
	@Override
	@Contract(pure = true)
	public Integer dot(IMultidimensional<Integer> other) {
		int ret = 0;
		for(int i = 0; i < this.dimensions.length; ++i) {
			ret += this.dimensions[i] * other.getDimension(i);
		}
		return ret;
	}
	@Override
	@Contract(pure = true)
	public IntPosition multiply(Integer multiplier) {
		IntPosition ret = new IntPosition(this.dimensions);
		for(int i = 0; i < ret.dimensions.length; ++i) {
			ret.dimensions[i] *= multiplier;
		}
		return ret;
	}
	@Override
	@Contract(pure = true)
	public DoublePosition divide(double divider) {
		DoublePosition ret = this.asDouble();
		for(int i = 0; i < ret.dimensions.length; ++i) {
			ret.dimensions[i] /= divider;
		}
		return ret;
	}
	@Override
	@Contract(pure = true)
	public DoublePosition asDouble() {
		return new DoublePosition(Arrays.stream(this.dimensions).asDoubleStream().toArray());
	}
}
