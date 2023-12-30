package fr.ethilvan.dac;

import com.sk89q.worldedit.regions.Region;
import fr.ethilvan.dac.commands.DacCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class DAC extends JavaPlugin {

	public HashMap<String, Region> regions;


	public DAC() {
		this.regions = new HashMap<>();
	}


	@Override
	public void onEnable() {
		// Register commands
		this.getCommand("dac").setExecutor(new DacCommand(this));

		getLogger().info("Enabled " + this.getName());
	}


	@Override
	public void onDisable() {
		getLogger().info("Disabled " + this.getName());
	}
}
