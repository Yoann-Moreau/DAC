package fr.ethilvan.dac;

import fr.ethilvan.dac.commands.DacCommand;
import fr.ethilvan.dac.game.DacGame;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class DAC extends JavaPlugin {

	private HashMap<String, DacGame> games;

	public DAC() {
		this.games = new HashMap<>();
	}


	public HashMap<String, DacGame> getGames() {
		return games;
	}

	public void setGames(HashMap<String, DacGame> games) {
		this.games = games;
	}

	public void addGame(String dacName, DacGame dacGame) {
		if (!this.games.containsKey(dacName)) {
			this.games.put(dacName, dacGame);
		}
	}

	public void removeGame(String dacName) {
		this.games.remove(dacName);
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
