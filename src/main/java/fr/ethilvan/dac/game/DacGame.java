package fr.ethilvan.dac.game;

import fr.ethilvan.dac.tools.Colors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import java.util.HashMap;

public class DacGame {
	private HashMap<String, Material> players;
	private HashMap<String, Location> locations;


	public DacGame() {
		this.players = new HashMap<>();
		this.locations = new HashMap<>();
	}


	public HashMap<String, Material> getPlayers() {
		return this.players;
	}

	public void setPlayers(HashMap<String, Material> players) {
		this.players = players;
	}

	public void addPlayer(Player player, String color) {
		if (!this.players.containsKey(player.getName())) {
			this.players.put(player.getName(), Colors.convertColorToMaterial(color));
		}
	}

	public void removePlayer(Player player) {
		this.players.remove(player.getName());
	}


	public HashMap<String, Location> getLocations() {
		return this.locations;
	}

	public void setLocations(HashMap<String, Location> locations) {
		this.locations = locations;
	}

	public void addLocation(Player player, Location location) {
		if (!this.locations.containsKey(player.getName())) {
			this.locations.put(player.getName(), location);
		}
	}

	public void removeLocation(Player player) {
		this.locations.remove(player.getName());
	}
}
