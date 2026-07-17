package fr.ethilvan.dac.game;

import com.sk89q.worldedit.math.BlockVector3;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.tools.DacColor;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;


public class DacGame {

	private final DAC dac;

	private final String name;

	private HashMap<UUID, DacColor> playerDacColors;
	private HashMap<UUID, Location> playerLocations;
	private ArrayList<UUID> playerUuids;
	private ArrayList<UUID> currentPlayerUuids;
	private ArrayList<UUID> eliminatedPlayerUuids;

	private UUID currentPlayerUuid;

	private boolean started;

	private boolean jumpOver;

	private Location divingLocation;

	private boolean suddenDeath;

	private BlockVector3 suddenDeathDacLocation;


	public DacGame(DAC dac, String dacName) {
		this.dac = dac;
		this.name = dacName;
		this.playerDacColors = new HashMap<>();
		this.playerLocations = new HashMap<>();
		this.playerUuids = new ArrayList<>();
		this.currentPlayerUuids = new ArrayList<>();
		this.eliminatedPlayerUuids = new ArrayList<>();
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


	public HashMap<UUID, DacColor> getPlayerDacColors() {
		return this.playerDacColors;
	}

	public void setPlayerDacColors(HashMap<UUID, DacColor> playerDacColors) {
		this.playerDacColors = playerDacColors;
	}

	public void addPlayerDacColor(UUID playerUuid, DacColor dacColor) {
		if (!this.playerDacColors.containsKey(playerUuid)) {
			this.playerDacColors.put(playerUuid, dacColor);
		}
	}

	public void removePlayerDacColor(UUID playerUuid) {
		this.playerDacColors.remove(playerUuid);
	}


	public HashMap<UUID, Location> getPlayerLocations() {
		return this.playerLocations;
	}

	public void setPlayerLocations(HashMap<UUID, Location> playerLocations) {
		this.playerLocations = playerLocations;
	}

	public void addPlayerLocation(UUID playerUuid, Location location) {
		if (!this.playerLocations.containsKey(playerUuid)) {
			this.playerLocations.put(playerUuid, location);
		}
	}

	public void removePlayerLocation(UUID playerUuid) {
		this.playerLocations.remove(playerUuid);
	}


	public ArrayList<UUID> getPlayerUuids() {
		return this.playerUuids;
	}

	public void setPlayerUuids(ArrayList<UUID> playerUuids) {
		this.playerUuids = playerUuids;
	}

	public void addPlayerUuid(UUID playerUuid) {
		if (!this.playerUuids.contains(playerUuid)) {
			this.playerUuids.add(playerUuid);
		}
	}

	public void removePlayerUuid(UUID playerUuid) {
		this.playerUuids.remove(playerUuid);
	}

	public void randomizePlayerOrder() {
		Collections.shuffle(this.playerUuids, new Random());
	}


	public ArrayList<UUID> getCurrentPlayerUuids() {
		return currentPlayerUuids;
	}

	public void setCurrentPlayerUuids(ArrayList<UUID> currentPlayerUuids) {
		this.currentPlayerUuids = currentPlayerUuids;
	}

	public void addCurrentPlayerUuid(UUID playerUuid) {
		if (!this.currentPlayerUuids.contains(playerUuid)) {
			this.currentPlayerUuids.add(playerUuid);
		}
	}

	public void removeCurrentPlayerUuid(UUID playerUuid) {
		this.currentPlayerUuids.remove(playerUuid);
	}


	public UUID getCurrentPlayerUuid() {
		return this.currentPlayerUuid;
	}

	public void setCurrentPlayerUuid(UUID currentPlayerUuid) {
		this.currentPlayerUuid = currentPlayerUuid;
	}


	public ArrayList<UUID> getEliminatedPlayerUuids() {
		return this.eliminatedPlayerUuids;
	}

	public void setEliminatedPlayerUuids(ArrayList<UUID> eliminatedPlayerUuids) {
		this.eliminatedPlayerUuids = eliminatedPlayerUuids;
	}

	public void addEliminatedPlayerUuid(UUID playerUuid) {
		if (!this.eliminatedPlayerUuids.contains(playerUuid)) {
			this.eliminatedPlayerUuids.add(playerUuid);
		}
	}

	public void removeEliminatedPlayerUuid(UUID playerUuid) {
		this.eliminatedPlayerUuids.remove(playerUuid);
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


	public void  messageAllButOnePlayer(Player player, TextComponent text) {
		for (UUID playerUuid : this.getPlayerUuids()) {
			if (player.getUniqueId().equals(playerUuid)) {
				continue;
			}
			Player playerInLoop = Bukkit.getPlayer(playerUuid);
			if (playerInLoop == null) {
				continue;
			}
			playerInLoop.sendMessage(text);
		}
	}


	public ArrayList<Player> getPlayers(ArrayList<UUID> playerUuids) {
		ArrayList<Player> players = new ArrayList<>();

		for (UUID currentPlayerUuid : playerUuids) {
			Player currentPlayer = Bukkit.getPlayer(currentPlayerUuid);
			if (currentPlayer == null) {
				continue;
			}
			players.add(Bukkit.getPlayer(currentPlayerUuid));
		}

		return players;
	}


	public ArrayList<Player> getAllPlayersButOne(ArrayList<UUID> playerUuids, UUID playerUuid) {
		ArrayList<Player> players = new ArrayList<>();

		for (UUID currentPlayerUuid : playerUuids) {
			if (currentPlayerUuid.equals(playerUuid)) {
				continue;
			}
			Player currentPlayer = Bukkit.getPlayer(currentPlayerUuid);
			if (currentPlayer == null) {
				continue;
			}
			players.add(Bukkit.getPlayer(currentPlayerUuid));
		}

		return players;
	}
}
