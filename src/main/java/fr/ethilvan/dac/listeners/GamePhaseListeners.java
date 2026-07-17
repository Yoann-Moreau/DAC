package fr.ethilvan.dac.listeners;

import com.sk89q.worldedit.regions.Region;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.events.DacGameTurnEvent;
import fr.ethilvan.dac.events.GameStartEvent;
import fr.ethilvan.dac.events.PlayerTurnEvent;
import fr.ethilvan.dac.game.DacGame;
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
			UUID winnerUuid = dacGame.getCurrentPlayerUuids().getFirst();
			Player winner = Bukkit.getPlayer(winnerUuid);
			if (winner == null) {
				return;
			}
			String winnerName = winner.getName();
			MessageManagement.messageToPlayers(dac, players, "messages.gamePhases.gameOver");
			HashMap<String, String> placeholders = new HashMap<>();
			placeholders.put("\\{player-name}", winnerName);
			placeholders.put("\\{player-color}", dacGame.getPlayerDacColors().get(winnerUuid).name().toLowerCase());
			MessageManagement.messageToPlayers(dac, players, "messages.gamePhases.winner", placeholders);
			dacGame.setStarted(false);
			dacGame.setPlayerDacColors(null);
			dacGame.setPlayerLocations(null);
			dacGame.setPlayerUuids(null);
			dacGame.setCurrentPlayerUuids(null);
			dacGame.getDac().removeGame(dacGame.getName());
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

		Player player = e.getPlayer();
		if (player == null) {
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

		dacGame.setCurrentPlayerUuid(e.getPlayer().getUniqueId());

		HashMap<String, String> placeholders = new HashMap<>();
		placeholders.put("\\{player-name}", player.getName());
		placeholders.put("\\{player-color}", dacGame.getPlayerDacColors().get(player.getUniqueId()).name().toLowerCase());

		ArrayList<Player> players = dacGame.getAllPlayersButOne(dacGame.getPlayerUuids(), player.getUniqueId());
		MessageManagement.messageToPlayers(
				dacGame.getDac(),
				players,
				"messages.gamePhases.playerTurn",
				placeholders
		);

		MessageManagement.messageToPlayer(dacGame.getDac(), player, "messages.gamePhases.yourTurn");
		e.getPlayer().teleport(dacGame.getDivingLocation());
	}
}
