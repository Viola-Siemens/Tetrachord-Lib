package com.hexagram2021.tetrachordlib.vanilla;

import com.hexagram2021.tetrachordlib.core.container.impl.DoublePosition;
import com.hexagram2021.tetrachordlib.core.container.impl.IntPosition;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;

@SuppressWarnings("unused")
public final class MDUtils {
	private MDUtils() {
	}

	/**
	 * It is recommended to define your own function (e.g. <code>static IntPosition blockPos(BlockPos blockPos)</code>) to avoid misuse.
	 * @param vec3i 	BlockPos, SectionPos or anything else.
	 */
	public static IntPosition vec3i(Vec3i vec3i) {
		return new IntPosition(vec3i.getX(), vec3i.getY(), vec3i.getZ());
	}
	public static IntPosition chunkPos(ChunkPos chunkPos) {
		return new IntPosition(chunkPos.x, chunkPos.z);
	}

	public static DoublePosition position(Position position) {
		return new DoublePosition(position.x(), position.y(), position.z());
	}
	public static DoublePosition vec3(Vector3f vector3f) {
		return new DoublePosition(vector3f.x(), vector3f.y(), vector3f.z());
	}
	public static DoublePosition vec3(Vector3d vector3d) {
		return new DoublePosition(vector3d.x, vector3d.y, vector3d.z);
	}
}
