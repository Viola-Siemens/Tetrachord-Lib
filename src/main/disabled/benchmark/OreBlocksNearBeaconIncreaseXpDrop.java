package com.hexagram2021.tetrachordlib.benchmark;

import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static com.hexagram2021.tetrachordlib.benchmark.OreBlocksNearBeaconIncreaseXpDropImproved.*;

public class OreBlocksNearBeaconIncreaseXpDrop {
	@Nullable
	BlockPos beacon = null;

	private record Record(BlockPos blockPos, Integer count, Integer radius) {
	}

	List<Record> ores = Lists.newArrayList();

	private int query(int beginX, int endX, int beginZ, int endZ) {
		Objects.requireNonNull(this.beacon);
		return this.ores.stream().filter(
				record -> beginX <= record.blockPos().getX() + record.radius && endX > record.blockPos().getX() - record.radius &&
						beginZ <= record.blockPos().getZ() + record.radius && endZ > record.blockPos().getZ() - record.radius &&
						Math.abs(record.blockPos().getX() - this.beacon.getX()) <= BEACON_RADIUS && Math.abs(record.blockPos().getZ() - this.beacon.getZ()) <= BEACON_RADIUS
		).mapToInt(Record::count).sum();
	}

	private static final int PLATFORM_SIDE_SIZE = 195;
	private static void build(ServerLevel serverLevel, BlockPos beacon, List<Record> ores) {
		int count = 0;
		ores.clear();
		for(int x = -PLATFORM_SIDE_SIZE; x <= PLATFORM_SIDE_SIZE; ++x) {
			for(int y = -BEACON_Y_RADIUS; y <= BEACON_Y_RADIUS; ++y) {
				for(int z = -PLATFORM_SIDE_SIZE; z <= PLATFORM_SIDE_SIZE; ++z) {
					BlockPos newPos = beacon.offset(x, y, z);
					BlockState blockState = serverLevel.getBlockState(newPos);
					if(blockState.is(Blocks.COPPER_BLOCK)) {
						ores.add(new Record(newPos, 1, 2));
					} else if(blockState.is(Blocks.IRON_BLOCK)) {
						ores.add(new Record(newPos, 1, 4));
					} else if(blockState.is(Blocks.GOLD_BLOCK)) {
						ores.add(new Record(newPos, 1, 6));
					} else if(blockState.is(Blocks.DIAMOND_BLOCK)) {
						ores.add(new Record(newPos, 2, 8));
					} else if(blockState.is(Blocks.NETHERITE_BLOCK)) {
						ores.add(new Record(newPos, 3, 9));
					} else {
						continue;
					}
					count += 1;
				}
			}
		}
		serverLevel.getServer().getPlayerList().broadcastMessage(new TextComponent("Loaded %d ore blocks.".formatted(count)), ChatType.SYSTEM, Util.NIL_UUID);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBeaconPlace(BlockEvent.EntityPlaceEvent e) {
		if (!(e.getWorld() instanceof ServerLevel serverLevel)) {
			return;
		}
		if (e.isCanceled()) {
			return;
		}
		BlockState blockState = e.getPlacedBlock();
		BlockPos pos = e.getPos();
		if(this.beacon != null) {
			if(blockState.is(Blocks.COPPER_BLOCK)) {
				this.ores.add(new Record(pos, 1, 2));
			} else if(blockState.is(Blocks.IRON_BLOCK)) {
				this.ores.add(new Record(pos, 1, 4));
			} else if(blockState.is(Blocks.GOLD_BLOCK)) {
				this.ores.add(new Record(pos, 1, 6));
			} else if(blockState.is(Blocks.DIAMOND_BLOCK)) {
				this.ores.add(new Record(pos, 2, 8));
			} else if(blockState.is(Blocks.NETHERITE_BLOCK)) {
				this.ores.add(new Record(pos, 3, 9));
			}
			return;
		}
		if(blockState.is(Blocks.BEACON)) {
			this.beacon = pos;
			build(serverLevel, this.beacon, this.ores);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBreakBlock(BlockEvent.BreakEvent e) {
		if (!(e.getWorld() instanceof ServerLevel)) {
			return;
		}
		if (e.isCanceled()) {
			return;
		}
		BlockState blockState = e.getState();
		BlockPos pos = e.getPos();
		if(this.beacon != null) {
			if(Math.abs(pos.getX() - this.beacon.getX()) <= BEACON_RADIUS && Math.abs(pos.getY() - this.beacon.getY()) <= BEACON_Y_RADIUS && Math.abs(pos.getZ() - this.beacon.getZ()) < BEACON_RADIUS) {
				e.setExpToDrop(e.getExpToDrop() + query(pos.getX(), pos.getX() + 1, pos.getZ(), pos.getZ() + 1));
			}
			this.ores.removeIf(record -> record.blockPos().equals(pos));
			return;
		}
		if(blockState.is(Blocks.BEACON)) {
			this.beacon = null;
		}
	}
}
