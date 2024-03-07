package com.hexagram2021.tetrachordlib.core.container.impl;

import com.hexagram2021.tetrachordlib.core.algorithm.Algorithm;
import com.hexagram2021.tetrachordlib.core.container.IEditRule;
import com.hexagram2021.tetrachordlib.core.container.SegmentTree2D;
import com.hexagram2021.tetrachordlib.core.container.VisitConsumer2D;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

import java.util.Arrays;

/**
 * Notice that this data structure will make an n*n area to store data. For example, input array is 6*31, and the tree will be 32*32.
 * Be careful when using this to store "thin and long" areas to prevent memory overloaded.
 */
public class ArrayQuadSegmentTree2D<T> implements SegmentTree2D<T> {
	private static int leftUpChild(int index) {
		return (index << 2) + 1;
	}
	private static int rightUpChild(int index) {
		return (index << 2) + 2;
	}
	private static int leftDownChild(int index) {
		return (index << 2) + 3;
	}
	private static int rightDownChild(int index) {
		return (index << 2) + 4;
	}
	private boolean isLeaf(int index) {
		return ((index * 3 + 1) & (this.size * this.size * 0x3)) != 0;
	}

	private void build(T[][] a, int index) {
		if(isLeaf(index)) {
			int arrIndex = index * 3 + 1;
			int hb4 = Algorithm.highbit(arrIndex);
			if((hb4 & 0x55555555) == 0) {
				hb4 >>= 1;
			}
			arrIndex = (arrIndex - hb4) / 3;
			int adder = 1;
			int arrYIndex = 0, arrXIndex = 0;
			while(arrIndex > 0) {
				int ad4 = arrIndex & 0x3;
				arrIndex >>= 2;
				if((ad4 & 0x2) != 0) {
					arrYIndex += adder;
				}
				if((ad4 & 0x1) != 0) {
					arrXIndex += adder;
				}
				adder <<= 2;
			}
			if(arrYIndex < a.length && arrXIndex < a[arrYIndex].length) {
				this.value[index] = a[arrYIndex][arrXIndex];
			} else {
				this.value[index] = this.editRule.elementDefault();
			}
			return;
		}
		int luIndex = leftUpChild(index);
		int ruIndex = rightUpChild(index);
		int ldIndex = leftDownChild(index);
		int rdIndex = rightDownChild(index);
		this.build(a, luIndex);
		this.build(a, ruIndex);
		this.build(a, ldIndex);
		this.build(a, rdIndex);
		this.value[index] = this.editRule.combine(this.value[luIndex], this.value[ruIndex], this.value[ldIndex], this.value[rdIndex]);
		this.adder[index] = this.editRule.zero();
	}

	private void build(int index) {
		if(isLeaf(index)) {
			this.value[index] = this.editRule.elementDefault();
			return;
		}
		int luIndex = leftUpChild(index);
		int ruIndex = rightUpChild(index);
		int ldIndex = leftDownChild(index);
		int rdIndex = rightDownChild(index);
		this.build(luIndex);
		this.build(ruIndex);
		this.build(ldIndex);
		this.build(rdIndex);
		this.value[index] = this.editRule.combine(this.value[luIndex], this.value[ruIndex], this.value[ldIndex], this.value[rdIndex]);
		this.adder[index] = this.editRule.zero();
	}

	private void editFullRange(int index, T delta, int length) {
		if(length == 1) {
			this.value[index] = this.editRule.edit(this.value[index], delta, 1, 1);
			return;
		}
		this.adder[index] = this.editRule.update(this.adder[index], delta);
	}

	private T queryFullRange(int index, int length) {
		if(length == 1) {
			return this.value[index];
		}
		return this.editRule.edit(this.value[index], this.adder[index], length, length);
	}

	private void edit(T delta, int index, int leftBound, int topBound, int length, int beginX, int endX, int beginY, int endY) {
		assert(length > 0 && leftBound <= beginX && leftBound + length >= endX && topBound <= beginY && topBound + length >= endY);
		if(beginX == leftBound && endX == leftBound + length && beginY == topBound && endY == topBound + length) {
			this.editFullRange(index, delta, length);
			return;
		}
		int halfLength = length >> 1;
		boolean toLeft = beginX < leftBound + halfLength;
		boolean toRight = endX > leftBound + halfLength;
		boolean toTop = beginY < topBound + halfLength;
		boolean toBottom = endY > topBound + halfLength;
		int luIndex = leftUpChild(index);
		int ruIndex = rightUpChild(index);
		int ldIndex = leftDownChild(index);
		int rdIndex = rightDownChild(index);
		this.value[index] = this.editRule.edit(this.value[index], this.adder[index], length, length);
		this.editFullRange(luIndex, this.adder[index], halfLength);
		this.editFullRange(ruIndex, this.adder[index], halfLength);
		this.editFullRange(ldIndex, this.adder[index], halfLength);
		this.editFullRange(rdIndex, this.adder[index], halfLength);
		this.adder[index] = this.editRule.zero();
		if(toTop) {
			if(toBottom) {
				if(toLeft) {
					if(toRight) {
						this.edit(delta, luIndex, leftBound, topBound, halfLength, beginX, leftBound + halfLength, beginY, topBound + halfLength);
						this.edit(delta, ruIndex, leftBound + halfLength, topBound, halfLength, leftBound + halfLength, endX, beginY, topBound + halfLength);
						this.edit(delta, ldIndex, leftBound, topBound + halfLength, halfLength, beginX, leftBound + halfLength, topBound + halfLength, endY);
						this.edit(delta, rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, leftBound + halfLength, endX, topBound + halfLength, endY);
					} else {
						this.edit(delta, luIndex, leftBound, topBound, halfLength, beginX, endX, beginY, topBound + halfLength);
						this.edit(delta, ldIndex, leftBound, topBound + halfLength, halfLength, beginX, endX, topBound + halfLength, endY);
					}
				} else if(toRight) {
					this.edit(delta, ruIndex, leftBound + halfLength, topBound, halfLength, beginX, endX, beginY, topBound + halfLength);
					this.edit(delta, rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, beginX, endX, topBound + halfLength, endY);
				}
			} else {
				if(toLeft) {
					if(toRight) {
						this.edit(delta, luIndex, leftBound, topBound, halfLength, beginX, leftBound + halfLength, beginY, endY);
						this.edit(delta, ruIndex, leftBound + halfLength, topBound, halfLength, leftBound + halfLength, endX, beginY, endY);
					} else {
						this.edit(delta, luIndex, leftBound, topBound, halfLength, beginX, endX, beginY, endY);
					}
				} else if(toRight) {
					this.edit(delta, ruIndex, leftBound + halfLength, topBound, halfLength, beginX, endX, beginY, endY);
				}
			}
		} else if(toBottom) {
			if(toLeft) {
				if(toRight) {
					this.edit(delta, ldIndex, leftBound, topBound + halfLength, halfLength, beginX, leftBound + halfLength, beginY, endY);
					this.edit(delta, rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, leftBound + halfLength, endX, beginY, endY);
				} else {
					this.edit(delta, ldIndex, leftBound, topBound + halfLength, halfLength, beginX, endX, beginY, endY);
				}
			} else if(toRight) {
				this.edit(delta, rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, beginX, endX, beginY, endY);
			}
		}

		this.value[index] = this.editRule.combine(
				this.queryFullRange(luIndex, halfLength), this.queryFullRange(ruIndex, halfLength),
				this.queryFullRange(ldIndex, halfLength), this.queryFullRange(rdIndex, halfLength)
		);
	}

	private T query(int index, int leftBound, int topBound, int length, int beginX, int endX, int beginY, int endY) {
		assert(length > 0 && leftBound <= beginX && leftBound + length >= endX && topBound <= beginY && topBound + length >= endY);
		if(beginX == leftBound && endX == leftBound + length && beginY == topBound && endY == topBound + length) {
			return this.queryFullRange(index, length);
		}
		int halfLength = length >> 1;
		boolean toLeft = beginX < leftBound + halfLength;
		boolean toRight = endX > leftBound + halfLength;
		boolean toTop = beginY < topBound + halfLength;
		boolean toBottom = endY > topBound + halfLength;
		int luIndex = leftUpChild(index);
		int ruIndex = rightUpChild(index);
		int ldIndex = leftDownChild(index);
		int rdIndex = rightDownChild(index);
		T result = null;
		if(toTop) {
			if(toBottom) {
				if(toLeft) {
					if(toRight) {
						result = this.editRule.combine(
								this.query(luIndex, leftBound, topBound, halfLength, beginX, leftBound + halfLength, beginY, topBound + halfLength),
								this.query(ruIndex, leftBound + halfLength, topBound, halfLength, leftBound + halfLength, endX, beginY, topBound + halfLength),
								this.query(ldIndex, leftBound, topBound + halfLength, halfLength, beginX, leftBound + halfLength, topBound + halfLength, endY),
								this.query(rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, leftBound + halfLength, endX, topBound + halfLength, endY)
						);
					} else {
						result = this.editRule.combine(
								this.query(luIndex, leftBound, topBound, halfLength, beginX, endX, beginY, topBound + halfLength),
								this.query(ldIndex, leftBound, topBound + halfLength, halfLength, beginX, endX, topBound + halfLength, endY)
						);
					}
				} else if(toRight) {
					result = this.editRule.combine(
							this.query(ruIndex, leftBound + halfLength, topBound, halfLength, beginX, endX, beginY, topBound + halfLength),
							this.query(rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, beginX, endX, topBound + halfLength, endY)
					);
				}
			} else {
				if(toLeft) {
					if(toRight) {
						result = this.editRule.combine(
								this.query(luIndex, leftBound, topBound, halfLength, beginX, leftBound + halfLength, beginY, endY),
								this.query(ruIndex, leftBound + halfLength, topBound, halfLength, leftBound + halfLength, endX, beginY, endY)
						);
					} else {
						result = this.query(luIndex, leftBound, topBound, halfLength, beginX, endX, beginY, endY);
					}
				} else if(toRight) {
					result = this.query(ruIndex, leftBound + halfLength, topBound, halfLength, beginX, endX, beginY, endY);
				}
			}
		} else if(toBottom) {
			if(toLeft) {
				if(toRight) {
					result = this.editRule.combine(
							this.query(ldIndex, leftBound, topBound + halfLength, halfLength, beginX, leftBound + halfLength, beginY, endY),
							this.query(rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, leftBound + halfLength, endX, beginY, endY)
					);
				} else {
					result = this.query(ldIndex, leftBound, topBound + halfLength, halfLength, beginX, endX, beginY, endY);
				}
			} else if(toRight) {
				result = this.query(rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, beginX, endX, beginY, endY);
			}
		}
		assert result != null;
		return this.editRule.edit(result, this.adder[index], endX - beginX, endY - beginY);
	}

	private void visit(int index, int leftBound, int topBound, int length, int beginX, int endX, int beginY, int endY, VisitConsumer2D<T> consumer) {
		assert(length > 0 && leftBound <= beginX && leftBound + length >= endX && topBound <= beginY && topBound + length >= endY);
		if(this.isLeaf(index)) {
			consumer.visit(leftBound, topBound, this.value[index]);
			return;
		}
		int halfLength = length >> 1;
		boolean toLeft = beginX < leftBound + halfLength;
		boolean toRight = endX > leftBound + halfLength;
		boolean toTop = beginY < topBound + halfLength;
		boolean toBottom = endY > topBound + halfLength;
		int luIndex = leftUpChild(index);
		int ruIndex = rightUpChild(index);
		int ldIndex = leftDownChild(index);
		int rdIndex = rightDownChild(index);
		this.value[index] = this.editRule.edit(this.value[index], this.adder[index], length, length);
		this.editFullRange(luIndex, this.adder[index], halfLength);
		this.editFullRange(ruIndex, this.adder[index], halfLength);
		this.editFullRange(ldIndex, this.adder[index], halfLength);
		this.editFullRange(rdIndex, this.adder[index], halfLength);
		this.adder[index] = this.editRule.zero();
		if(toTop) {
			if(toBottom) {
				if(toLeft) {
					if(toRight) {
						this.visit(luIndex, leftBound, topBound, halfLength, beginX, leftBound + halfLength, beginY, topBound + halfLength, consumer);
						this.visit(ruIndex, leftBound + halfLength, topBound, halfLength, leftBound + halfLength, endX, beginY, topBound + halfLength, consumer);
						this.visit(ldIndex, leftBound, topBound + halfLength, halfLength, beginX, leftBound + halfLength, topBound + halfLength, endY, consumer);
						this.visit(rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, leftBound + halfLength, endX, topBound + halfLength, endY, consumer);
					} else {
						this.visit(luIndex, leftBound, topBound, halfLength, beginX, endX, beginY, topBound + halfLength, consumer);
						this.visit(ldIndex, leftBound, topBound + halfLength, halfLength, beginX, endX, topBound + halfLength, endY, consumer);
					}
				} else if(toRight) {
					this.visit(ruIndex, leftBound + halfLength, topBound, halfLength, beginX, endX, beginY, topBound + halfLength, consumer);
					this.visit(rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, beginX, endX, topBound + halfLength, endY, consumer);
				}
			} else {
				if(toLeft) {
					if(toRight) {
						this.visit(luIndex, leftBound, topBound, halfLength, beginX, leftBound + halfLength, beginY, endY, consumer);
						this.visit(ruIndex, leftBound + halfLength, topBound, halfLength, leftBound + halfLength, endX, beginY, endY, consumer);
					} else {
						this.visit(luIndex, leftBound, topBound, halfLength, beginX, endX, beginY, endY, consumer);
					}
				} else if(toRight) {
					this.visit(ruIndex, leftBound + halfLength, topBound, halfLength, beginX, endX, beginY, endY, consumer);
				}
			}
		} else if(toBottom) {
			if(toLeft) {
				if(toRight) {
					this.visit(ldIndex, leftBound, topBound + halfLength, halfLength, beginX, leftBound + halfLength, beginY, endY, consumer);
					this.visit(rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, leftBound + halfLength, endX, beginY, endY, consumer);
				} else {
					this.visit(ldIndex, leftBound, topBound + halfLength, halfLength, beginX, endX, beginY, endY, consumer);
				}
			} else if(toRight) {
				this.visit(rdIndex, leftBound + halfLength, topBound + halfLength, halfLength, beginX, endX, beginY, endY, consumer);
			}
		}
	}

	private final IEditRule<T> editRule;
	private final int size;
	private final T[] value;
	private final T[] adder;


	public ArrayQuadSegmentTree2D(int length, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		this.editRule = editRule;
		if(length <= 0) {
			throw new IllegalArgumentException(String.format("Cannot build a 2d segment tree with length %d.", length));
		}
		int hb = Algorithm.highbit(length);
		this.size = hb << 1;
		int size = this.size * this.size;
		this.value = sizedArray.get(((size << 2) - 1) / 3 + 3);
		this.adder = sizedArray.get((size - 1) / 3 + 3);
		this.build(0);
	}
	public ArrayQuadSegmentTree2D(T[][] array, IEditRule<T> editRule, Int2ObjectFunction<T[]> sizedArray) {
		this.editRule = editRule;
		int length = array.length;
		length = Math.max(length, Arrays.stream(array).mapToInt(a -> a.length).max().orElse(length));
		if(length == 0) {
			throw new IllegalArgumentException("Cannot build a 2d segment tree with length 0.");
		}
		int hb = Algorithm.highbit(length);
		this.size = hb << 1;
		int size = this.size * this.size;
		this.value = sizedArray.get(((size << 2) - 1) / 3 + 3);
		this.adder = sizedArray.get((size - 1) / 3 + 3);
		this.build(array, 0);
	}

	@Override
	public void edit(T delta, int beginX, int endX, int beginY, int endY) {
		if(beginX < 0 || endX >= this.size || beginX >= endX) {
			throw new IllegalArgumentException(String.format("Cannot edit x-axis [%d, %d) from %d-sized 2d segment tree.", beginX, endX, this.size));
		}
		if(beginY < 0 || endY >= this.size || beginY >= endY) {
			throw new IllegalArgumentException(String.format("Cannot edit y-axis [%d, %d) from %d-sized 2d segment tree.", beginY, endY, this.size));
		}
		this.edit(delta, 0, 0, 0, this.size, beginX, endX, beginY, endY);
	}

	@Override
	public T query(int beginX, int endX, int beginY, int endY) {
		if(beginX < 0 || endX >= this.size || beginX >= endX) {
			throw new IllegalArgumentException(String.format("Cannot query x-axis [%d, %d) from %d-sized 2d segment tree.", beginX, endX, this.size));
		}
		if(beginY < 0 || endY >= this.size || beginY >= endY) {
			throw new IllegalArgumentException(String.format("Cannot query y-axis [%d, %d) from %d-sized 2d segment tree.", beginY, endY, this.size));
		}
		return this.query(0, 0, 0, this.size, beginX, endX, beginY, endY);
	}

	@Override
	public IEditRule<T> getEditRule() {
		return this.editRule;
	}

	@Override
	public int sideSize(int dimension) {
		return this.size;
	}
	@Override
	public int totalSize() {
		return this.size * this.size;
	}

	@Override
	public void visit(int beginX, int endX, int beginY, int endY, VisitConsumer2D<T> consumer) {
		this.visit(0, 0, 0, this.size, beginX, endX, beginY, endY, consumer);
	}
}
