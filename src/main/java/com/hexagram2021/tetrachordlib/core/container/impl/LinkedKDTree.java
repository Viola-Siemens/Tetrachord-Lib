package com.hexagram2021.tetrachordlib.core.container.impl;

import com.google.common.collect.Lists;
import com.hexagram2021.tetrachordlib.core.algorithm.Algorithm;
import com.hexagram2021.tetrachordlib.core.container.IMultidimensional;
import com.hexagram2021.tetrachordlib.core.container.KDTree;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("unused")
public class LinkedKDTree<T, TD extends Comparable<TD>> implements KDTree<T, TD> {
	public class LinkedKDNode implements KDNode<T, TD> {
		@Nullable
		private LinkedKDNode ftr;
		@Nullable
		private LinkedKDNode lc;
		@Nullable
		private LinkedKDNode rc;
		private final T other;
		private final IMultidimensional<TD> value;
		private final IMultidimensional<TD> max;
		private final IMultidimensional<TD> min;
		private boolean removed = false;

		private int subtreeSize;

		LinkedKDNode(IMultidimensional<TD> value, T other) {
			this.value = value;
			this.max = value.clone();
			this.min = value.clone();
			this.other = other;
			this.subtreeSize = 1;
		}
		LinkedKDNode(IMultidimensional<TD> value, T other, @Nullable LinkedKDNode ftr) {
			this(value, other);
			this.ftr = ftr;
		}
		LinkedKDNode(IMultidimensional<TD> value, T other, @Nullable LinkedKDNode ftr, @Nullable LinkedKDNode lc, @Nullable LinkedKDNode rc) {
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

		public int getSubtreeSize() {
			return this.subtreeSize;
		}

		/**
		 * Maintain subtreeSize and check if the tree is unbalanced.
		 * If multiple nodes are unbalanced, rebuild will only happen at the shallowest (from root to leaves) unbalanced node.
		 * @param edit		Insert: 1; Remove: -1.
		 * @return			-1 if no need to rebalance. sepDim of this node otherwise.
		 */
		@SuppressWarnings("unchecked")
		private int editSubtreeSizeAndRebuildIfUnbalanced(int edit) {
			this.subtreeSize += edit;
			if(this.ftr == null) {
				double threshold = this.subtreeSize * LinkedKDTree.this.alpha;
				if(threshold > 6 && ((this.lc != null && this.lc.subtreeSize > threshold) || (this.rc != null && this.rc.subtreeSize > threshold))) {
					//rebalance the entire tree
					LinkedKDTree.this.rebalance();
					return -1;
				}
				return (LinkedKDTree.this.sepDim() + 1) % LinkedKDTree.this.getDimensionSize();
			}
			int sepDim = this.ftr.editSubtreeSizeAndRebuildIfUnbalanced(edit);
			if(sepDim < 0) {
				return sepDim;
			}
			double threshold = this.subtreeSize * LinkedKDTree.this.alpha;
			LinkedKDTree.this.hot = this.ftr;
			if(threshold > 6 && ((this.lc != null && this.lc.subtreeSize > threshold) || (this.rc != null && this.rc.subtreeSize > threshold))) {
				//rebalance subtree
				List<BuildNode<T, TD>> remainingTree = Lists.newArrayList();
				KDTree.inDfs(this, (o, m) -> remainingTree.add(BuildNode.of(o, m)));
				assert remainingTree.size() == this.subtreeSize;
				LinkedKDNode rebuildSubtree = LinkedKDTree.this.build(remainingTree.toArray(BuildNode[]::new), 0, remainingTree.size(), sepDim);
				if(this.ftr.lc == this) {
					this.ftr.lc = rebuildSubtree;
				} else {
					assert this.ftr.rc == this;
					this.ftr.rc = rebuildSubtree;
				}
				return -1;
			}
			return (sepDim + 1) % LinkedKDTree.this.getDimensionSize();
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
			this.subtreeSize = 1;
			if(this.lc != null) {
				for(int i = 0; i < this.value.getDimensionSize(); ++i) {
					if(this.max.getDimension(i).compareTo(this.lc.max.getDimension(i)) < 0) {
						this.max.setDimension(i, this.lc.max.getDimension(i));
					}
					if(this.min.getDimension(i).compareTo(this.lc.min.getDimension(i)) > 0) {
						this.min.setDimension(i, this.lc.min.getDimension(i));
					}
				}
				this.subtreeSize += this.lc.subtreeSize;
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
				this.subtreeSize += this.rc.subtreeSize;
			}
		}

		@Override @Nullable
		public LinkedKDNode father() {
			return this.ftr;
		}
		@Override @Nullable
		public LinkedKDNode leftChild() {
			return this.lc;
		}
		@Override @Nullable
		public LinkedKDNode rightChild() {
			return this.rc;
		}
		@Override
		public LinkedKDTree<T, TD> getTree() {
			return LinkedKDTree.this;
		}

		void setRemoved() {
			this.setRemoved(true);
		}
		void setRemoved(boolean removed) {
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
	private LinkedKDNode root = null;
	private int size = 0;
	private int sepDim = 0;

	private double alpha = 0.6875;

	public LinkedKDTree(int dimensionSize) {
		this.dimensionSize = dimensionSize;
	}

	@Nullable
	private transient LinkedKDNode hot = null;
	private transient int hotSepDim = 0;
	@Nullable
	private LinkedKDNode find(IMultidimensional<TD> md, boolean allowRemoved) {
		LinkedKDNode ret = this.root;
		this.hotSepDim = this.sepDim;
		while(ret != null) {
			if((!ret.removed() || allowRemoved) && ret.value().equals(md)) {
				return ret;
			}
			this.hot = ret;
			if(this.getComparator(this.hotSepDim).compare(ret.value(), md) < 0) {
				ret = ret.rightChild();
			} else {
				ret = ret.leftChild();
			}
			this.hotSepDim = (this.hotSepDim + 1) % this.dimensionSize;
		}
		return null;
	}

	@Override @Nullable
	public LinkedKDNode root() {
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

	private LinkedKDNode build(BuildNode<T, TD>[] buildNodes, int beg, int end, int dimension) {
		int nodeCount = end - beg;
		int kth = nodeCount / 2;
		Algorithm.quickSelect(
				buildNodes, beg, end, kth, Comparator.comparing(BuildNode::value, this.getComparator(dimension))
		);
		BuildNode<T, TD> bn = buildNodes[beg + kth];
		LinkedKDNode ret = new LinkedKDNode(bn.value(), bn.other(), this.hot);
		if(nodeCount > 1) {
			this.hot = ret;
			ret.lc = this.build(buildNodes, beg, beg + kth, (dimension + 1) % this.dimensionSize);
			if (nodeCount > 2) {
				this.hot = ret;
				ret.rc = this.build(buildNodes, beg + kth + 1, end, (dimension + 1) % this.dimensionSize);
			}
		}
		ret.maintain();
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
			this.root = this.build(buildNodes, 0, buildNodes.length, this.sepDim);
		}
	}

	@Override
	public LinkedKDNode insert(BuildNode<T, TD> buildNode) {
		assert buildNode.value().getDimensionSize() == this.dimensionSize :
				"Node with dimension size %d cannot be inserted into this %d-dimension tree.".formatted(buildNode.value().getDimensionSize(), this.dimensionSize);
		if(this.size == 0) {
			this.root = new LinkedKDNode(buildNode.value(), buildNode.other());
			this.size += 1;
			return this.root;
		}
		LinkedKDNode kdn = this.find(buildNode.value(), true);
		if(kdn != null) {
			if(kdn.removed()) {
				kdn.setRemoved(false);
				this.size += 1;
				kdn.editSubtreeSizeAndRebuildIfUnbalanced(1);
			}
			return kdn;
		}
		this.hotSepDim = (this.hotSepDim + this.dimensionSize - 1) % this.dimensionSize;
		if(this.getComparator(this.hotSepDim).compare(Objects.requireNonNull(this.hot).value(), buildNode.value()) < 0) {
			kdn = this.hot.rc = new LinkedKDNode(buildNode.value(), buildNode.other(), this.hot);
		} else {
			kdn = this.hot.lc = new LinkedKDNode(buildNode.value(), buildNode.other(), this.hot);
		}
		this.size += 1;
		kdn.pushUp();
		this.hot.editSubtreeSizeAndRebuildIfUnbalanced(1);
		return kdn;
	}
	@Override @Nullable
	public BuildNode<T, TD> remove(IMultidimensional<TD> md) {
		LinkedKDNode kdn = this.find(md, false);
		if(kdn == null || kdn.removed()) {
			return null;
		}
		kdn.setRemoved();
		this.size -= 1;
		kdn.editSubtreeSizeAndRebuildIfUnbalanced(-1);
		return BuildNode.of(kdn.other, kdn.value);
	}
	@Override @Nullable
	public BuildNode<T, TD> remove(@Nullable KDNode<T, TD> kdn) {
		if(kdn instanceof LinkedKDNode node && !kdn.removed()) {
			node.setRemoved();
			this.size -= 1;
			node.editSubtreeSizeAndRebuildIfUnbalanced(-1);
			return BuildNode.of(node.other, node.value);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void rebalance() {
		List<BuildNode<T, TD>> remainingTree = Lists.newArrayList();
		this.inDfs((o, m) -> remainingTree.add(BuildNode.of(o, m)));
		assert remainingTree.size() == this.size;
		this.build(remainingTree.toArray(BuildNode[]::new));
	}

	@Override
	public int getDimensionSize() {
		return this.dimensionSize;
	}

	@Override
	public double getAlpha() {
		return this.alpha;
	}
	@Override
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
}
