package com.hexagram2021.tetrachordlib.core.container;

/**
 * Jesus, what make you do this? I'm sorry to say that you should implement this by yourself.
 * @see SegmentTree1D
 * @see SegmentTree2D
 */
@SuppressWarnings("unused")
public interface SegmentTree3D<T> {
	/**
	 * Edit a volume [beginX, endX)x[beginY, endY)x[beginZ, endZ), notice that begin is inclusive while end is exclusive.
	 * @param delta		Amount of the edit.
	 * @param beginX	West bound (inclusive) of x-axis of the volume.
	 * @param endX		East bound (exclusive) of x-axis of the volume.
	 * @param beginY	Top bound (inclusive) of y-axis of the volume.
	 * @param endY		Bottom bound (exclusive) of y-axis of the volume.
	 * @param beginZ	North bound (inclusive) of z-axis of the volume.
	 * @param endZ		South bound (exclusive) of z-axis of the volume.
	 */
	void edit(T delta, int beginX, int endX, int beginY, int endY, int beginZ, int endZ);

	/**
	 * Query the combination of a volume [beginX, endX)x[beginY, endY)x[beginZ, endZ), notice that begin is inclusive while end is exclusive.
	 * @param beginX	West bound (inclusive) of x-axis of the volume.
	 * @param endX		East bound (exclusive) of x-axis of the volume.
	 * @param beginY	Top bound (inclusive) of y-axis of the volume.
	 * @param endY		Bottom bound (exclusive) of y-axis of the volume.
	 * @param beginZ	North bound (inclusive) of z-axis of the volume.
	 * @param endZ		South bound (exclusive) of z-axis of the volume.
	 * @return			The combination of a volume.
	 */
	T query(int beginX, int endX, int beginY, int endY, int beginZ, int endZ);

	IEditRule<T> getEditRule();

	void visit(int beginX, int endX, int beginY, int endY, int beginZ, int endZ, VisitConsumer<T> consumer);

	@FunctionalInterface
	interface VisitConsumer<T> {
		void accept(int x, int y, int z, T value);
	}
}
