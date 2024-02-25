package com.hexagram2021.tetrachordlib.core.container;


import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

/**
 * @param <T>	The type of value that this SegmentTree maintains.
 */
public interface IEditRule<T> {
	/**
	 * @return	Default value of type &lt;T&gt; when build a segment tree.
	 */
	@Contract(pure = true)
	T elementDefault();
	/**
	 * @return	Zero value of type &lt;T&gt;.
	 */
	@Contract(pure = true) @Nullable
	T zero();

	/**
	 * This function is the update rule on single element (length = 1) or a segment (length > 1) when each element is edited by delta.<br/>
	 * Example 1: To add delta on each element, for sum, <code>edit(x, delta, length) = x + delta * length</code>;
	 * for maximum, <code>edit(x, delta, length) = x + delta</code>.<br/>
	 * Example 2: To set each element = delta, for sum, <code>edit(x, delta, length) = delta * length</code>;
	 * for maximum, <code>edit(x, delta, length) = delta</code>.
	 * @param x			Original combined value of this segment.
	 * @param delta		Amount of the edit on each element of this segment.
	 * @param length	Length of this segment.
	 * @return			Combined value of this segment after edit.
	 */
	@Contract(pure = true)
	T edit(T x, @Nullable T delta, int length);
	/**
	 * @param x			Original combined value of this area.
	 * @param delta		Amount of the edit on each element of this area.
	 * @param xLength	X-axis length of this area.
	 * @param yLength	Y-axis length of this area.
	 * @return			Combined value of this area after edit.
	 */
	@Contract(pure = true)
	T edit(T x, @Nullable T delta, int xLength, int yLength);
	/**
	 * Example 1: For sum, <code>combine(a, b) = a + b</code>;<br>
	 * Example 2: For min, <code>combine(a, b) = a < b ? a : b</code>;
	 * @return			Combined result.
	 */
	@Contract(pure = true)
	T combine(T a, T b);
	/**
	 * @return			Combined result.
	 */
	@Contract(pure = true)
	T combine(T lu, T ru, T ld, T rd);
	/**
	 * Example 1: To add delta on each element, <code>update(old, delta) = old + delta</code>;<br>
	 * Example 2: To set each element = delta, <code>update(old, delta) = delta</code>;
	 * @param old		Old delta.
	 * @param delta		Amount of the edit on each element of this segment/area.
	 * @return			Combined result.
	 */
	@Contract(pure = true) @Nullable
	T update(@Nullable T old, @Nullable T delta);
}
