package com.hexagram2021.tetrachordlib.benchmark;

import com.hexagram2021.tetrachordlib.core.container.SegmentTree2D;
import com.hexagram2021.tetrachordlib.core.container.impl.EditRules;
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

/**
 * Please only reproduce this in the first single-player world you enter, or a server, to avoid memory leakage.
 * Or you can edit the code to free keys from previous single-player world of campfires.<br>
 *
 * Requirement: when mining a block next to a beacon, the following ore blocks provides "ore score" buff to nearby block positions:<br>
 * <table cellspacing=12>
 *     <th>
 *         <td>Copper</td>
 *         <td>Iron</td>
 *         <td>Gold</td>
 *         <td>Diamond</td>
 *         <td>Netherite</td>
 *     </th>
 *     <tr>
 *         <td>Radius</td>
 *         <td>5x5</td>
 *         <td>9x9</td>
 *         <td>13x13</td>
 *         <td>17x17</td>
 *         <td>19x19</td>
 *     </tr>
 *     <tr>
 *         <td>Score</td>
 *         <td>1</td>
 *         <td>1</td>
 *         <td>1</td>
 *         <td>2</td>
 *         <td>3</td>
 *     </tr>
 * </table>
 * And assume that we are mining a block on an x-scored block position, we will finally get [(x + 1) / 2] XP from the block.<br>
 * For example, we get 6 netherite blocks on (0, 0), 5 netherite blocks on (18, 16), 3 diamond blocks on (-1, -1). Then the "ore score" of (-2, -2) is 3*6 + 3*2 = 24
 * (because netherite blocks in (18, 16) cannot affect (-2, -2) for their distance is 17). So if you mine a stone block, you'll get 12 XP.
 */
public class OreBlocksNearBeaconIncreaseXpDropImproved {
	static final int BEACON_RADIUS = 32;
	static final int BEACON_Y_RADIUS = 32;

	@Nullable
	BlockPos beacon = null;

	SegmentTree2D<Integer> st = SegmentTree2D.newArrayQuadSegmentTree2D((BEACON_RADIUS << 1) + 1, EditRules.Integer.sumAdd(), Integer[]::new);

	private static void edit(SegmentTree2D<Integer> st, int count, int x, int z, int radius) {
		st.edit(count, Math.max(x - radius + BEACON_RADIUS, 0), Math.min(x + radius + 1 + BEACON_RADIUS, (BEACON_RADIUS << 1) + 1), Math.max(z - radius + BEACON_RADIUS, 0), Math.min(z + radius + 1 + BEACON_RADIUS, (BEACON_RADIUS << 1) + 1));
	}

	private static void build(ServerLevel serverLevel, BlockPos beacon, SegmentTree2D<Integer> st) {
		int count = 0;
		for(int x = -BEACON_RADIUS; x <= BEACON_RADIUS; ++x) {
			for(int y = -BEACON_Y_RADIUS; y <= BEACON_Y_RADIUS; ++y) {
				for(int z = -BEACON_RADIUS; z <= BEACON_RADIUS; ++z) {
					BlockState blockState = serverLevel.getBlockState(beacon.offset(x, y, z));
					if(blockState.is(Blocks.COPPER_BLOCK)) {
						edit(st, 1, x, z, 2);
					} else if(blockState.is(Blocks.IRON_BLOCK)) {
						edit(st, 1, x, z, 4);
					} else if(blockState.is(Blocks.GOLD_BLOCK)) {
						edit(st, 1, x, z, 6);
					//} else if(blockState.is(Blocks.DIAMOND_BLOCK)) {
					//	edit(st, 2, x, z, 8);
					//} else if(blockState.is(Blocks.NETHERITE_BLOCK)) {
					//	edit(st, 3, x, z, 9);
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
			int x = pos.getX() - this.beacon.getX(), z = pos.getZ() - this.beacon.getZ();
			if(blockState.is(Blocks.COPPER_BLOCK)) {
				edit(this.st, 1, x, z, 2);
			} else if(blockState.is(Blocks.IRON_BLOCK)) {
				edit(this.st, 1, x, z, 4);
			} else if(blockState.is(Blocks.GOLD_BLOCK)) {
				edit(this.st, 1, x, z, 6);
			//} else if(blockState.is(Blocks.DIAMOND_BLOCK)) {
			//	edit(this.st, 2, x, z, 8);
			//} else if(blockState.is(Blocks.NETHERITE_BLOCK)) {
			//	edit(this.st, 3, x, z, 9);
			}
			return;
		}
		if(blockState.is(Blocks.BEACON)) {
			this.beacon = pos;
			build(serverLevel, this.beacon, this.st);
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
			int x = pos.getX() - this.beacon.getX(), z = pos.getZ() - this.beacon.getZ();
			if(Math.abs(x) <= BEACON_RADIUS && Math.abs(pos.getY() - this.beacon.getY()) <= BEACON_Y_RADIUS && Math.abs(z) < BEACON_RADIUS) {
				e.setExpToDrop(e.getExpToDrop() + this.st.query(x + BEACON_RADIUS, x + 1 + BEACON_RADIUS, z + BEACON_RADIUS, z + 1 + BEACON_RADIUS));
			}
			if(blockState.is(Blocks.COPPER_BLOCK)) {
				edit(this.st, -1, x, z, 2);
			} else if(blockState.is(Blocks.IRON_BLOCK)) {
				edit(this.st, -1, x, z, 4);
			} else if(blockState.is(Blocks.GOLD_BLOCK)) {
				edit(this.st, -1, x, z, 6);
			//} else if(blockState.is(Blocks.DIAMOND_BLOCK)) {
			//	edit(this.st, -2, x, z, 8);
			//} else if(blockState.is(Blocks.NETHERITE_BLOCK)) {
			//	edit(this.st, -3, x, z, 9);
			}
			return;
		}
		if(blockState.is(Blocks.BEACON)) {
			this.beacon = null;
		}
	}
}
