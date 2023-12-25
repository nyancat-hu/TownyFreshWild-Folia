package com.mcla.townyfreshwildfolia.regentask.versions;

import com.mcla.townyfreshwildfolia.regentask.RegenTask;
import com.palmergames.bukkit.towny.scheduling.ScheduledTask;
import org.bukkit.Chunk;
import org.bukkit.World;

public class RegenTask_v1_20_R1 extends RegenTask {

	public RegenTask_v1_20_R1(Chunk chunk) {
		super(chunk);
	}

	@Override
	public void setBlockFast(World world, int x, int y, int z, int blockId, byte data) {
//		WorldServer w = ((CraftWorld) world).getHandle();
//		Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
//
//
//		BlockPosition bp = new BlockPosition(x, y, z);
//
//		int combined = blockId + (data << 12);
//
//		IBlockData ibd = net.minecraft.server.v1_12_R1.Block.getByCombinedId(combined);
//		chunk.a(bp, ibd);
	}

	@Override
	public void regenerateChunk(World world, int x, int z) {
		world.regenerateChunk(x, z);
	}


	@Override
	public void accept(io.papermc.paper.threadedregions.scheduler.ScheduledTask scheduledTask) {
		run();
	}
}