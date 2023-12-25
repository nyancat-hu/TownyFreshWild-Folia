package com.mcla.townyfreshwildfolia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.mcla.townyfreshwildfolia.exceptions.IntegrationNotAddedException;
import com.mcla.townyfreshwildfolia.integrations.Ignition;
import com.mcla.townyfreshwildfolia.integrations.Integrations;
import com.mcla.townyfreshwildfolia.integrations.towny.TownyIgnition;
import com.mcla.townyfreshwildfolia.regentask.RegenTask;
import com.mcla.townyfreshwildfolia.store.Config;
import com.mcla.townyfreshwildfolia.store.WildernessLog;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FreshWilderness extends JavaPlugin implements Listener {

	// -------------------------------------------------- //
	// SINGLETON  
	// -------------------------------------------------- //
	
	private static FreshWilderness i;
	public static FreshWilderness get() { return i; }
	public FreshWilderness() { i = this; }
	
	// -------------------------------------------------- //
	// FIELDS  
	// -------------------------------------------------- //
	
	private ScheduledTask taskid;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	// -------------------------------------------------- //
	// METHODS  
	// -------------------------------------------------- //
	
	@Override
	public void onEnable() {
		try {
			Config.get().save();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Integrations.get().addIntegration(TownyIgnition.get(), true);
		
		// Register our main listener 
		this.getServer().getPluginManager().registerEvents(this, this);
		
		// Start our checkChunks task
		this.taskid = Bukkit.getGlobalRegionScheduler().runAtFixedRate(this, task->checkChunks(), 0L, 20L * Config.get().secondsBetweenResets);
	}
	
	@Override
	public void onDisable() {
		// cancel the task
		Bukkit.getGlobalRegionScheduler().cancelTasks(this);
		
		// Save our logs
		for (WildernessLog log : WildernessLog.getAll()) {
			try {
				log.save();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// Unregister our main listener
		HandlerList.unregisterAll((Listener) this); 
		
		// we can safely go over this list as it is a copy
		for (Ignition ignition : Integrations.get().getEnabled()) {
			try {
				Integrations.get().disable(ignition);
			} catch (IntegrationNotAddedException e) {
				// don't really need to worry about it
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().hasPermission("freshwilderness.ignore")) return;
		
		Chunk chunk = event.getBlock().getChunk();
		
		this.logChunk(chunk);
	}
	
	public void logChunk(Chunk chunk) {		
		if ( ! Integrations.get().shouldLogAt(chunk)) return;
		
		String key = chunk.getX() + ":" + chunk.getZ();
		
		WildernessLog log = WildernessLog.get(chunk.getWorld());
				
		log.chunks.put(key, System.currentTimeMillis());
	}
	
	public final void checkChunks() {
		for (WildernessLog log : new ArrayList<WildernessLog>(WildernessLog.getAll())) {
			for (Entry<String, Long> chunk : new HashMap<String, Long>(log.chunks).entrySet()) {
				// See how old the chunk is  
				Long secs = (System.currentTimeMillis() * 1000) - (chunk.getValue() * 1000);
				
				// Check if the chunk needs to be reset
				if (secs < Config.get().secondsBetweenResets) continue;
				
				// Determine the chunk using the key
				String[] coords = chunk.getKey().split(":");
				
				World world = Bukkit.getWorld(UUID.fromString(log.uid));
				Chunk ch = world.getChunkAt(Integer.valueOf(coords[0]), Integer.valueOf(coords[1]));
				
				// Find all players in the chunk
				List<Player> chPlayers = new ArrayList<Player>();
				for (Player chPlayer : ch.getWorld().getPlayers()) {
					if (chPlayer.getLocation().getChunk() != ch) continue;
					chPlayers.add(chPlayer);
				}
				
				// Some servers will want chunks to reset even if a player is in the chunk
				if ( ! Config.get().resetEvenIfPlayerInChunk) {
					// found players, so lets skip this chunk for now - we'll check in the next round
					if (chPlayers.size() > 0) continue;
				}
				
				// Check if any integration wants us to stop
				for (Ignition intergration : Integrations.get().getEnabled()) {
					if ( ! intergration.getEngine().shouldReset(ch)) return;
				}
				
				// Run our resets
				for (Ignition intergration : Integrations.get().getEnabled()) {
					if ( ! intergration.getEngine().runReset(ch)) return;
				}
				
				// Remove it from the log
				log.chunks.remove(chunk.getKey());
				
				// Run our regeneration task a tick later (and a few seconds later if we have to)
				RegenTask task = RegenTask.get(ch);

                if (task != null) {
                    Bukkit.getRegionScheduler().runDelayed(FreshWilderness.get(),ch.getBlock(8,64,8).getLocation(),task,1 + (20 * Config.get().secondsBeforeReset));
                }
//				FreshWilderness.get().getServer().getScheduler().runTaskLater(FreshWilderness.get(), task, 1 + (20 * Config.get().secondsBeforeReset));
			}
			
			try {
				log.save();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public final Gson getGson() {
		return this.gson;
	}
	
}
