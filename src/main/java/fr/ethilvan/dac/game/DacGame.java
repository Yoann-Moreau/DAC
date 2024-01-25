package fr.ethilvan.dac.game;

import com.sk89q.worldedit.math.BlockVector3;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.tools.Colors;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class DacGame {

	private final DAC dac;

	private final String name;

	private HashMap<String, Material> playerMaterials;
	private HashMap<String, Location> playerLocations;
	private ArrayList<String> playerNames;
	private ArrayList<String> currentPlayerNames;
	private ArrayList<String> eliminatedPlayerNames;

	private String currentPlayerName;

	private boolean started;

	private boolean jumpOver;

	private Location divingLocation;

	private boolean suddenDeath;

	private BlockVector3 suddenDeathDacLocation;


	public DacGame(DAC dac, String dacName) {
		this.dac = dac;
		this.name = dacName;
		this.playerMaterials = new HashMap<>();
		this.playerLocations = new HashMap<>();
		this.playerNames = new ArrayList<>();
		this.currentPlayerNames = new ArrayList<>();
		this.eliminatedPlayerNames = new ArrayList<>();
		this.started = false;
		this.jumpOver = false;
		this.suddenDeath = false;
	}


	public DAC getDac() {
		return this.dac;
	}

	public String getName() {
		return this.name;
	}


	public HashMap<String, Material> getPlayerMaterials() {
		return this.playerMaterials;
	}

	public void setPlayerMaterials(HashMap<String, Material> playerMaterials) {
		this.playerMaterials = playerMaterials;
	}

	public void addPlayerMaterial(String playerName, String color) {
		if (!this.playerMaterials.containsKey(playerName)) {
			this.playerMaterials.put(playerName, Colors.convertColorToMaterial(color));
		}
	}

	public void removePlayerMaterial(String playerName) {
		this.playerMaterials.remove(playerName);
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


	public ArrayList<String> getEliminatedPlayerNames() {
		return this.eliminatedPlayerNames;
	}

	public void setEliminatedPlayerNames(ArrayList<String> eliminatedPlayerNames) {
		this.eliminatedPlayerNames = eliminatedPlayerNames;
	}

	public void addEliminatedPlayerName(String playerName) {
		if (!this.eliminatedPlayerNames.contains(playerName)) {
			this.eliminatedPlayerNames.add(playerName);
		}
	}

	public void removeEliminatedPlayerName(String playerName) {
		this.eliminatedPlayerNames.remove(playerName);
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

	public void setDivingLocation(Location divingLocation) {
		this.divingLocation = divingLocation;
	}


	public boolean isSuddenDeath() {
		return suddenDeath;
	}

	public void setSuddenDeath(boolean suddenDeath) {
		this.suddenDeath = suddenDeath;
	}


	public BlockVector3 getSuddenDeathDacLocation() {
		return suddenDeathDacLocation;
	}

	public void setSuddenDeathDacLocation(BlockVector3 suddenDeathDacLocation) {
		this.suddenDeathDacLocation = suddenDeathDacLocation;
	}

	public void messageAllPlayers(TextComponent text) {
		for (String playerName : this.getPlayerNames()) {
			Player playerInLoop = Bukkit.getPlayer(playerName);
			if (playerInLoop == null) {
				continue;
			}
			playerInLoop.sendMessage(text);
		}
	}


	public void  messageAllButOnePlayer(Player player, TextComponent text) {
		for (String playerName : this.getPlayerNames()) {
			if (player.getName().equals(playerName)) {
				continue;
			}
			Player playerInLoop = Bukkit.getPlayer(playerName);
			if (playerInLoop == null) {
				continue;
			}
			playerInLoop.sendMessage(text);
		}
	}
}
