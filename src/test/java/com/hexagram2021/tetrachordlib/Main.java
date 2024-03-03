package com.hexagram2021.tetrachordlib;

import com.google.common.collect.Lists;
import com.hexagram2021.tetrachordlib.core.algorithm.Algorithm;
import com.hexagram2021.tetrachordlib.core.container.IMultidimensional;
import com.hexagram2021.tetrachordlib.core.container.KDTree;
import com.hexagram2021.tetrachordlib.core.container.SegmentTree2D;
import com.hexagram2021.tetrachordlib.core.container.impl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
	private static final String FOLDER = "src/test/java/com/hexagram2021/tetrachordlib/";
	private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

	private static final boolean LOG_DETAIL = false;

	private static final int XZBound = 1024;
	private static final int YBound = 32;

	private static void testSegmentTree1() throws IOException {
		//https://www.luogu.com.cn/problem/P4514

		String OP;
		int W, H;
		java.util.Scanner in = new java.util.Scanner(Files.newInputStream(Paths.get(FOLDER + "st.in")));
		java.util.Scanner out = new java.util.Scanner(Files.newInputStream(Paths.get(FOLDER + "st.out")));
		boolean failed = false;
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
					int output = st.query(x1, x2, y1, y2);
					if(output != out.nextInt()) {
						failed = true;
					}
					break;
				default:
					return;
			}
		}
		System.out.print("Test Case (Segment Tree): ");
		System.out.println(failed ? "\033[31mTEST FAILED!!!\033[0m" : "\033[32mTEST PASSED.\033[0m");
	}

	private static void testSegmentTree() {
		//Simulate Players' Behavior
		int[][] array = new int[1024][1024];
		SegmentTree2D<Integer> st = SegmentTree2D.newArrayQuadSegmentTree2D(1024, EditRules.Integer.sumAdd(), Integer[]::new);
		boolean failed = false;
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
					failed = true;
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
		System.out.println(failed ? "\033[31mTEST FAILED!!!\033[0m" : "\033[32mTEST PASSED.\033[0m");
	}

	private static void testKDTree1() throws IOException {
		//https://www.luogu.com.cn/problem/P6247

		KDTree<Integer, Double> kdt = KDTree.newLinkedKDTree(2);
		java.util.Scanner in = new java.util.Scanner(Files.newInputStream(Paths.get(FOLDER + "kdt.in")));
		java.util.Scanner out = new java.util.Scanner(Files.newInputStream(Paths.get(FOLDER + "kdt.out")));
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
		System.out.print("Test Case (KD Tree 2): ");
		boolean failed = !FORMAT.format(nearest).equals(out.next());
		failed |= !FORMAT.format(farthest).equals(out.next());
		System.out.println(failed ? "\033[31mTEST FAILED!!!\033[0m" : "\033[32mTEST PASSED.\033[0m");
	}

	private static void testKDTree2() throws IOException {
		KDTree<Integer, Integer> kdt = KDTree.newLinkedKDTree(2);
		java.util.Scanner in = new java.util.Scanner(Files.newInputStream(Paths.get(FOLDER + "kdt.in")));
		java.util.Scanner out = new java.util.Scanner(Files.newInputStream(Paths.get(FOLDER + "kdt.out")));
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
		System.out.print("Test Case (KD Tree 1): ");
		boolean failed = !FORMAT.format(nearest).equals(out.next());
		failed |= !FORMAT.format(farthest).equals(out.next());
		System.out.println(failed ? "\033[31mTEST FAILED!!!\033[0m" : "\033[32mTEST PASSED.\033[0m");
	}

	@SuppressWarnings("unchecked")
	private static void testKDTree() {
		//Simulate Players' Behavior
		IntPosition[] arr = new IntPosition[1024];
		for(int i = 0; i < 1024; ++i) {
			arr[i] = new IntPosition(Algorithm.randInt(-XZBound, XZBound), Algorithm.randInt(-YBound, YBound * 2), Algorithm.randInt(-XZBound, XZBound));
		}
		arr = Arrays.stream(arr).distinct().toArray(IntPosition[]::new);
		List<IntPosition> list = Lists.newArrayList(arr);
		KDTree<Integer, Integer> kdt = KDTree.newLinkedKDTree(3);
		kdt.build(Arrays.stream(arr).map(p -> new KDTree.BuildNode<>(p, Algorithm.randInt(-1024, 0))).toArray(KDTree.BuildNode[]::new));
		int cnt = 0;
		boolean failed = false;
		for(int i = 0; i < 16384; ++i) {
			int a = Algorithm.randInt(0, 10000);
			if(a < list.size()) {
				IntPosition position = list.remove(a);
				kdt.remove(position);
				if(LOG_DETAIL) {
					System.out.printf("\tRemove (%d, %d, %d).\n", position.getDimension(0), position.getDimension(1), position.getDimension(2));
				}
			} else {
				IntPosition position = new IntPosition(Algorithm.randInt(-XZBound, XZBound), Algorithm.randInt(-YBound, YBound * 2), Algorithm.randInt(-XZBound, XZBound));
				if((a & 0x1) != 0 && !list.isEmpty()) {
					double ans, output;
					if(Algorithm.randBool()) {
						ans = list.stream().mapToDouble(md -> md.distanceWith(position)).min().orElseThrow(RuntimeException::new);
						output = kdt.findClosest(position).distanceWith(position);
					} else {
						ans = list.stream().mapToDouble(md -> md.distanceWith(position)).max().orElseThrow(RuntimeException::new);
						output = kdt.findFarthest(position).distanceWith(position);
					}
					if(LOG_DETAIL) {
						System.out.printf("\tQuery (%d, %d, %d). Expect: %f. Found: %f.\n",
								position.getDimension(0), position.getDimension(1), position.getDimension(2), ans, output);
					}
					if(Math.abs(ans - output) > 1e-6) {
						System.out.printf("Wrong output! Expect: %f. Found: %f.\n", ans, output);
						failed = true;
					}
				} else {
					if(!list.contains(position)) {
						list.add(position);
						kdt.insert(KDTree.BuildNode.of(cnt, position));
						if (LOG_DETAIL) {
							System.out.printf("\tAdd (%d, %d, %d).\n", position.getDimension(0), position.getDimension(1), position.getDimension(2));
						}
						cnt += 1;
					}
				}
			}
		}
		System.out.print("Test KD Tree: ");
		System.out.println(failed ? "\033[31mTEST FAILED!!!\033[0m" : "\033[32mTEST PASSED.\033[0m");
	}

	@SuppressWarnings("unchecked")
	private static void testKDTreeMaintainability() {
		//Simulate Players' Behavior
		IntPosition[] arr = new IntPosition[1024];
		for(int i = 0; i < 1024; ++i) {
			arr[i] = new IntPosition(Algorithm.randInt(-XZBound, XZBound), Algorithm.randInt(-YBound, YBound * 2), Algorithm.randInt(-XZBound, XZBound));
		}
		arr = Arrays.stream(arr).distinct().toArray(IntPosition[]::new);
		List<IntPosition> list = Lists.newArrayList(arr);
		KDTree<Integer, Integer> kdt = KDTree.newLinkedKDTree(3);
		kdt.build(Arrays.stream(arr).map(p -> new KDTree.BuildNode<>(p, Algorithm.randInt(-1024, 0))).toArray(KDTree.BuildNode[]::new));
		int cnt = 0;
		for(int i = 0; i < 16384; ++i) {
			int a = Algorithm.randInt(0, 10000);
			if(a < list.size()) {
				IntPosition position = list.remove(a);
				kdt.remove(position);
				if(LOG_DETAIL) {
					System.out.printf("\tRemove (%d, %d, %d).\n", position.getDimension(0), position.getDimension(1), position.getDimension(2));
				}
			} else {
				IntPosition position = new IntPosition(Algorithm.randInt(-XZBound, XZBound), Algorithm.randInt(-YBound, YBound * 2), Algorithm.randInt(-XZBound, XZBound));
				if((a & 0x1) != 0 && !list.isEmpty()) {
					kdt.inDfs(kdn -> {
						LinkedKDTree<Integer, Integer>.LinkedKDNode lkdn = (LinkedKDTree<Integer, Integer>.LinkedKDNode) kdn;
						int s = lkdn.getSubtreeSize() - 1;
						LinkedKDTree<Integer, Integer>.LinkedKDNode lc = lkdn.leftChild();
						LinkedKDTree<Integer, Integer>.LinkedKDNode rc = lkdn.rightChild();
						if(lc != null) {
							s -= lc.getSubtreeSize();
						}
						if(rc != null) {
							s -= rc.getSubtreeSize();
						}
						if(s != 0) {
							throw new AssertionError("s should be lc.s + rc.s + 1!");
						}
					});
				} else {
					if(!list.contains(position)) {
						list.add(position);
						kdt.insert(KDTree.BuildNode.of(cnt, position));
						if (LOG_DETAIL) {
							System.out.printf("\tAdd (%d, %d, %d).\n", position.getDimension(0), position.getDimension(1), position.getDimension(2));
						}
						cnt += 1;
					}
				}
			}
		}
		System.out.println("Test KD Tree Maintainability: \033[32mTEST PASSED.\033[0m");
	}

	@SuppressWarnings("unchecked")
	private static void testKDTreeTime() {
		//Simulate Players' Behavior
		IntPosition[] arr = new IntPosition[1024];
		for(int i = 0; i < 1024; ++i) {
			arr[i] = new IntPosition(Algorithm.randInt(-XZBound, XZBound), Algorithm.randInt(-YBound, YBound * 2), Algorithm.randInt(-XZBound, XZBound));
		}
		arr = Arrays.stream(arr).distinct().toArray(IntPosition[]::new);
		long query = 0, insert = 0, remove = 0;
		long tmp;
		System.out.print("Test Time of KD Tree:\n");
		long beginBf = System.currentTimeMillis();
		List<IntPosition> list = Lists.newArrayList(arr);
		for(int i = 0; i < 32768; ++i) {
			int a = Algorithm.randInt(0, 10000);
			if(a < list.size()) {
				tmp = System.currentTimeMillis();
				IntPosition position = list.remove(a);
				remove += System.currentTimeMillis() - tmp;
				if(LOG_DETAIL) {
					System.out.printf("\tRemove (%d, %d, %d).\n", position.getDimension(0), position.getDimension(1), position.getDimension(2));
				}
			} else {
				IntPosition position = new IntPosition(Algorithm.randInt(-XZBound, XZBound), Algorithm.randInt(-YBound, YBound * 2), Algorithm.randInt(-XZBound, XZBound));
				if((a & 0x1) != 0 && !list.isEmpty()) {
					double ans;
					tmp = System.currentTimeMillis();
					if(Algorithm.randBool()) {
						ans = list.stream().mapToDouble(md -> md.distanceWith(position)).min().orElseThrow(RuntimeException::new);
					} else {
						ans = list.stream().mapToDouble(md -> md.distanceWith(position)).max().orElseThrow(RuntimeException::new);
					}
					query += System.currentTimeMillis() - tmp;
					if(LOG_DETAIL) {
						System.out.printf("\tQuery (%d, %d, %d). Found: %f.\n",
								position.getDimension(0), position.getDimension(1), position.getDimension(2), ans);
					}
				} else {
					if(!list.contains(position)) {
						tmp = System.currentTimeMillis();
						list.add(position);
						insert += System.currentTimeMillis() - tmp;
						if (LOG_DETAIL) {
							System.out.printf("\tAdd (%d, %d, %d).\n", position.getDimension(0), position.getDimension(1), position.getDimension(2));
						}
					}
				}
			}
		}
		long endBf = System.currentTimeMillis();
		System.out.printf("Brute Force: %dms\n", endBf - beginBf);
		System.out.printf("\tinsert: %dms, remove: %dms, query: %dms\n", insert, remove, query);
		insert = remove = query = 0;
		int cnt = 0;
		long beginKdt = System.currentTimeMillis();
		KDTree<Integer, Integer> kdt = KDTree.newLinkedKDTree(3);
		kdt.build(Arrays.stream(arr).map(p -> new KDTree.BuildNode<>(p, Algorithm.randInt(-1024, 0))).toArray(KDTree.BuildNode[]::new));
		for(int i = 0; i < 32768; ++i) {
			int a = Algorithm.randInt(0, 10000);
			if(a < kdt.size()) {
				IntPosition p0 = new IntPosition(Algorithm.randInt(-XZBound, XZBound), Algorithm.randInt(-YBound, YBound * 2), Algorithm.randInt(-XZBound, XZBound));
				tmp = System.currentTimeMillis();
				KDTree.KDNode<Integer, Integer> kdn = kdt.findClosest(p0);
				IMultidimensional<Integer> position = kdn.value();
				kdt.remove(kdn);
				remove += System.currentTimeMillis() - tmp;
				if(LOG_DETAIL) {
					System.out.printf("\tRemove (%d, %d, %d).\n", position.getDimension(0), position.getDimension(1), position.getDimension(2));
				}
			} else {
				IntPosition position = new IntPosition(Algorithm.randInt(-XZBound, XZBound), Algorithm.randInt(-YBound, YBound * 2), Algorithm.randInt(-XZBound, XZBound));
				if((a & 0x1) != 0 && !kdt.isEmpty()) {
					double ans;
					tmp = System.currentTimeMillis();
					if(Algorithm.randBool()) {
						ans = kdt.findClosest(position).distanceWith(position);
					} else {
						ans = kdt.findFarthest(position).distanceWith(position);
					}
					query += System.currentTimeMillis() - tmp;
					if(LOG_DETAIL) {
						System.out.printf("\tQuery (%d, %d, %d). Found: %f.\n",
								position.getDimension(0), position.getDimension(1), position.getDimension(2), ans);
					}
				} else {
					tmp = System.currentTimeMillis();
					kdt.insert(KDTree.BuildNode.of(cnt, position));
					insert += System.currentTimeMillis() - tmp;
					if (LOG_DETAIL) {
						System.out.printf("\tAdd (%d, %d, %d).\n", position.getDimension(0), position.getDimension(1), position.getDimension(2));
					}
					cnt += 1;
				}
			}
		}
		long endKdt = System.currentTimeMillis();
		System.out.printf("KDT: %dms\n", endKdt - beginKdt);
		System.out.printf("\tinsert: %dms, remove: %dms, query: %dms\n", insert, remove, query);
	}

	private static void testFenwickTree1D() throws IOException {
		//https://www.luogu.com.cn/problem/P3374

		int n, m, op, x, y;
		AtomicBoolean failed = new AtomicBoolean(false);
		java.util.Scanner in = new java.util.Scanner(Files.newInputStream(Paths.get(FOLDER + "ft.in")));
		java.util.Scanner out = new java.util.Scanner(Files.newInputStream(Paths.get(FOLDER + "ft.out")));
		n = in.nextInt();
		m = in.nextInt();
		Integer[] arr = new Integer[n];
		for(int i = 0; i < n; ++i) {
			arr[i] = in.nextInt();
		}
		ArrayFenwickTree1D<Integer> ft = new ArrayFenwickTree1D<>(arr, EditRules.Integer.sumAdd(), Integer[]::new);
		for(int i = 0; i < m; ++i) {
			op = in.nextInt();
			x = in.nextInt();
			y = in.nextInt();
			switch(op) {
				case 1:
					ft.edit(y, x - 1);
					break;
				case 2:
					int output = ft.query(x - 1, y);
					int ans = out.nextInt();
					if(output != ans) {
						System.out.printf("Wrong output! Expect: %d. Found: %d.\n", ans, output);
						failed.set(true);
					}
					break;
				default:
					break;
			}
		}
		ft.visit(n, i -> {
			int ans = out.nextInt();
			if(i != ans) {
				System.out.printf("Wrong output! Expect: %d. Found: %d.\n", ans, i);
				failed.set(true);
			}
		});
		System.out.print("Test Fenwick Tree: ");
		System.out.println(failed.get() ? "\033[31mTEST FAILED!!!\033[0m" : "\033[32mTEST PASSED.\033[0m");
	}

	@SuppressWarnings("CallToPrintStackTrace")
	public static void main(String[] args) {
		Algorithm.setSeed(42);
		try {
			testKDTree1();
			testKDTree2();
			testKDTree();
			testKDTreeMaintainability();
			testKDTreeTime();
			testSegmentTree1();
			testSegmentTree();
			testFenwickTree1D();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
