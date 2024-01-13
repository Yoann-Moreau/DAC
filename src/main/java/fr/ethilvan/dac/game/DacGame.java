package fr.ethilvan.dac.game;

import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.tools.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class DacGame {

	private final DAC dac;

	private final String name;

	private HashMap<String, Material> playerColors;

	private HashMap<String, Location> playerLocations;

	private ArrayList<String> playerNames;

	private ArrayList<String> currentPlayerNames;

	private String currentPlayerName;

	private boolean started;

	private boolean jumpOver;

	private final Location divingLocation;


	public DacGame(DAC dac, String dacName) {
		this.dac = dac;
		this.name = dacName;
		this.playerColors = new HashMap<>();
		this.playerLocations = new HashMap<>();
		this.playerNames = new ArrayList<>();
		this.currentPlayerNames = new ArrayList<>();
		this.started = false;
		this.jumpOver = false;

		ConfigurationSection config = this.dac.getConfig().getConfigurationSection("regions." + dacName);

		assert config != null;
		String worldName = config.getString("world");
		double x = config.getDouble("diving.x");
		double y = config.getDouble("diving.y");
		double z = config.getDouble("diving.z");
		float yaw = (float) config.getDouble("diving.yaw");
		float pitch = (float) config.getDouble("diving.pitch");

		assert worldName != null;
		this.divingLocation = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
	}


	public String getName() {
		return this.name;
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


	public ArrayList<String> getCurrentPlayerNames() {
		return currentPlayerNames;
	}

	public void setCurrentPlayerNames(ArrayList<String> currentPlayerNames) {
		this.currentPlayerNames = currentPlayerNames;
	}

	public void addCurrentPlayerName(String playerName) {
		if (!this.currentPlayerNames.contains(playerName)) {
			this.currentPlayerNames.add(playerName);
		}
	}

	public void removeCurrentPlayerName(String playerName) {
		this.currentPlayerNames.remove(playerName);
	}


	public String getCurrentPlayerName() {
		return this.currentPlayerName;
	}

	public void setCurrentPlayerName(String currentPlayerName) {
		this.currentPlayerName = currentPlayerName;
	}


	public boolean isStarted() {
		return this.started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}


	public boolean isJumpOver() {
		return jumpOver;
	}

	public void setJumpOver(boolean jumpOver) {
		this.jumpOver = jumpOver;
	}


	public Location getDivingLocation() {
		return divingLocation;
	}
}
