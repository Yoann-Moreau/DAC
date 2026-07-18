package fr.ethilvan.dac.listeners;

import com.sk89q.worldedit.regions.Region;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.events.*;
import fr.ethilvan.dac.game.DacGame;
import fr.ethilvan.dac.game.EliminationCause;
import fr.ethilvan.dac.tools.MessageManagement;
import fr.ethilvan.dac.tools.PoolManagement;
import fr.ethilvan.dac.tools.RegionManagement;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class GamePhaseListeners implements Listener {

	@EventHandler
	public void onGameStart(GameStartEvent e) {
		Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(e.getDacGame(), false));
	}


	@EventHandler
	public void onDacGameTurn(DacGameTurnEvent e) {
		// Reset list of eliminated players
		e.getDacGame().setEliminatedPlayerUuids(new ArrayList<>());

		// End game if no players left
		if (e.getDacGame().getCurrentPlayerUuids().isEmpty()) {
			return;
		}

		UUID currentPlayerUuid = e.getDacGame().getCurrentPlayerUuids().getFirst();
		Player currentPlayer = Bukkit.getPlayer(currentPlayerUuid);
		if (currentPlayer == null) {
			return;
		}

		DacGame dacGame = e.getDacGame();
		DAC dac = dacGame.getDac();
		ArrayList<Player> players = dacGame.getPlayers(dacGame.getPlayerUuids());

		if (e.isPoolFilled()) {
			if (!dacGame.isSuddenDeath()) {
				MessageManagement.messageToPlayers(dac, players, "messages.gamePhases.suddenDeath");
				dacGame.setSuddenDeath(true);
			}
			ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." +
					dacGame.getName());
			if (config == null) {
				MessageManagement.messageToPlayer(dac, currentPlayer, "messages.gamePhases.dacRegionsRetrieve");
				return;
			}
			String poolRegionName = config.getString("pool");
			if (poolRegionName == null) {
				MessageManagement.messageToPlayer(dac, currentPlayer, "messages.gamePhases.poolNameRetrieve");
				return;
			}
			String worldName = config.getString("world");
			if (worldName == null) {
				MessageManagement.messageToPlayer(dac, currentPlayer, "messages.gamePhases.worldNameRetrieve");
				return;
			}
			Region poolRegion = RegionManagement.getExistingRegion(worldName, poolRegionName);
			if (poolRegion == null) {
				MessageManagement.messageToPlayer(dac, currentPlayer, "messages.gamePhases.poolRetrieve");
				return;
			}
			dacGame.setSuddenDeathDacLocation(PoolManagement.getRandomBlockInPool(poolRegion));
		}

		if (dacGame.getCurrentPlayerUuids().size() == 1 && dacGame.getPlayerUuids().size() > 1) {
			Bukkit.getPluginManager().callEvent(new GameEndEvent(dacGame));
			return;
		}

		MessageManagement.messageToPlayers(dac, players, "messages.gamePhases.newDacTurn");
		Bukkit.getPluginManager().callEvent(
				new PlayerTurnEvent(dacGame, dacGame.getCurrentPlayerUuids().getFirst())
		);
	}


	@EventHandler
	public void onPlayerTurn(PlayerTurnEvent e) {
		DacGame dacGame = e.getDacGame();
		dacGame.setJumpOver(false);

		UUID playerUuid = e.getPlayerUuid();
		if (playerUuid == null) {
			return;
		}

		Player player = Bukkit.getPlayer(playerUuid);
		if (player == null || !player.isOnline()) {
			// Eliminate disconnected player
			Bukkit.getPluginManager().callEvent(
					new EliminatedPlayerEvent(dacGame, playerUuid, EliminationCause.DISCONNECTION)
			);
			return;
		}

		if (dacGame.isSuddenDeath()) {
			String message = PoolManagement.dacPattern(
					dacGame.getDac(),
					dacGame.getName(),
					dacGame.getSuddenDeathDacLocation().x(),
					dacGame.getSuddenDeathDacLocation().z()
			);
			if (message != null) {
				player.sendRichMessage(message);
			}
		}

		dacGame.setCurrentPlayerUuid(e.getPlayerUuid());

		HashMap<String, String> placeholders = new HashMap<>();
		placeholders.put("\\{player-name}", player.getName());
		placeholders.put("\\{player-color}", dacGame.getPlayerDacColors().get(playerUuid).name().toLowerCase());

		ArrayList<Player> players = dacGame.getAllPlayersButOne(dacGame.getPlayerUuids(), playerUuid);
		MessageManagement.messageToPlayers(
				dacGame.getDac(),
				players,
				"messages.gamePhases.playerTurn",
				placeholders
		);

		MessageManagement.messageToPlayer(dacGame.getDac(), player, "messages.gamePhases.yourTurn");
		player.teleport(dacGame.getDivingLocation());
	}


	@EventHandler
	public void onPlayerEliminated(EliminatedPlayerEvent e) {
		DacGame dacGame = e.getDacGame();
		DAC dac = dacGame.getDac();
		UUID playerUuid = e.getPlayerUuid();
		EliminationCause cause = e.getCause();
		Player player = Bukkit.getPlayer(playerUuid);

		boolean onlyOnePlayer = dacGame.getPlayerUuids().size() == 1;

		dacGame.addEliminatedPlayerUuid(playerUuid);

		ArrayList<Player> playersToNotify = dacGame.getAllPlayersButOne(dacGame.getPlayerUuids(), playerUuid);
		HashMap<String, String> placeholders = new HashMap<>();
		placeholders.put("\\{player-name}", Bukkit.getOfflinePlayer(playerUuid).getName());
		placeholders.put("\\{player-color}", dacGame.getPlayerDacColors().get(playerUuid).name().toLowerCase());

		if (cause == EliminationCause.DISCONNECTION) {
			MessageManagement.messageToPlayers(
					dacGame.getDac(),
					playersToNotify,
					"messages.gamePhases.dcedPlayerEliminated",
					placeholders
			);
			if (onlyOnePlayer) {
				dacGame.endGame();
				return;
			}
		}
		else if (cause == EliminationCause.FALL_DAMAGE) {
			MessageManagement.messageToPlayer(dac, player, "messages.gamePhases.youAreEliminated");
			MessageManagement.messageToPlayers(
					dacGame.getDac(),
					playersToNotify,
					"messages.gamePhases.fallenPlayerEliminated",
					placeholders
			);

			if (player == null || !player.isOnline()) {
				return;
			}
		}

		int currentPlayerIndex = dacGame.getCurrentPlayerUuids().indexOf(playerUuid);
		int nextIndex = currentPlayerIndex + 1;

		if (nextIndex >= dacGame.getCurrentPlayerUuids().size()) {

			if (onlyOnePlayer && cause == EliminationCause.FALL_DAMAGE) {
				// Call next DAC turn (only one player)
				dacGame.setEliminatedPlayerUuids(new ArrayList<>());
				Bukkit.getScheduler().scheduleSyncDelayedTask(dac, () -> {
					player.teleport(dacGame.getPlayerLocations().get(player.getUniqueId()));
					Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(dacGame, false));
				}, 10L);
				return;
			}

			ArrayList<UUID> currentPlayerUuids = dacGame.getCurrentPlayerUuids();
			ArrayList<UUID> eliminatedPlayerUuids = dacGame.getEliminatedPlayerUuids();

			// Try again if all players eliminated in the same turn
			if (currentPlayerUuids.size() == eliminatedPlayerUuids.size() && currentPlayerUuids.size() > 1) {

				MessageManagement.messageToPlayers(
						dac,
						dacGame.getPlayers(dacGame.getPlayerUuids()),
						"messages.gamePhases.tryAgain"
				);

				// Launch next turn with every eliminated players
				dacGame.setEliminatedPlayerUuids(new ArrayList<>());
				if (returnWhenCallingNextGameTurn(dacGame, cause, player)) return;
			}

			// Remove eliminated players
			for (UUID eliminatedPlayerUuid : dacGame.getEliminatedPlayerUuids()) {
				dacGame.removeCurrentPlayerUuid(eliminatedPlayerUuid);
			}

			// Launch next turn without eliminated players
			if (returnWhenCallingNextGameTurn(dacGame, cause, player)) return;
		}

		UUID nextPlayerUuid = dacGame.getCurrentPlayerUuids().get(nextIndex);
		if (cause == EliminationCause.FALL_DAMAGE) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(dac, () -> {
				player.teleport(dacGame.getPlayerLocations().get(player.getUniqueId()));
				Bukkit.getPluginManager().callEvent(new PlayerTurnEvent(dacGame, nextPlayerUuid));
			}, 10L);
		}
		else if (cause == EliminationCause.DISCONNECTION) {
			Bukkit.getPluginManager().callEvent(new PlayerTurnEvent(dacGame, nextPlayerUuid));
		}
	}


	@EventHandler
	public void onGameEnd(GameEndEvent event) {
		DacGame dacGame = event.getDacGame();
		DAC dac = dacGame.getDac();

		if (dacGame.getCurrentPlayerUuids().isEmpty()) {
			dacGame.endGame();
			return;
		}

		ArrayList<Player> players = dacGame.getPlayers(dacGame.getPlayerUuids());
		UUID winnerUuid = dacGame.getCurrentPlayerUuids().getFirst();

		String winnerName = Bukkit.getOfflinePlayer(winnerUuid).getName();
		MessageManagement.messageToPlayers(dac, players, "messages.gamePhases.gameOver");
		HashMap<String, String> placeholders = new HashMap<>();
		placeholders.put("\\{player-name}", winnerName);
		placeholders.put("\\{player-color}", dacGame.getPlayerDacColors().get(winnerUuid).name().toLowerCase());
		MessageManagement.messageToPlayers(dac, players, "messages.gamePhases.winner", placeholders);
		dacGame.endGame();
	}


	private boolean returnWhenCallingNextGameTurn(
			DacGame dacGame,
			EliminationCause cause,
			Player player
	) {
		if (cause == EliminationCause.FALL_DAMAGE) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(dacGame.getDac(), () -> {
				player.teleport(dacGame.getPlayerLocations().get(player.getUniqueId()));
				Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(dacGame, false));
			}, 10L);
			return true;
		}
		else if (cause == EliminationCause.DISCONNECTION) {
			Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(dacGame, false));
			return true;
		}
		return false;
	}
}
