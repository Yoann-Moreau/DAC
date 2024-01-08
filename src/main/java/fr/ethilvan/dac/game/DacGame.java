package fr.ethilvan.dac.game;

import fr.ethilvan.dac.tools.Colors;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class DacGame {
	private HashMap<String, Material> playerColors;

	private HashMap<String, Location> playerLocations;

	private ArrayList<String> playerNames;

	private boolean started;


	public DacGame() {
		this.playerColors = new HashMap<>();
		this.playerLocations = new HashMap<>();
		this.playerNames = new ArrayList<>();
		this.started = false;
	}


	public HashMap<String, Material> getPlayerColors() {
		return this.playerColors;
	}

	public void setPlayerColors(HashMap<String, Material> playerColors) {
		this.playerColors = playerColors;
	}

	public void addPlayerColor(String playerName, String color) {
		if (!this.playerColors.containsKey(playerName)) {
			this.playerColors.put(playerName, Colors.convertColorToMaterial(color));
		}
	}

	public void removePlayerColor(String playerName) {
		this.playerColors.remove(playerName);
	}


	public HashMap<String, Location> getPlayerLocations() {
		return this.playerLocations;
	}

	public void setPlayerLocations(HashMap<String, Location> playerLocations) {
		this.playerLocations = playerLocations;
	}

	public void addPlayerLocation(String playerName, Location location) {
		if (!this.playerLocations.containsKey(playerName)) {
			this.playerLocations.put(playerName, location);
		}
	}

	public void removePlayerLocation(String playerName) {
		this.playerLocations.remove(playerName);
	}


	public ArrayList<String> getPlayerNames() {
		return this.playerNames;
	}

	public void setPlayerNames(ArrayList<String> playerNames) {
		this.playerNames = playerNames;
	}

	public void addPlayerName(String playerName) {
		if (!this.playerNames.contains(playerName)) {
			this.playerNames.add(playerName);
		}
	}

	public void removePlayerName(String playerName) {
		this.playerNames.remove(playerName);
	}

	public void randomizePlayerOrder() {
		Collections.shuffle(this.playerNames, new Random());
	}


	public boolean isStarted() {
		return this.started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}
}
