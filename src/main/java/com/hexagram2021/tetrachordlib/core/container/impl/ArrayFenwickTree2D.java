package com.hexagram2021.tetrachordlib.core.container.impl;

import com.hexagram2021.tetrachordlib.core.algorithm.Algorithm;
import com.hexagram2021.tetrachordlib.core.container.FenwickTree2D;
import com.hexagram2021.tetrachordlib.core.container.IEditRule;
import com.hexagram2021.tetrachordlib.core.container.VisitConsumer2D;

import java.util.Arrays;

@SuppressWarnings("unused")
public class ArrayFenwickTree2D<T> implements FenwickTree2D<T> {
	/**
	 * @return		Combination of area [x-lbx+1, x+lbx-1], [y-lby+1, y].
	 */
	private T buildX(int x, int y) {
		int lbx = Algorithm.lowbit(x + 1);
		int lby = Algorithm.lowbit(y + 1);
		if(lbx == 1) {
			if(lby == 1) {
				this.value[x][y] = this.editRule.elementDefault();
				return this.value[x][y];
			}
			T top = this.buildY(x, y - (lby >> 1));
			this.value[x][y] = this.editRule.combine(top, this.editRule.elementDefault());
			return this.value[x][y];
		}
		T left = this.buildX(x - (lbx >> 1), y);
		if(lby == 1) {
			this.value[x][y] = this.editRule.combine(left, this.editRule.elementDefault());
			return this.editRule.combine(this.value[x][y], this.buildX(x + (lbx >> 1), y));
		}
		T top = this.buildY(x, y - (lby >> 1));
		this.value[x][y] = this.editRule.combine(this.editRule.combine(left, top), this.editRule.elementDefault());
		return this.editRule.combine(this.value[x][y], this.buildX(x + (lbx >> 1), y));
	}
	/**
	 * @return		Combination of area [x, x], [y-lby+1, y+lby-1].
	 */
	private T buildY(int x, int y) {
		int lbx = Algorithm.lowbit(x + 1);
		int lby = Algorithm.lowbit(y + 1);
		if(lbx == 1) {
			if (lby == 1) {
				this.value[x][y] = this.editRule.elementDefault();
				return this.value[x][y];
			}
			T top = this.buildY(x, y - (lby >> 1));
			this.value[x][y] = this.editRule.combine(top, this.editRule.elementDefault());
			return this.editRule.combine(this.value[x][y], this.buildY(x, y + (lby >> 1)));
		}
		T ret;
		if (lby == 1) {
			ret = this.editRule.elementDefault();
			this.value[x][y] = ret;
		} else {
			T top = this.buildY(x, y - (lby >> 1));
			this.value[x][y] = this.editRule.combine(top, this.editRule.elementDefault());
			ret = this.editRule.combine(this.value[x][y], this.buildY(x, y + (lby >> 1)));
		}
		int nextlbx = lbx >> 1;
		while(nextlbx > 0) {
			this.value[x][y] = this.editRule.combine(this.value[x][y], this.value[x - nextlbx][y]);
			nextlbx >>= 1;
		}
		return ret;
	}
	private T buildX(T[][] a, int x, int y) {
		int lbx = Algorithm.lowbit(x + 1);
		int lby = Algorithm.lowbit(y + 1);
		if(lbx == 1) {
			if(lby == 1) {
				if(x < a.length && y < a[x].length) {
					this.value[x][y] = a[x][y];
				} else {
					this.value[x][y] = this.editRule.elementDefault();
				}
				return this.value[x][y];
			}
			T top = this.buildY(a, x, y - (lby >> 1));
			T current = (x < a.length && y < a[x].length) ? a[x][y] : this.editRule.elementDefault();
			this.value[x][y] = this.editRule.combine(top, current);
			return this.value[x][y];
		}
		T left = this.buildX(a, x - (lbx >> 1), y);
		if(lby == 1) {
			T current = (x < a.length && y < a[x].length) ? a[x][y] : this.editRule.elementDefault();
			this.value[x][y] = this.editRule.combine(left, current);
			return this.editRule.combine(this.value[x][y], this.buildX(a, x + (lbx >> 1), y));
		}
		T top = this.buildY(a, x, y - (lby >> 1));
		T current = (x < a.length && y < a[x].length) ? a[x][y] : this.editRule.elementDefault();
		this.value[x][y] = this.editRule.combine(this.editRule.combine(left, top), current);
		return this.editRule.combine(this.value[x][y], this.buildX(a, x + (lbx >> 1), y));
	}
	private T buildY(T[][] a, int x, int y) {
		int lbx = Algorithm.lowbit(x + 1);
		int lby = Algorithm.lowbit(y + 1);
		if(lbx == 1) {
			if (lby == 1) {
				if(x < a.length && y < a[x].length) {
					this.value[x][y] = a[x][y];
				} else {
					this.value[x][y] = this.editRule.elementDefault();
				}
				return this.value[x][y];
			}
			T top = this.buildY(a, x, y - (lby >> 1));
			T current = (x < a.length && y < a[x].length) ? a[x][y] : this.editRule.elementDefault();
			this.value[x][y] = this.editRule.combine(top, current);
			return this.editRule.combine(this.value[x][y], this.buildY(a, x, y + (lby >> 1)));
		}
		T ret;
		if (lby == 1) {
			if(x < a.length && y < a[x].length) {
				ret = a[x][y];
			} else {
				ret = this.editRule.elementDefault();
			}
			this.value[x][y] = ret;
		} else {
			T top = this.buildY(a, x, y - (lby >> 1));
			T current = (x < a.length && y < a[x].length) ? a[x][y] : this.editRule.elementDefault();
			this.value[x][y] = this.editRule.combine(top, current);
			ret = this.editRule.combine(this.value[x][y], this.buildY(a, x, y + (lby >> 1)));
		}
		int nextlbx = lbx >> 1;
		while(nextlbx > 0) {
			this.value[x][y] = this.editRule.combine(this.value[x][y], this.value[x - nextlbx][y]);
			nextlbx >>= 1;
		}
		return ret;
	}

	private T visitX(int x, int y, int lengthX, int lengthY, VisitConsumer2D<T> consumer) {
		int lbx = Algorithm.lowbit(x + 1);
		int lby = Algorithm.lowbit(y + 1);
		if(lbx == 1) {
			if(lby == 1) {
				if(x < lengthX && y < lengthY) {
					consumer.visit(x, y, this.value[x][y]);
				}
				return this.value[x][y];
			}
			T top = this.visitY(x, y - (lby >> 1), lengthX, lengthY, consumer);
			if(x < lengthX && y < lengthY) {
				consumer.visit(x, y, this.editRule.subtract(this.value[x][y], top));
			}
			return this.value[x][y];
		}
		T left = this.visitX(x - (lbx >> 1), y, lengthX, lengthY, consumer);
		if(lby == 1) {
			if(x < lengthX && y < lengthY) {
				consumer.visit(x, y, this.editRule.subtract(this.value[x][y], left));
			}
			return this.editRule.combine(this.value[x][y], this.visitX(x + (lbx >> 1), y, lengthX, lengthY, consumer));
		}
		T top = this.visitY(x, y - (lby >> 1), lengthX, lengthY, consumer);
		if(x < lengthX && y < lengthY) {
			consumer.visit(x, y, this.editRule.subtract(this.value[x][y], this.editRule.combine(left, top)));
		}
		return this.editRule.combine(this.value[x][y], this.visitX(x + (lbx >> 1), y, lengthX, lengthY, consumer));
	}
	private T visitY(int x, int y, int lengthX, int lengthY, VisitConsumer2D<T> consumer) {
		int lbx = Algorithm.lowbit(x + 1);
		int lby = Algorithm.lowbit(y + 1);
		if(lbx == 1) {
			if (lby == 1) {
				if(x < lengthX && y < lengthY) {
					consumer.visit(x, y, this.value[x][y]);
				}
				return this.value[x][y];
			}
			T top = this.visitY(x, y - (lby >> 1), lengthX, lengthY, consumer);
			if(x < lengthX && y < lengthY) {
				consumer.visit(x, y, this.editRule.subtract(this.value[x][y], top));
			}
			return this.editRule.combine(this.value[x][y], this.visitY(x, y + (lby >> 1), lengthX, lengthY, consumer));
		}
		T current = this.value[x][y];
		int nextlbx = lbx >> 1;
		while(nextlbx > 0) {
			current = this.editRule.subtract(current, this.value[x - nextlbx][y]);
			nextlbx >>= 1;
		}
		if (lby == 1) {
			if(x < lengthX && y < lengthY) {
				consumer.visit(x, y, current);
			}
			return current;
		}
		T top = this.visitY(x, y - (lby >> 1), lengthX, lengthY, consumer);
		if(x < lengthX && y < lengthY) {
			consumer.visit(x, y, this.editRule.subtract(current, top));
		}
		return this.editRule.combine(current, this.visitY(x, y + (lby >> 1), lengthX, lengthY, consumer));
	}

	private final IEditRule<T> editRule;
	private final int sizeX;
	private final int sizeY;
	private final T[][] value;


	public ArrayFenwickTree2D(int lengthX, int lengthY, IEditRule<T> editRule, ToSized2DArray<T> sizedArray) {
		this.editRule = editRule;
		if(lengthX <= 0 || lengthY <= 0) {
			throw new IllegalArgumentException(String.format("Cannot build a fenwick tree with length %d and %d.", lengthX, lengthY));
		}
		int sizeX = Algorithm.highbit(lengthX - 1) << 1;
		int sizeY = Algorithm.highbit(lengthY - 1) << 1;
		this.sizeX = Math.max(sizeX, 0x10);
		this.sizeY = Math.max(sizeY, 0x10);
		this.value = sizedArray.get(this.sizeX, this.sizeY);
		T left = this.buildX((this.sizeX >> 1) - 1, this.sizeY - 1);
		T top = this.buildY(this.sizeX - 1, (this.sizeY >> 1) - 1);
		this.value[this.sizeX - 1][this.sizeY - 1] = this.editRule.combine(this.editRule.combine(left, top), this.editRule.elementDefault());
	}
	public ArrayFenwickTree2D(T[][] array, IEditRule<T> editRule, ToSized2DArray<T> sizedArray) {
		this.editRule = editRule;
		int lengthY = Arrays.stream(array).mapToInt(a -> a.length).max().orElse(0);
		if(array.length == 0 || lengthY == 0) {
			throw new IllegalArgumentException("Cannot build a fenwick tree with length 0.");
		}
		this.sizeX = Algorithm.highbit(array.length - 1) << 1;
		this.sizeY = Algorithm.highbit(lengthY - 1) << 1;
		this.value = sizedArray.get(this.sizeX, this.sizeY);
		T left = this.buildX(array, (this.sizeX >> 1) - 1, this.sizeY - 1);
		T top = this.buildY(array, this.sizeX - 1, (this.sizeY >> 1) - 1);
		this.value[this.sizeX - 1][this.sizeY - 1] = this.editRule.combine(this.editRule.combine(left, top), this.editRule.elementDefault());
	}

	@Override
	public void edit(T delta, int x, int y) {
		while(x < this.sizeX) {
			for(int dy = y; dy < this.sizeY; dy += Algorithm.lowbit(dy + 1)) {
				this.value[x][dy] = this.editRule.edit(this.value[x][dy], delta, 1);
			}
			x += Algorithm.lowbit(x + 1);
		}
	}

	@Override
	public T query(int lengthX, int lengthY) {
		T ret = this.value[lengthX][lengthY];
		for(int y = lengthY - Algorithm.lowbit(lengthY + 1); y >= 0; y -= Algorithm.lowbit(y + 1)) {
			ret = this.editRule.combine(ret, this.value[lengthX][y]);
		}
		lengthX -= Algorithm.lowbit(lengthX + 1);
		while(lengthX >= 0) {
			for(int y = lengthY; y >= 0; y -= Algorithm.lowbit(y + 1)) {
				ret = this.editRule.combine(ret, this.value[lengthX][y]);
			}
			lengthX -= Algorithm.lowbit(lengthX + 1);
		}
		return ret;
	}

	@Override
	public IEditRule<T> getEditRule() {
		return this.editRule;
	}

	@Override
	public int sizeX() {
		return this.sizeX;
	}
	@Override
	public int sizeY() {
		return this.sizeY;
	}

	@Override
	public void visit(int lengthX, int lengthY, VisitConsumer2D<T> consumer) {
		T left = this.visitX((this.sizeX >> 1) - 1, this.sizeY - 1, lengthX, lengthY, consumer);
		T top = this.visitY(this.sizeX - 1, (this.sizeY >> 1) - 1, lengthX, lengthY, consumer);
		if(this.sizeX - 1 < lengthX && this.sizeY - 1 < lengthY) {
			consumer.visit(
					this.sizeX - 1, this.sizeY - 1,
					this.editRule.subtract(this.value[this.sizeX - 1][this.sizeY - 1],this.editRule.combine(left, top))
			);
		}
	}
}
