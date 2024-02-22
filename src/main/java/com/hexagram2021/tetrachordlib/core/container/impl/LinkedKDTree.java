package com.hexagram2021.tetrachordlib.core.container.impl;

import com.hexagram2021.tetrachordlib.core.algorithm.Algorithm;
import com.hexagram2021.tetrachordlib.core.container.IMultidimensional;
import com.hexagram2021.tetrachordlib.core.container.KDTree;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("unused")
public class LinkedKDTree<T, TD extends Comparable<TD>> implements KDTree<T, TD> {
	public static class LinkedKDNode<T, TD extends Comparable<TD>> implements KDNode<T, TD> {
		@Nullable
		private LinkedKDNode<T, TD> ftr;
		@Nullable
		private LinkedKDNode<T, TD> lc;
		@Nullable
		private LinkedKDNode<T, TD> rc;
		private final T other;
		private final IMultidimensional<TD> value;
		private final IMultidimensional<TD> max;
		private final IMultidimensional<TD> min;
		private boolean removed = false;

		public LinkedKDNode(IMultidimensional<TD> value, T other) {
			this.value = value;
			this.max = value.clone();
			this.min = value.clone();
			this.other = other;
		}
		public LinkedKDNode(IMultidimensional<TD> value, T other, @Nullable LinkedKDNode<T, TD> ftr) {
			this(value, other);
			this.ftr = ftr;
		}
		public LinkedKDNode(IMultidimensional<TD> value, T other, @Nullable LinkedKDNode<T, TD> ftr, @Nullable LinkedKDNode<T, TD> lc, @Nullable LinkedKDNode<T, TD> rc) {
			this(value, other, ftr);
			this.lc = lc;
			this.rc = rc;
		}

		@Override
		public IMultidimensional<TD> value() {
			return this.value;
		}
		@Override
		public T other() {
			return this.other;
		}
		@Override
		public boolean removed() {
			return this.removed;
		}

		@Override
		public double lowerboundDistanceWith(IMultidimensional<TD> md) {
			return md.lowerboundDistanceWith(this.max, this.min);
		}

		@Override
		public double upperboundDistanceWith(IMultidimensional<TD> md) {
			return md.upperboundDistanceWith(this.max, this.min);
		}

		@Override
		public void pushUp() {
			if(this.ftr == null) {
				return;
			}
			boolean flag = false;
			for(int i = 0; i < this.value.getDimensionSize(); ++i) {
				if(this.ftr.max.getDimension(i).compareTo(this.max.getDimension(i)) < 0) {
					flag = true;
					this.ftr.max.setDimension(i, this.max.getDimension(i));
				}
				if(this.ftr.min.getDimension(i).compareTo(this.min.getDimension(i)) > 0) {
					flag = true;
					this.ftr.min.setDimension(i, this.min.getDimension(i));
				}
			}
			if(flag) {
				this.ftr.pushUp();
			}
		}

		@Override
		public void pushDown() {
			if(this.ftr == null) {
				return;
			}
			if(this.ftr.removed) {
				for (int i = 0; i < this.value.getDimensionSize(); ++i) {
					this.ftr.max.setMin();
					this.ftr.min.setMax();
				}
			} else {
				for (int i = 0; i < this.value.getDimensionSize(); ++i) {
					this.ftr.max.setDimension(i, this.ftr.value.getDimension(i));
					this.ftr.min.setDimension(i, this.ftr.value.getDimension(i));
				}
			}
			if(this.ftr.lc != null) {
				for(int i = 0; i < this.value.getDimensionSize(); ++i) {
					if(this.ftr.max.getDimension(i).compareTo(this.ftr.lc.max.getDimension(i)) < 0) {
						this.ftr.max.setDimension(i, this.ftr.lc.max.getDimension(i));
					}
					if(this.ftr.min.getDimension(i).compareTo(this.ftr.lc.min.getDimension(i)) > 0) {
						this.ftr.min.setDimension(i, this.ftr.lc.min.getDimension(i));
					}
				}
			}
			if(this.ftr.rc != null) {
				for(int i = 0; i < this.value.getDimensionSize(); ++i) {
					if(this.ftr.max.getDimension(i).compareTo(this.ftr.rc.max.getDimension(i)) < 0) {
						this.ftr.max.setDimension(i, this.ftr.rc.max.getDimension(i));
					}
					if(this.ftr.min.getDimension(i).compareTo(this.ftr.rc.min.getDimension(i)) > 0) {
						this.ftr.min.setDimension(i, this.ftr.rc.min.getDimension(i));
					}
				}
			}
			this.ftr.pushDown();
		}

		@Override
		public void maintain() {
			if(this.lc != null) {
				this.lc.maintain();
				for(int i = 0; i < this.value.getDimensionSize(); ++i) {
					if(this.max.getDimension(i).compareTo(this.lc.max.getDimension(i)) < 0) {
						this.max.setDimension(i, this.lc.max.getDimension(i));
					}
					if(this.min.getDimension(i).compareTo(this.lc.min.getDimension(i)) > 0) {
						this.min.setDimension(i, this.lc.min.getDimension(i));
					}
				}
			}
			if(this.rc != null) {
				this.rc.maintain();
				for(int i = 0; i < this.value.getDimensionSize(); ++i) {
					if(this.max.getDimension(i).compareTo(this.rc.max.getDimension(i)) < 0) {
						this.max.setDimension(i, this.rc.max.getDimension(i));
					}
					if(this.min.getDimension(i).compareTo(this.rc.min.getDimension(i)) > 0) {
						this.min.setDimension(i, this.rc.min.getDimension(i));
					}
				}
			}
		}

		@Override @Nullable
		public LinkedKDNode<T, TD> father() {
			return this.ftr;
		}
		@Override @Nullable
		public LinkedKDNode<T, TD> leftChild() {
			return this.lc;
		}
		@Override @Nullable
		public LinkedKDNode<T, TD> rightChild() {
			return this.rc;
		}

		@Override
		public void setRemoved(boolean removed) {
			if(this.removed == removed) {
				return;
			}
			this.removed = removed;
			if(removed) {
				for(int i = 0; i < this.value.getDimensionSize(); ++i) {
					this.max.setMin();
					this.min.setMax();
				}
				if(this.lc != null) {
					for(int i = 0; i < this.value.getDimensionSize(); ++i) {
						if(this.max.getDimension(i).compareTo(this.lc.max.getDimension(i)) < 0) {
							this.max.setDimension(i, this.lc.max.getDimension(i));
						}
						if(this.min.getDimension(i).compareTo(this.lc.min.getDimension(i)) > 0) {
							this.min.setDimension(i, this.lc.min.getDimension(i));
						}
					}
				}
				if(this.rc != null) {
					for(int i = 0; i < this.value.getDimensionSize(); ++i) {
						if(this.max.getDimension(i).compareTo(this.rc.max.getDimension(i)) < 0) {
							this.max.setDimension(i, this.rc.max.getDimension(i));
						}
						if(this.min.getDimension(i).compareTo(this.rc.min.getDimension(i)) > 0) {
							this.min.setDimension(i, this.rc.min.getDimension(i));
						}
					}
				}
				this.pushDown();
			} else {
				for(int i = 0; i < this.value.getDimensionSize(); ++i) {
					if(this.max.getDimension(i).compareTo(this.value.getDimension(i)) < 0) {
						this.max.setDimension(i, this.value.getDimension(i));
					}
					if(this.min.getDimension(i).compareTo(this.value.getDimension(i)) > 0) {
						this.min.setDimension(i, this.value.getDimension(i));
					}
				}
				this.pushUp();
			}
		}
	}

	private final int dimensionSize;
	@Nullable
	private LinkedKDNode<T, TD> root = null;
	private int size = 0;
	private int sepDim = 0;

	public LinkedKDTree(int dimensionSize) {
		this.dimensionSize = dimensionSize;
	}

	private transient int idleCount = 0;
	private void applyIdleCountAndRebalanceIfNecessary() {
		if(this.size == 0) {
			this.idleCount = 0;
			this.clear();
			return;
		}
		this.idleCount += 1;
		if(this.idleCount < 16) {
			return;
		}
		if(this.size / this.idleCount < 4) {
			this.rebalance();
			this.idleCount = 0;
		}
	}

	@Nullable
	private transient LinkedKDNode<T, TD> hot = null;
	private transient int hotSepDim = 0;
	@Nullable
	private LinkedKDNode<T, TD> find(IMultidimensional<TD> md, boolean allowRemoved) {
		LinkedKDNode<T, TD> ret = this.root;
		this.hotSepDim = this.sepDim;
		while(ret != null) {
			if((!ret.removed() || allowRemoved) && ret.value().equals(md)) {
				return ret;
			}
			this.hot = ret;
			if(ret.value().getDimension(this.hotSepDim).compareTo(md.getDimension(this.hotSepDim)) < 0) {
				ret = ret.rightChild();
			} else {
				ret = ret.leftChild();
			}
			this.hotSepDim = (this.hotSepDim + 1) % this.dimensionSize;
		}
		return null;
	}

	@Override @Nullable
	public KDNode<T, TD> root() {
		return this.root;
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public int sepDim() {
		return this.sepDim;
	}

	@Override
	public void setInitSepDim(int sepDim) {
		if(this.root == null) {
			this.sepDim = sepDim;
		} else {
			throw new IllegalStateException("Cannot set initial sepDim when root is not null!");
		}
	}

	@Override
	public void clear() {
		this.root = null;
		this.sepDim = 0;
		this.size = 0;
		this.hot = null;
		this.hotSepDim = 0;
	}

	private LinkedKDNode<T, TD> build(BuildNode<T, TD>[] buildNodes, int beg, int end, int depth) {
		int nodeCount = end - beg;
		int dimension = (this.sepDim + depth) % this.dimensionSize;
		Algorithm.quickSelect(
				buildNodes, beg, end, nodeCount / 2,
				Comparator.comparing(bn -> bn.value().getDimension(dimension))
		);
		BuildNode<T, TD> bn = buildNodes[beg + nodeCount / 2];
		LinkedKDNode<T, TD> ret = new LinkedKDNode<>(bn.value(), bn.other(), this.hot);
		if(nodeCount > 1) {
			this.hot = ret;
			ret.lc = this.build(buildNodes, beg, beg + nodeCount / 2, depth + 1);
			if (nodeCount > 2) {
				this.hot = ret;
				ret.rc = this.build(buildNodes, beg + nodeCount / 2 + 1, end, depth + 1);
			}
		}
		return ret;
	}

	@Override
	public void build(BuildNode<T, TD>[] buildNodes) {
		this.clear();
		this.size = buildNodes.length;

		if(this.size > 0) {
			IMultidimensional<Double> mean = Arrays.stream(buildNodes).map(BuildNode::value).reduce(IMultidimensional::add).orElseThrow().divide(this.size);
			IMultidimensional<Double> var = Arrays.stream(buildNodes).map(bn -> bn.value().asDouble().minus(mean)).map(md -> md.hadamard(md)).reduce(IMultidimensional::add).orElseThrow().divide(this.size);

			this.sepDim = 0;
			for(int j = 1; j < this.dimensionSize; ++j) {
				if(var.getDimension(this.sepDim) < var.getDimension(j)) {
					this.sepDim = j;
				}
			}
			this.root = this.build(buildNodes, 0, buildNodes.length, 0);
			Objects.requireNonNull(this.root).maintain();
		}
	}

	@Override
	public KDNode<T, TD> insert(BuildNode<T, TD> buildNode) {
		assert buildNode.value().getDimensionSize() == this.dimensionSize :
				"Node with dimension size %d cannot be inserted into this %d-dimension tree.".formatted(buildNode.value().getDimensionSize(), this.dimensionSize);
		if(this.size == 0) {
			this.root = new LinkedKDNode<>(buildNode.value(), buildNode.other());
			this.size += 1;
			return this.root;
		}
		KDNode<T, TD> kdn = this.find(buildNode.value(), true);
		if(kdn != null) {
			kdn.setRemoved(false);
			return kdn;
		}
		this.hotSepDim = (this.hotSepDim + this.dimensionSize - 1) % this.dimensionSize;
		if(Objects.requireNonNull(this.hot).value().getDimension(this.hotSepDim).compareTo(buildNode.value().getDimension(this.hotSepDim)) < 0) {
			kdn = this.hot.rc = new LinkedKDNode<>(buildNode.value(), buildNode.other(), this.hot);
		} else {
			kdn = this.hot.lc = new LinkedKDNode<>(buildNode.value(), buildNode.other(), this.hot);
		}
		this.size += 1;
		kdn.pushUp();
		this.applyIdleCountAndRebalanceIfNecessary();
		return kdn;
	}
	@Override @Nullable
	public BuildNode<T, TD> remove(IMultidimensional<TD> md) {
		LinkedKDNode<T, TD> kdn = this.find(md, false);
		if(kdn == null) {
			return null;
		}
		kdn.setRemoved();
		this.size -= 1;
		this.applyIdleCountAndRebalanceIfNecessary();
		return BuildNode.of(kdn.other, kdn.value);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void rebalance() {
		List<BuildNode<T, TD>> remainingTree = Lists.newArrayList();
		this.preDfs((o, m) -> remainingTree.add(BuildNode.of(o, m)));
		assert remainingTree.size() == this.size;
		this.build(remainingTree.toArray(new BuildNode[0]));
	}
}
