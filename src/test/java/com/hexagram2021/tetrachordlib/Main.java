package com.hexagram2021.tetrachordlib;

import com.google.common.collect.Lists;
import com.hexagram2021.tetrachordlib.core.algorithm.Algorithm;
import com.hexagram2021.tetrachordlib.core.container.*;
import com.hexagram2021.tetrachordlib.core.container.impl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

	private static void testFenwickTree1D1() throws IOException {
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
		System.out.print("Test Case (Fenwick Tree 1D): ");
		System.out.println(failed.get() ? "\033[31mTEST FAILED!!!\033[0m" : "\033[32mTEST PASSED.\033[0m");
	}

	private static void testFenwickTree1D() {
		final int SIZE = 1020;
		int[] array = new int[SIZE];
		Integer[] build = new Integer[SIZE];
		AtomicBoolean failed = new AtomicBoolean(false);
		for(int i = 0; i < SIZE; ++i) {
			build[i] = array[i] = Algorithm.randInt(0, 32);
		}
		FenwickTree1D<Integer> ft = FenwickTree1D.newArrayFenwickTree1D(build, EditRules.Integer.sumAdd(), Integer[]::new);
		for(int i = 0; i < 32768; ++i) {
			int x = Algorithm.randInt(0, SIZE - 1);
			if(Algorithm.randInt(0, 4) == 0) {
				int k = Algorithm.randInt(-8, 8);
				if(k == 0) {
					k = 8;
				}
				array[x] += k;
				ft.edit(k, x);
				if(LOG_DETAIL) {
					System.out.printf("\tEdit %d at (%d).\n", k, x);
				}
			} else {
				int x2 = Algorithm.randInt(x + 1, SIZE + 1);
				int output = ft.query(x, x2);
				int ans = 0;
				for(int dx = x; dx < x2; ++dx) {
					ans += array[dx];
				}
				if(LOG_DETAIL) {
					System.out.printf("\tQuery: (%d, %d).Expect: %d. Found: %d.\n", x, x2, ans, output);
				}
				if(Math.abs(ans - output) > 1e-6) {
					System.out.printf("Wrong output! Expect: %d. Found: %d.\n", ans, output);
					failed.set(true);
				}
			}
		}
		final AtomicInteger cnt = new AtomicInteger(0);
		ft.visit(SIZE, value -> {
			if(array[cnt.get()] != value) {
				System.out.printf("Wrong visit! Expect: %d. Found: %d.\n", array[cnt.get()], value);
				failed.set(true);
			}
			cnt.addAndGet(1);
		});
		System.out.print("Test Fenwick Tree 1D: ");
		System.out.println(failed.get() ? "\033[31mTEST FAILED!!!\033[0m" : "\033[32mTEST PASSED.\033[0m");
	}

	public static void testFenwickTree2D1() throws IOException {
		//https://loj.ac/p/133

		int n, m, op, x1, y1, x2, y2;
		java.util.Scanner in = new java.util.Scanner(Files.newInputStream(Paths.get(FOLDER + "ft2d.in")));
		java.util.Scanner out = new java.util.Scanner(Files.newInputStream(Paths.get(FOLDER + "ft2d.out")));
		AtomicBoolean failed = new AtomicBoolean(false);
		n = in.nextInt();
		m = in.nextInt();
		FenwickTree2D<Integer> ft = FenwickTree2D.newArrayFenwickTree2D(n, m, EditRules.Integer.sumAdd(), (x, y) -> new Integer[x][y]);
		while(in.hasNext()) {
			op = in.nextInt();
			x1 = in.nextInt();
			y1 = in.nextInt();
			x2 = in.nextInt();
			if(op == 1) {
				ft.edit(x2, x1 - 1, y1 - 1);
			} else {
				y2 = in.nextInt();
				int output = ft.query(x1 - 1, x2, y1 - 1, y2);
				int ans = out.nextInt();
				if(output != ans) {
					System.out.printf("Wrong output! Expect: %d. Found: %d.\n", ans, output);
					failed.set(true);
				}
			}
		}
		ft.visit(n, m, (x, y, value) -> {
			int ans = out.nextInt();
			if(ans != value) {
				System.out.printf("Wrong visit at (%d, %d)! Expect: %d. Found: %d.\n", x, y, ans, value);
				failed.set(true);
			}
		});
		System.out.print("Test Case (Fenwick Tree 2D): ");
		System.out.println(failed.get() ? "\033[31mTEST FAILED!!!\033[0m" : "\033[32mTEST PASSED.\033[0m");
	}

	private static void testFenwickTree2D() {
		final int SIZE = 1020;
		int[][] array = new int[SIZE][SIZE >> 1];
		Integer[][] build = new Integer[SIZE][SIZE >> 1];
		AtomicBoolean failed = new AtomicBoolean(false);
		for(int i = 0; i < SIZE; ++i) {
			for(int j = 0; j < (SIZE >> 1); ++j) {
				build[i][j] = array[i][j] = Algorithm.randInt(0, 32);
			}
		}
		FenwickTree2D<Integer> ft = FenwickTree2D.newArrayFenwickTree2D(build, EditRules.Integer.sumAdd(), (xs, ys) -> new Integer[xs][ys]);
		for(int i = 0; i < 32768; ++i) {
			int x = Algorithm.randInt(0, SIZE - 1);
			int y = Algorithm.randInt(0, (SIZE >> 1) - 1);
			if(Algorithm.randInt(0, 4) == 0) {
				int k = Algorithm.randInt(-8, 8);
				if(k == 0) {
					k = 8;
				}
				array[x][y] += k;
				ft.edit(k, x, y);
				if(LOG_DETAIL) {
					System.out.printf("\tEdit %d at (%d, %d).\n", k, x, y);
				}
			} else {
				int x2 = Algorithm.randInt(x + 1, SIZE + 1);
				int y2 = Algorithm.randInt(y + 1, (SIZE >> 1) + 1);
				int output = ft.query(x, x2, y, y2);
				int ans = 0;
				for(int dx = x; dx < x2; ++dx) {
					for(int dy = y; dy < y2; ++dy) {
						ans += array[dx][dy];
					}
				}
				if(LOG_DETAIL) {
					System.out.printf("\tQuery: (%d, %d, %d, %d).Expect: %d. Found: %d.\n", x, x2, y, y2, ans, output);
				}
				if(Math.abs(ans - output) > 1e-6) {
					System.out.printf("Wrong output! Expect: %d. Found: %d.\n", ans, output);
					failed.set(true);
				}
			}
		}
		ft.visit(SIZE, SIZE >> 1, (x, y, value) -> {
			if(array[x][y] != value) {
				System.out.printf("Wrong visit! Expect: %d. Found: %d.\n", array[x][y], value);
				failed.set(true);
			}
		});
		System.out.print("Test Fenwick Tree 2D: ");
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
			testFenwickTree1D1();
			testFenwickTree2D1();
			testFenwickTree1D();
			testFenwickTree2D();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
