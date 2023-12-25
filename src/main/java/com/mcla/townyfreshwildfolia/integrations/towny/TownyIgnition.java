package com.mcla.townyfreshwildfolia.integrations.towny;

import com.mcla.townyfreshwildfolia.integrations.Engine;
import com.mcla.townyfreshwildfolia.integrations.Ignition;
import org.bukkit.Bukkit;


public class TownyIgnition extends Ignition {

	private static TownyIgnition i = new TownyIgnition();
	public static TownyIgnition get() { return i; }
	
	public TownyIgnition() {
		this.setPluginName("Towny");
	}
	
	private TownyEngine engine = null;
	
	@Override
	public Boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Towny");
	}

	@Override
	public Engine getEngine() {
		if (this.engine == null)
			this.engine = new TownyEngine();
		return this.engine;
	}

}
