package com.mcla.townyfreshwildfolia.integrations.towny;

import com.mcla.townyfreshwildfolia.FreshWilderness;
import com.mcla.townyfreshwildfolia.integrations.Engine;
import com.mcla.townyfreshwildfolia.store.Config;
import com.palmergames.bukkit.towny.event.town.TownUnclaimEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.object.WorldCoord;



public class TownyEngine extends Engine {

	@Override
	public Boolean shouldReset(Chunk chunk) {
		return ( ! this.isClaimed(chunk));
	}

	@Override
	public Boolean runReset(Chunk chunk) {
		return true;
	}

	@Override
	public Boolean shouldLogAt(Chunk chunk) {
		if (this.isClaimed(chunk)) return false;
		
		if (Config.get().dontLogIfClaimNearby) {
			for (int x = chunk.getX() - 1 ; x < 1 ; x = x + 15) {
				for (int z =  chunk.getZ() - 1 ; z < 1 ; z = z + 15) {
					if (Math.sqrt(x * x + z * z) < (double) 1) {
						Chunk nextChunk = chunk.getWorld().getChunkAt(x, z);
						if ( ! this.isClaimed(nextChunk)) continue;
						
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public Boolean isClaimed(Chunk chunk) {
		String world = chunk.getWorld().getName();
		Integer chunkX = chunk.getX();
		Integer chunkZ = chunk.getZ();
		
		WorldCoord worldcoord = new WorldCoord(world, chunkX, chunkZ);
		
		try {
			return worldcoord.getTownBlock().hasTown();
		} catch (Exception e) {
			return false;
		}
	}
	
	@EventHandler
	public void onUnclaim(TownUnclaimEvent event) {
		if ( ! Config.get().logWhenUnclaimed) return;
		
		WorldCoord coord = event.getWorldCoord();
		final Chunk chunk = coord.getBukkitWorld().getChunkAt(coord.getX(), coord.getZ());
		
		// Run log later
		Bukkit.getRegionScheduler().runDelayed(FreshWilderness.get(),chunk.getBlock(8,64,8).getLocation(), task->FreshWilderness.get().logChunk(chunk),20);
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//
//			}
//		}.runTaskLater(FreshWilderness.get(), 20);
	}
	
}
