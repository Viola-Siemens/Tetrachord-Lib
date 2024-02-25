package com.hexagram2021.tetrachordlib.core.container;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.AtomicDouble;
import com.hexagram2021.tetrachordlib.core.container.impl.LinkedKDTree;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * KD Tree is an efficient data structure to query nearest and farthest points of each given position.
 * @param <T>		Type of data that the point is maintained.
 * @param <TD>		Type of each dimension.
 */
@SuppressWarnings("unused")
public interface KDTree<T, TD extends Comparable<TD>> {
	record BuildNode<T, TD extends Comparable<TD>>(IMultidimensional<TD> value, T other) {
		public BuildNode(IMultidimensional<TD> value, T other) {
			this.value = value.clone();
			this.other = other;
		}

		@SuppressWarnings("MethodDoesntCallSuperMethod")
		@Override
		public BuildNode<T, TD> clone() {
			return new BuildNode<>(this.value, this.other);
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof KDTree.BuildNode<?, ?> bn && this.value.equals(bn.value) && this.other.equals(bn.other);
		}
		@Override
		public int hashCode() {
			return Objects.hash(this.value, this.other);
		}

		public static <T, TD extends Comparable<TD>> BuildNode<T, TD> of(T other, IMultidimensional<TD> val) {
			return new BuildNode<>(val, other);
		}
	}

	interface KDNode<T, TD extends Comparable<TD>> {
		IMultidimensional<TD> value();
		T other();
		@SuppressWarnings("BooleanMethodIsAlwaysInverted")
		boolean removed();

		/**
		 * Compute the distance between this node and the input multidimensional position.
		 * @param md		Other position to compute with.
		 * @return			Distance between this.value() and md.
		 */
		default double distanceWith(IMultidimensional<TD> md) {
			return this.value().distanceWith(md);
		}
		double lowerboundDistanceWith(IMultidimensional<TD> md);
		double upperboundDistanceWith(IMultidimensional<TD> md);

		/**
		 * Called in KDTree$insert. Only affect a single link instead of the whole tree.
		 */
		void pushUp();
		/**
		 * Called in KDTree$remove. Only affect a single link instead of the whole tree.
		 */
		void pushDown();
		/**
		 * Called in KDTree$build. Affect the whole tree.
		 */
		void maintain();

		default void setRemoved() {
			this.setRemoved(true);
		}
		void setRemoved(boolean removed);

		@Nullable
		KDNode<T, TD> father();
		@Nullable
		KDNode<T, TD> leftChild();
		@Nullable
		KDNode<T, TD> rightChild();

		default void visit(IVisitFunction.Binary<T, IMultidimensional<TD>> visitFunction) {
			visitFunction.visit(this.other(), this.value());
		}
	}

	@Nullable
	KDNode<T, TD> root();
	int size();
	default boolean isEmpty() {
		return this.size() == 0;
	}
	int sepDim();
	void setInitSepDim(int sepDim);
	void clear();
	/**
	 * Remove the whole tree and build a KDTree from the given nodes.
	 * @param buildNodes	Nodes to be built from.
	 */
	void build(BuildNode<T, TD>[] buildNodes);
	/**
	 * Insert a node into this KDTree. If node already exists, no update will occur.
	 * @param buildNode		Node to be added.
	 * @return				New node if not exists, or the existing node.
	 */
	KDNode<T, TD> insert(BuildNode<T, TD> buildNode);
	/**
	 * Remove a node from this KDTree. If node does not exist, no update will occur.
	 * @param md			Node to be removed.
	 * @return				The removed node if exists, or null if not exists.
	 */
	@Nullable
	BuildNode<T, TD> remove(IMultidimensional<TD> md);
	/**
	 * Rebalance the entire KDTree.
	 */
	void rebalance();

	/**
	 * Find the closest position of the tree to target point. Make sure the tree is NOT empty.
	 * @param target	Target point to be searched for.
	 * @return			The closest position to target. If target is in this tree, then return target.
	 */
	default KDNode<T, TD> findClosest(IMultidimensional<TD> target) {
		KDNode<T, TD> root = Objects.requireNonNull(this.root());
		AtomicDouble dist = new AtomicDouble(root.distanceWith(target));
		AtomicReference<KDNode<T, TD>> answer = new AtomicReference<>(root);
		searchForClosest(root, target, dist, answer);
		return answer.get();
	}
	/**
	 * Find the farthest position of the tree to target point. Make sure the tree is NOT empty.
	 * @param target	Target point to be searched for.
	 * @return			The farthest position to target.
	 */
	default KDNode<T, TD> findFarthest(IMultidimensional<TD> target) {
		KDNode<T, TD> root = Objects.requireNonNull(this.root());
		AtomicDouble dist = new AtomicDouble(root.distanceWith(target));
		AtomicReference<KDNode<T, TD>> answer = new AtomicReference<>(root);
		searchForFarthest(root, target, dist, answer);
		return answer.get();
	}

	default void bfs(IVisitFunction.Binary<T, IMultidimensional<TD>> visitFunction) {
		KDNode<T, TD> root = this.root();
		if(root == null) {
			return;
		}
		Queue<KDNode<T, TD>> q = Queues.newArrayDeque();
		q.add(root);
		while(!q.isEmpty()) {
			KDNode<T, TD> kdn = q.poll();
			if(!kdn.removed()) {
				kdn.visit(visitFunction);
			}
			KDNode<T, TD> lc = kdn.leftChild();
			KDNode<T, TD> rc = kdn.rightChild();
			if(lc != null) {
				q.add(lc);
			}
			if(rc != null) {
				q.add(rc);
			}
		}
	}
	default void preDfs(IVisitFunction.Binary<T, IMultidimensional<TD>> visitFunction) {
		KDNode<T, TD> root = this.root();
		if(root != null) {
			preDfs(root, visitFunction);
		}
	}
	default void inDfs(IVisitFunction.Binary<T, IMultidimensional<TD>> visitFunction) {
		KDNode<T, TD> root = this.root();
		if(root != null) {
			inDfs(root, visitFunction);
		}
	}
	default void postDfs(IVisitFunction.Binary<T, IMultidimensional<TD>> visitFunction) {
		KDNode<T, TD> root = this.root();
		if(root != null) {
			postDfs(root, visitFunction);
		}
	}

	private static <T, TD extends Comparable<TD>> void preDfs(KDNode<T, TD> kdn, IVisitFunction.Binary<T, IMultidimensional<TD>> visitFunction) {
		if(!kdn.removed()) {
			kdn.visit(visitFunction);
		}

		KDNode<T, TD> lc = kdn.leftChild();
		KDNode<T, TD> rc = kdn.rightChild();
		if(lc != null) {
			preDfs(lc, visitFunction);
		}
		if(rc != null) {
			preDfs(rc, visitFunction);
		}
	}
	private static <T, TD extends Comparable<TD>> void inDfs(KDNode<T, TD> kdn, IVisitFunction.Binary<T, IMultidimensional<TD>> visitFunction) {
		KDNode<T, TD> lc = kdn.leftChild();
		KDNode<T, TD> rc = kdn.rightChild();
		if(lc != null) {
			inDfs(lc, visitFunction);
		}

		if(!kdn.removed()) {
			kdn.visit(visitFunction);
		}

		if(rc != null) {
			inDfs(rc, visitFunction);
		}
	}
	private static <T, TD extends Comparable<TD>> void postDfs(KDNode<T, TD> kdn, IVisitFunction.Binary<T, IMultidimensional<TD>> visitFunction) {
		KDNode<T, TD> lc = kdn.leftChild();
		KDNode<T, TD> rc = kdn.rightChild();
		if(lc != null) {
			postDfs(lc, visitFunction);
		}
		if(rc != null) {
			postDfs(rc, visitFunction);
		}

		if(!kdn.removed()) {
			kdn.visit(visitFunction);
		}
	}
	private static <T, TD extends Comparable<TD>> void searchForClosest(KDNode<T, TD> kdn, IMultidimensional<TD> target,
																		AtomicDouble dist, AtomicReference<KDNode<T, TD>> answer) {
		KDNode<T, TD> lc = kdn.leftChild();
		KDNode<T, TD> rc = kdn.rightChild();
		if(lc != null) {
			double leftDist = lc.distanceWith(target);
			if(!lc.removed() && dist.get() > leftDist) {
				dist.set(leftDist);
				answer.set(lc);
				searchForClosest(lc, target, dist, answer);
			} else if(lc.lowerboundDistanceWith(target) < dist.get()) {
				searchForClosest(lc, target, dist, answer);
			}
		}
		if(rc != null) {
			double rightDist = rc.distanceWith(target);
			if(!rc.removed() && dist.get() > rightDist) {
				dist.set(rightDist);
				answer.set(rc);
				searchForClosest(rc, target, dist, answer);
			} else if(rc.lowerboundDistanceWith(target) < dist.get()) {
				searchForClosest(rc, target, dist, answer);
			}
		}
	}
	private static <T, TD extends Comparable<TD>> void searchForFarthest(KDNode<T, TD> kdn, IMultidimensional<TD> target,
																		 AtomicDouble dist, AtomicReference<KDNode<T, TD>> answer) {
		KDNode<T, TD> lc = kdn.leftChild();
		KDNode<T, TD> rc = kdn.rightChild();
		if(lc != null) {
			double leftDist = lc.distanceWith(target);
			if(!lc.removed() && dist.get() < leftDist) {
				dist.set(leftDist);
				answer.set(lc);
				searchForFarthest(lc, target, dist, answer);
			} else if(lc.upperboundDistanceWith(target) > dist.get()) {
				searchForFarthest(lc, target, dist, answer);
			}
		}
		if(rc != null) {
			double rightDist = rc.distanceWith(target);
			if(!rc.removed() && dist.get() < rightDist) {
				dist.set(rightDist);
				answer.set(rc);
				searchForFarthest(rc, target, dist, answer);
			} else if(rc.upperboundDistanceWith(target) > dist.get()) {
				searchForFarthest(rc, target, dist, answer);
			}
		}
	}

	static <T, TD extends Comparable<TD>> LinkedKDTree<T, TD> newLinkedKDTree(int dimensionSize) {
		return new LinkedKDTree<>(dimensionSize);
	}
}
