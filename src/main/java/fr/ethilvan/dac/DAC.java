package fr.ethilvan.dac;

import fr.ethilvan.dac.commands.DacCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class DAC extends JavaPlugin {


	public DAC() {

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
