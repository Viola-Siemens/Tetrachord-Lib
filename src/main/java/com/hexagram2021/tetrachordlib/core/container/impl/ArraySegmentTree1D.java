package com.hexagram2021.tetrachordlib.core.container.impl;

import com.hexagram2021.tetrachordlib.core.algorithm.Algorithm;
import com.hexagram2021.tetrachordlib.core.container.IEditRule;
import com.hexagram2021.tetrachordlib.core.container.SegmentTree1D;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

import java.util.function.Consumer;

public class ArraySegmentTree1D<T> implements SegmentTree1D<T> {
	private static int leftChild(int index) {
		return (index << 1) + 1;
	}
	private static int rightChild(int index) {
		return (index << 1) + 2;
	}
	private boolean isLeaf(int index) {
		return ((index + 1) & this.size) != 0;
	}

	private void build(T[] a, int index) {
		if(isLeaf(index)) {
			int arrIndex = index + 1 - Algorithm.highbit(index + 1);
			if(arrIndex < a.length) {
				this.value[index] = a[arrIndex];
			} else {
				this.value[index] = this.editRule.elementDefault();
			}
			return;
		}
		int lIndex = leftChild(index);
		int rIndex = rightChild(index);
		this.build(a, lIndex);
		this.build(a, rIndex);
		this.value[index] = this.editRule.combine(this.value[lIndex], this.value[rIndex]);
		this.adder[index] = this.editRule.zero();
	}

	private void build(int index) {
		if(isLeaf(index)) {
			this.value[index] = this.editRule.elementDefault();
			return;
		}
		int lIndex = leftChild(index);
		int rIndex = rightChild(index);
		this.build(lIndex);
		this.build(rIndex);
		this.value[index] = this.editRule.combine(this.value[lIndex], this.value[rIndex]);
		this.adder[index] = this.editRule.zero();
	}

	private void editFullRange(int index, T delta, int length) {
		if(length == 1) {
			this.value[index] = this.editRule.edit(this.value[index], delta, 1);
			return;
		}
		this.adder[index] = this.editRule.update(this.adder[index], delta);
	}

	private T queryFullRange(int index, int length) {
		if(length == 1) {
			return this.value[index];
		}
		return this.editRule.edit(this.value[index], this.adder[index], length);
	}

	private void edit(T delta, int index, int leftBound, int rightBound, int begin, int end) {
		assert(rightBound - leftBound > 0 && leftBound <= begin && rightBound >= end);
		if(begin == leftBound && end == rightBound) {
			this.editFullRange(index, delta, end - begin);
			return;
		}
		int mid = (leftBound + rightBound) >> 1;
		boolean toLeft = begin < mid;
		boolean toRight = end > mid;
		this.value[index] = this.editRule.edit(this.value[index], this.adder[index], rightBound - leftBound);
		this.editFullRange(leftChild(index), this.adder[index], mid - leftBound);
		this.editFullRange(rightChild(index), this.adder[index], rightBound - mid);
		this.adder[index] = this.editRule.zero();
		if(toLeft) {
			if(toRight) {
				this.edit(delta, leftChild(index), leftBound, mid, begin, mid);
				this.edit(delta, rightChild(index), mid, rightBound, mid, end);
			} else {
				this.edit(delta, leftChild(index), leftBound, mid, begin, end);
			}
		} else if(toRight) {
			this.edit(delta, rightChild(index), mid, rightBound, begin, end);
		}
		this.value[index] = this.editRule.combine(
				this.queryFullRange(leftChild(index), mid - leftBound),
				this.queryFullRange(rightChild(index), rightBound - mid)
		);
	}

	private T query(int index, int leftBound, int rightBound, int begin, int end) {
		assert(rightBound - leftBound > 0 && leftBound <= begin && rightBound >= end);
		if(begin == leftBound && end == rightBound) {
			return this.queryFullRange(index, end - begin);
		}
		int mid = (leftBound + rightBound) >> 1;
		boolean toLeft = begin < mid;
		boolean toRight = end > mid;
		T result = null;
		if(toLeft) {
			if(toRight) {
				result = this.editRule.combine(this.query(leftChild(index), leftBound, mid, begin, mid), this.query(rightChild(index), mid, rightBound, mid, end));
			} else {
				result = this.query(leftChild(index), leftBound, mid, begin, end);
			}
		} else if(toRight) {
			result = this.query(rightChild(index), mid, rightBound, begin, end);
		}
		assert result != null;
		return this.editRule.edit(result, this.adder[index], end - begin);
	}

	private void visit(int index, int leftBound, int rightBound, int begin, int end, Consumer<T> consumer) {
		assert(rightBound - leftBound > 0 && leftBound <= begin && rightBound >= end);
		if(this.isLeaf(index)) {
			consumer.accept(this.value[index]);
			return;
		}
		int mid = (leftBound + rightBound) >> 1;
		boolean toLeft = begin < mid;
		boolean toRight = end > mid;
		this.value[index] = this.editRule.edit(this.value[index], this.adder[index], rightBound - leftBound);
		this.editFullRange(leftChild(index), this.adder[index], mid - leftBound);
		this.editFullRange(rightChild(index), this.adder[index], rightBound - mid);
		this.adder[index] = this.editRule.zero();
		if(toLeft) {
			if(toRight) {
				this.visit(leftChild(index), leftBound, mid, begin, mid, consumer);
				this.visit(rightChild(index), mid, rightBound, mid, end, consumer);
			} else {
				this.visit(leftChild(index), leftBound, mid, begin, end, consumer);
			}
		} else if(toRight) {
			this.visit(rightChild(index), mid, rightBound, begin, end, consumer);
		}
	}

	private final IEditRule<T> editRule;
	private final int size;
	private final T[] value;
	private final T[] adder;


	public ArraySegmentTree1D(int length, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		this.editRule = editRule;
		if(length <= 0) {
			throw new IllegalArgumentException(String.format("Cannot build a segment tree with length %d.", length));
		}
		int hb = Algorithm.highbit(length);
		this.size = hb << 1;
		this.value = sizedArray.get(this.size << 1);
		this.adder = sizedArray.get(this.size);
		this.build(0);
	}
	public ArraySegmentTree1D(T[] array, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		this.editRule = editRule;
		if(array.length == 0) {
			throw new IllegalArgumentException("Cannot build a segment tree with length 0.");
		}
		int hb = Algorithm.highbit(array.length);
		this.size = hb << 1;
		this.value = sizedArray.get(this.size << 1);
		this.adder = sizedArray.get(this.size);
		this.build(array, 0);
	}

	@Override
	public void edit(T delta, int begin, int end) {
		if(begin < 0 || end >= this.size || begin >= end) {
			throw new IllegalArgumentException(String.format("Cannot edit segment [%d, %d) from %d-sized segment tree.", begin, end, this.size));
		}
		this.edit(delta, 0, 0, this.size, begin, end);
	}

	@Override
	public T query(int begin, int end) {
		if(begin < 0 || end >= this.size || begin >= end) {
			throw new IllegalArgumentException(String.format("Cannot query segment [%d, %d) from %d-sized segment tree.", begin, end, this.size));
		}
		return this.query(0, 0, this.size, begin, end);
	}

	@Override
	public IEditRule<T> getEditRule() {
		return this.editRule;
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public void visit(int begin, int end, Consumer<T> consumer) {
		this.visit(0, 0, this.size, begin, end, consumer);
	}
}
