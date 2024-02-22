package com.hexagram2021.tetrachordlib.benchmark;

import com.google.common.collect.Maps;
import com.hexagram2021.tetrachordlib.core.container.KDTree;
import com.hexagram2021.tetrachordlib.core.container.impl.IntPosition;
import com.hexagram2021.tetrachordlib.vanilla.MDUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.function.Function;

/**
 * Please only reproduce this in the first single-player world you enter, or a server, to avoid memory leakage.
 * Or you can edit the code to free keys from previous single-player world of campfires.
 */
public class NoHostilesAroundCampfireImproved {
	static final double RADIUS = 32.0D;

	private final Map<Level, KDTree<BlockPos, Integer>> campfires = Maps.newIdentityHashMap();

	private static final Function<Level, KDTree<BlockPos, Integer>> comp = l -> KDTree.newLinkedKDTree(3);

	@SubscribeEvent
	public void onEntityCheckSpawn(LivingSpawnEvent.CheckSpawn e) {
		if (!(e.getWorld() instanceof ServerLevel serverLevel)) {
			return;
		}

		Entity entity = e.getEntity();
		if (!(entity instanceof Mob mob)) {
			return;
		}

		IntPosition current = MDUtils.vec3i(mob.blockPosition());
		KDTree<BlockPos, Integer> kdt = this.campfires.computeIfAbsent(serverLevel, comp);
		if (mob.getType().getCategory().equals(MobCategory.MONSTER) && !kdt.isEmpty() && kdt.findClosest(current).distanceWith(current) <= RADIUS) {
			e.setResult(Event.Result.DENY);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onCampfirePlace(BlockEvent.EntityPlaceEvent e) {
		if (!(e.getWorld() instanceof ServerLevel serverLevel)) {
			return;
		}
		if (e.isCanceled()) {
			return;
		}
		if(e.getPlacedBlock().is(BlockTags.CAMPFIRES) && e.getPlacedBlock().getValue(BlockStateProperties.LIT)) {
			this.campfires.computeIfAbsent(serverLevel, comp).insert(KDTree.BuildNode.of(e.getPos(), MDUtils.vec3i(e.getPos())));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onRightClickCampfireBlock(PlayerInteractEvent.RightClickBlock e) {
		if (!(e.getWorld() instanceof ServerLevel serverLevel)) {
			return;
		}
		if (e.isCanceled()) {
			return;
		}
		BlockPos blockPos = e.getHitVec().getBlockPos();
		BlockState blockState = serverLevel.getBlockState(blockPos);
		KDTree<BlockPos, Integer> kdt = this.campfires.computeIfAbsent(serverLevel, comp);
		if(blockState.is(BlockTags.CAMPFIRES) && blockState.getValue(BlockStateProperties.LIT)) {
			kdt.insert(KDTree.BuildNode.of(blockPos, MDUtils.vec3i(blockPos)));
		} else {
			kdt.remove(MDUtils.vec3i(blockPos));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onCampfireBreak(BlockEvent.BreakEvent e) {
		if (!(e.getWorld() instanceof ServerLevel serverLevel)) {
			return;
		}
		if (e.isCanceled()) {
			return;
		}
		if(e.getState().is(BlockTags.CAMPFIRES) && e.getState().getValue(BlockStateProperties.LIT)) {
			this.campfires.computeIfAbsent(serverLevel, comp).remove(MDUtils.vec3i(e.getPos()));
		}
	}
}
