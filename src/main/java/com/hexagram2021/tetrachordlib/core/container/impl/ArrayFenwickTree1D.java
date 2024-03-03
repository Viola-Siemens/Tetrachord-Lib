package com.hexagram2021.tetrachordlib.core.container.impl;

import com.hexagram2021.tetrachordlib.core.algorithm.Algorithm;
import com.hexagram2021.tetrachordlib.core.container.FenwickTree1D;
import com.hexagram2021.tetrachordlib.core.container.IEditRule;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class ArrayFenwickTree1D<T> implements FenwickTree1D<T> {
	private T build(int index) {
		int lb = Algorithm.lowbit(index + 1);
		if(lb == 1) {
			this.value[index] = this.editRule.elementDefault();
			return this.value[index];
		}
		T left = this.build(index - (lb >> 1));
		this.value[index] = this.editRule.combine(left, this.editRule.elementDefault());
		return this.editRule.combine(this.value[index], this.build(index + (lb >> 1)));
	}
	private T build(T[] a, int index) {
		int lb = Algorithm.lowbit(index + 1);
		if(lb == 1) {
			if(index < a.length) {
				this.value[index] = a[index];
			} else {
				this.value[index] = this.editRule.elementDefault();
			}
			return this.value[index];
		}
		T left = this.build(a, index - (lb >> 1));
		T current = index < a.length ? a[index] : this.editRule.elementDefault();
		this.value[index] = this.editRule.combine(left, current);
		return this.editRule.combine(this.value[index], this.build(a, index + (lb >> 1)));
	}

	private T visit(int index, int length, Consumer<T> consumer) {
		int lb = Algorithm.lowbit(index + 1);
		if(lb == 1) {
			if(index < length) {
				consumer.accept(this.value[index]);
			}
			return this.value[index];
		}
		T left = this.visit(index - (lb >> 1), length, consumer);
		if(index < length) {
			consumer.accept(this.editRule.subtract(this.value[index], left));
		}
		return this.editRule.combine(this.value[index], this.visit(index + (lb >> 1), length, consumer));
	}

	private final IEditRule<T> editRule;
	private final int size;
	private final T[] value;


	public ArrayFenwickTree1D(int length, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		this.editRule = editRule;
		if(length <= 0) {
			throw new IllegalArgumentException("Cannot build a fenwick tree with length %d.".formatted(length));
		}
		int hb = Algorithm.highbit(length - 1);
		this.size = hb << 1;
		this.value = sizedArray.get(this.size);
		int lb = Algorithm.lowbit(this.size);
		this.value[this.size - 1] = this.editRule.combine(this.build(this.size - 1 - (lb >> 1)), this.editRule.elementDefault());
	}
	public ArrayFenwickTree1D(T[] array, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		this.editRule = editRule;
		if(array.length == 0) {
			throw new IllegalArgumentException("Cannot build a fenwick tree with length 0.");
		}
		int hb = Algorithm.highbit(array.length - 1);
		this.size = hb << 1;
		this.value = sizedArray.get(this.size);
		int lb = Algorithm.lowbit(this.size);
		T current = this.size - 1 < array.length ? array[this.size - 1] : this.editRule.elementDefault();
		this.value[this.size - 1] = this.editRule.combine(this.build(array, this.size - 1 - (lb >> 1)), current);
	}

	@Override
	public void edit(T delta, int index) {
		while(index < this.size) {
			this.value[index] = this.editRule.edit(this.value[index], delta, 1);
			index += Algorithm.lowbit(index + 1);
		}
	}

	@Override
	public T query(int length) {
		T ret = this.value[length];
		length -= Algorithm.lowbit(length + 1);
		while(length >= 0) {
			ret = this.editRule.combine(ret, this.value[length]);
			length -= Algorithm.lowbit(length + 1);
		}
		return ret;
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
	public void visit(int length, Consumer<T> consumer) {
		int lb = Algorithm.lowbit(this.size);
		T left = this.visit(this.size - 1 - (lb >> 1), length, consumer);
		if(this.size - 1 < length) {
			consumer.accept(this.editRule.subtract(this.value[this.size - 1], left));
		}
	}
}
