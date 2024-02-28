package com.hexagram2021.tetrachordlib.core.algorithm;

import java.util.Comparator;
import java.util.Random;

@SuppressWarnings("unused")
public final class Algorithm {
	private static final Random random = new Random();

	public static int randInt(int lowBound /* inclusive */, int highBound /* exclusive */) {
		return random.nextInt(highBound - lowBound) + lowBound;
	}
	public static boolean randBool() {
		return random.nextBoolean();
	}

	/**
	 * Set a random seed.
	 * @param seed	New random seed.
	 */
	public static void setSeed(long seed) {
		random.setSeed(seed);
	}

	private Algorithm() {
	}

	/**
	 * @param arr	Array to be quick sorted or quick selected.
	 * @return		Index of axle.
	 * @param <T>	Type of the array.
	 */
	private static <T extends Comparable<T>> int makeAxle(T[] arr, int beg, int end) {
		int index = random.nextInt(end - beg) + beg;
		T frog = arr[index];
		arr[index] = arr[beg];
		for(;;) {
			while(beg < end) {
				end -= 1;
				if(frog.compareTo(arr[end]) >= 0) {
					arr[beg] = arr[end];
					break;
				}
			}
			if(beg >= end) {
				arr[beg] = frog;
				return beg;
			}
			while(beg < end) {
				beg += 1;
				if(arr[beg].compareTo(frog) >= 0) {
					arr[end] = arr[beg];
					break;
				}
			}
			if(beg >= end) {
				arr[end] = frog;
				return end;
			}
		}
	}

	/**
	 * @param arr			Array to be quick sorted or quick selected.
	 * @param comparator	Comparator of the objects.
	 * @return				Index of axle.
	 * @param <T>			Type of the array.
	 */
	private static <T> int makeAxle(T[] arr, int beg, int end, Comparator<T> comparator) {
		int index = random.nextInt(end - beg) + beg;
		T frog = arr[index];
		arr[index] = arr[beg];
		for(;;) {
			while(beg < end) {
				end -= 1;
				if(comparator.compare(frog, arr[end]) >= 0) {
					arr[beg] = arr[end];
					break;
				}
			}
			if(beg >= end) {
				arr[beg] = frog;
				return beg;
			}
			while(beg < end) {
				beg += 1;
				if(comparator.compare(arr[beg], frog) >= 0) {
					arr[end] = arr[beg];
					break;
				}
			}
			if(beg >= end) {
				arr[end] = frog;
				return end;
			}
		}
	}

	public static <T extends Comparable<T>> void quickSelect(T[] arr, int beg, int end, int kth) {
		if(end - beg < 2) return;
		int ax = makeAxle(arr, beg, end);
		int m = ax - beg;
		if(m == kth) return;
		if(m > kth) {
			quickSelect(arr, beg, ax, kth);
		} else {
			quickSelect(arr, ax + 1, end, kth - m - 1);
		}
	}

	public static <T> void quickSelect(T[] arr, int beg, int end, int kth, Comparator<T> comparator) {
		if(end - beg < 2) return;
		int ax = makeAxle(arr, beg, end, comparator);
		int m = ax - beg;
		if(m == kth) return;
		if(m > kth) {
			quickSelect(arr, beg, ax, kth, comparator);
		} else {
			quickSelect(arr, ax + 1, end, kth - m - 1, comparator);
		}
	}

	public static int lowbit(int x) {
		return x & -x;
	}
	public static int highbit(int x) {
		int lb = lowbit(x);
		for(;;) {
			x -= lb;
			int b = lowbit(x);
			if(b > 0) {
				lb = b;
			} else {
				break;
			}
		}
		return lb;
	}
}
