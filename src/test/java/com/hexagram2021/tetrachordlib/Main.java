package com.hexagram2021.tetrachordlib;

import com.google.common.collect.Lists;
import com.hexagram2021.tetrachordlib.core.algorithm.Algorithm;
import com.hexagram2021.tetrachordlib.core.container.IMultidimensional;
import com.hexagram2021.tetrachordlib.core.container.KDTree;
import com.hexagram2021.tetrachordlib.core.container.SegmentTree2D;
import com.hexagram2021.tetrachordlib.core.container.impl.DoublePosition;
import com.hexagram2021.tetrachordlib.core.container.impl.EditRules;
import com.hexagram2021.tetrachordlib.core.container.impl.IntPosition;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

public class Main {
	private static final String FOLDER = "src/test/java/com/hexagram2021/tetrachordlib/";

	private static final boolean LOG_DETAIL = true;

	private static void testSegmentTree1() throws FileNotFoundException {
		//https://www.luogu.com.cn/problem/P4514

		String OP;
		int W, H;
		java.util.Scanner in = new java.util.Scanner(new FileInputStream(FOLDER + "st.in"));
		in.next();
		W = in.nextInt();
		H = in.nextInt();
		SegmentTree2D<Integer> st = SegmentTree2D.newArrayQuadSegmentTree2D(Math.max(W, H), EditRules.Integer.sumAdd(), Integer[]::new);
		while(in.hasNext()) {
			int x1, x2, y1, y2, delta;
			OP = in.next();
			x1 = in.nextInt();
			y1 = in.nextInt();
			x2 = in.nextInt() + 1;
			y2 = in.nextInt() + 1;
			switch(OP) {
				case "L":
					delta = in.nextInt();
					st.edit(delta, x1, x2, y1, y2);
					break;
				case "k":
					System.out.println(st.query(x1, x2, y1, y2));
					break;
				default:
					return;
			}
		}
	}

	private static void testSegmentTree() {
		//Simulate Players' Behavior
		int[][] array = new int[1024][1024];
		SegmentTree2D<Integer> st = SegmentTree2D.newArrayQuadSegmentTree2D(1024, EditRules.Integer.sumAdd(), Integer[]::new);
		boolean fail = false;
		for(int i = 0; i < 4096; ++i) {
			int beginX = Algorithm.randInt(0, 1023);
			int endX = Algorithm.randInt(beginX + 1, 1024);
			int beginY = Algorithm.randInt(0, 1023);
			int endY = Algorithm.randInt(beginY + 1, 1024);
			if(Algorithm.randInt(0, 3) == 0) {
				int output = st.query(beginX, endX, beginY, endY);
				int ans = 0;
				for(int x = beginX; x < endX; ++x) {
					for(int y = beginY; y < endY; ++y) {
						ans += array[x][y];
					}
				}
				if(LOG_DETAIL) {
					System.out.printf("\tExpect: %d. Found: %d.\n", ans, output);
				}
				if(ans != output) {
					System.out.printf("Wrong output! Expect: %d. Found: %d.\n", ans, output);
					fail = true;
				}
			} else {
				int a = Algorithm.randInt(-8, 8);
				if(a == 0) {
					a = 8;
				}
				st.edit(a, beginX, endX, beginY, endY);
				for(int x = beginX; x < endX; ++x) {
					for(int y = beginY; y < endY; ++y) {
						array[x][y] += a;
					}
				}
			}
		}
		System.out.print("Test Segment Tree: ");
		System.out.println(fail ? "TEST FAILED!!!" : "TEST PASSED.");
	}

	private static void testKDTree1() throws FileNotFoundException {
		//https://www.luogu.com.cn/problem/P6247

		KDTree<Integer, Double> kdt = KDTree.newLinkedKDTree(2);
		java.util.Scanner in = new java.util.Scanner(new FileInputStream(FOLDER + "kdt.in"));
		@SuppressWarnings("unchecked")
		KDTree.BuildNode<Integer, Double>[] buildNodes = new KDTree.BuildNode[in.nextInt()];
		for(int i = 0; i < buildNodes.length; ++i) {
			double x, y;
			x = in.nextDouble();
			y = in.nextDouble();
			buildNodes[i] = KDTree.BuildNode.of(i, new DoublePosition(x, y));
		}
		kdt.build(buildNodes);
		int sepDim = kdt.sepDim();
		List<KDTree.BuildNode<Integer, Double>> list = Lists.newArrayList();
		kdt.bfs((other, value) -> list.add(KDTree.BuildNode.of(other, value)));
		kdt.clear();

		double nearest = 2e9, farthest = 0;
		kdt.setInitSepDim(sepDim);
		kdt.insert(list.get(0));
		for(int i = 1; i < list.size(); ++i) {
			double dist = kdt.findClosest(list.get(i).value()).distanceWith(list.get(i).value());
			if(nearest > dist) {
				nearest = dist;
			}
			dist = kdt.findFarthest(list.get(i).value()).distanceWith(list.get(i).value());
			if(farthest < dist) {
				farthest = dist;
			}
			kdt.insert(list.get(i));
		}
		System.out.printf("%.2f %.2f\n", nearest, farthest);
	}

	private static void testKDTree2() throws FileNotFoundException {
		Algorithm.setSeed(42);
		KDTree<Integer, Integer> kdt = KDTree.newLinkedKDTree(2);
		java.util.Scanner in = new java.util.Scanner(new FileInputStream(FOLDER + "kdt.in"));
		@SuppressWarnings("unchecked")
		KDTree.BuildNode<Integer, Integer>[] buildNodes = new KDTree.BuildNode[in.nextInt()];
		for(int i = 0; i < buildNodes.length; ++i) {
			double x, y;
			x = in.nextDouble();
			y = in.nextDouble();
			buildNodes[i] = KDTree.BuildNode.of(i, new IntPosition((int)Math.round(x), (int)Math.round(y)));
		}
		kdt.build(buildNodes);
		List<IMultidimensional<Integer>> list = Lists.newArrayList();
		kdt.bfs((other, value) -> list.add(value.clone()));

		double nearest = 2e9, farthest = 0;
		for(int i = 0; i < list.size() - 1; ++i) {
			IMultidimensional<Integer> d = list.get(i);
			Objects.requireNonNull(kdt.remove(d));
			KDTree.KDNode<Integer, Integer> kdn = kdt.findClosest(d);
			double dist = kdn.distanceWith(d);
			if(nearest > dist) {
				nearest = dist;
			}
			dist = kdt.findFarthest(d).distanceWith(d);
			if(farthest < dist) {
				farthest = dist;
			}
		}
		System.out.printf("%.2f %.2f\n", nearest, farthest);
	}

	private static void testKDTree() {
		//Simulate Players' Behavior
		List<IntPosition> list = Lists.newArrayList();
		KDTree<Integer, Integer> kdt = KDTree.newLinkedKDTree(3);
		int cnt = 0;
		boolean fail = false;
		for(int i = 0; i < 4096; ++i) {
			int a = Algorithm.randInt(0, 1024);
			if(a < list.size()) {
				IntPosition position = list.remove(a);
				kdt.remove(position);
				if(LOG_DETAIL) {
					System.out.printf("\tRemove (%d, %d, %d).\n", position.getDimension(0), position.getDimension(1), position.getDimension(2));
				}
			} else {
				IntPosition position = new IntPosition(Algorithm.randInt(-512, 512), Algorithm.randInt(-32, 64), Algorithm.randInt(-512, 512));
				if((a & 0x7) == 0 && !list.isEmpty()) {
					double ans, output;
					if(Algorithm.randBool()) {
						ans = list.stream().mapToDouble(md -> md.distanceWith(position)).min().orElseThrow();
						output = kdt.findClosest(position).distanceWith(position);
					} else {
						ans = list.stream().mapToDouble(md -> md.distanceWith(position)).max().orElseThrow();
						output = kdt.findFarthest(position).distanceWith(position);
					}
					if(LOG_DETAIL) {
						System.out.printf("\tExpect: %f. Found: %f.\n", ans, output);
					}
					if(Math.abs(ans - output) > 1e-6) {
						System.out.printf("Wrong output! Expect: %f. Found: %f.\n", ans, output);
						fail = true;
					}
				} else {
					list.add(position);
					kdt.insert(KDTree.BuildNode.of(cnt, position));
					if(LOG_DETAIL) {
						System.out.printf("\tAdd (%d, %d, %d).\n", position.getDimension(0), position.getDimension(1), position.getDimension(2));
					}
					cnt += 1;
				}
			}
		}
		System.out.print("Test KD Tree: ");
		System.out.println(fail ? "TEST FAILED!!!" : "TEST PASSED.");
	}

	@SuppressWarnings("CallToPrintStackTrace")
	public static void main(String[] args) {
		try {
			testKDTree1();
			testKDTree2();
			testKDTree();
			testSegmentTree1();
			testSegmentTree();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
