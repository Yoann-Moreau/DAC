package fr.ethilvan.dac;

import org.bukkit.plugin.java.JavaPlugin;

public final class DAC extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().info("Enabled " + this.getName());
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabled " + this.getName());
	}
}
