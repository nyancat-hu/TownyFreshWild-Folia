package com.mcla.townyfreshwildfolia.regentask;

import com.mcla.townyfreshwildfolia.regentask.versions.RegenTask_v1_20_R1;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.function.Consumer;


public abstract class RegenTask implements Runnable , Consumer<ScheduledTask> {

	// -------------------------------------------------- //
	// CONSTRUCT  
	// -------------------------------------------------- //

	public static RegenTask get(Chunk chunk) {
		try {
			Class.forName("net.minecraft.server.v1_20_R1.World");
			return new RegenTask_v1_20_R1(chunk);
		} catch (Exception e) {
		}

		return null;
	}

	protected RegenTask(Chunk chunk) {
		this.chunk = chunk;
	}

	// -------------------------------------------------- //
	// FIELDS  
	// -------------------------------------------------- //

	private Chunk chunk;

	// -------------------------------------------------- //
	// METHODS  
	// -------------------------------------------------- //

	@SuppressWarnings("deprecation")
	@Override
	public final void run() {
		// Set some variables that we will use later
		World world = this.chunk.getWorld();

		// Regenerate the chunk
		this.regenerateChunk(world, this.chunk.getX(), this.chunk.getZ());

		// 是否替换区块中的block，由于比较损耗性能，这里取消
//		if ( ! Config.get().removeMaterialsOnRegen.isEmpty()) {
//			int bx = this.chunk.getX() << 4;
//			int bz = this.chunk.getZ() << 4;
//
//			for (int xx = bx; xx < bx+16; xx++) {
//				for (int zz = bz; zz < bz+16; zz++) {
//					for (int yy = 0; yy < 128; yy++) {
//						Block block = world.getBlockAt(xx, yy, zz);
//						if ( ! Config.get().removeMaterialsOnRegen.contains(block.getType().name())) continue;
//
//						// Run this task asynchronously and on the minecraft API - do not call bukkit api here
//						FreshWilderness.get().getServer().getScheduler().runTaskAsynchronously(FreshWilderness.get(), new Runnable() {
//							private RegenTask task;
//							private World world;
//							private int x;
//							private int y;
//							private int z;
//							private int blockId;
//							private byte data;
//
//							@Override
//							public void run() {
//								// call the setBlockFast method
//								this.task.setBlockFast(this.world, this.x, this.y, this.z, this.blockId, data);
//							}
//
//							public Runnable prepareBlockFast(RegenTask task, World world, int x, int y, int z, int blockId, byte data) {
//								this.task = task;
//								this.world = world;
//								this.x = x;
//								this.y = y;
//								this.z = z;
//								this.blockId = blockId;
//								this.data = data;
//
//								return this;
//							}
//						}.prepareBlockFast(this,world, xx, yy, zz, 0, (byte) 0));
//
//						// TODO: which clients does this not work for? Is there an alternative?
//						world.refreshChunk(chunk.getX(), chunk.getZ());
//					}
//				}
//			}
//
//			// Remove entities if required
//			if (Config.get().removeEntitiesOnRegen) {
//				for (Entity entity : this.chunk.getEntities()) {
//					if (Config.get().excludeEntitiesOnRegen.contains(entity.getType().getName())) continue;
//
//					entity.remove();
//				}
//			}
//		}
	}

	public abstract void setBlockFast(World world, int x, int y, int z, int blockId, byte data);

	public abstract void regenerateChunk(World world, int x, int z);
}

