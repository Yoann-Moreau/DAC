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


public class GamePhaseListeners implements Listener {

	@EventHandler
	public void onGameStart(GameStartEvent e) {
		Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(e.getDacGame(), false));
	}


	@EventHandler
	public void onDacGameTurn(DacGameTurnEvent e) {
		// Reset list of eliminated players
		e.getDacGame().setEliminatedPlayerNames(new ArrayList<>());

		// End game if no players left
		if (e.getDacGame().getCurrentPlayerNames().isEmpty()) {
			return;
		}

		String currentPlayerName = e.getDacGame().getCurrentPlayerNames().getFirst();
		Player currentPlayer = Bukkit.getPlayer(currentPlayerName);
		if (currentPlayer == null) {
			return;
		}

		DacGame dacGame = e.getDacGame();
		DAC dac = dacGame.getDac();
		ArrayList<Player> players = dacGame.getPlayers(dacGame.getPlayerNames());

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

		if (dacGame.getCurrentPlayerNames().size() == 1 && dacGame.getPlayerNames().size() > 1) {
			String winnerName = dacGame.getCurrentPlayerNames().getFirst();
			MessageManagement.messageToPlayers(dac, players, "messages.gamePhases.gameOver");
			HashMap<String, String> placeholders = new HashMap<>();
			placeholders.put("\\{player-name}", winnerName);
			placeholders.put("\\{player-color}", dacGame.getPlayerDacColors().get(winnerName).name().toLowerCase());
			MessageManagement.messageToPlayers(dac, players, "messages.gamePhases.winner", placeholders);
			dacGame.setStarted(false);
			dacGame.setPlayerDacColors(null);
			dacGame.setPlayerLocations(null);
			dacGame.setPlayerNames(null);
			dacGame.setCurrentPlayerNames(null);
			dacGame.getDac().removeGame(dacGame.getName());
			return;
		}

		MessageManagement.messageToPlayers(dac, players, "messages.gamePhases.newDacTurn");
		Bukkit.getPluginManager().callEvent(
				new PlayerTurnEvent(dacGame, dacGame.getCurrentPlayerNames().getFirst())
		);
	}


	@EventHandler
	public void onPlayerTurn(PlayerTurnEvent e) {
		e.getDacGame().setJumpOver(false);

		Player player = e.getPlayer();
		if (player == null) {
			return;
		}

		if (e.getDacGame().isSuddenDeath()) {
			String message = PoolManagement.dacPattern(
					e.getDacGame().getDac(),
					e.getDacGame().getName(),
					e.getDacGame().getSuddenDeathDacLocation().x(),
					e.getDacGame().getSuddenDeathDacLocation().z()
			);
			if (message != null) {
				player.sendRichMessage(message);
			}
		}

		e.getDacGame().setCurrentPlayerName(e.getPlayer().getName());

		HashMap<String, String> placeholders = new HashMap<>();
		placeholders.put("\\{player-name}", player.getName());
		placeholders.put("\\{player-color}", e.getDacGame().getPlayerDacColors().get(player.getName()).name().toLowerCase());

		ArrayList<Player> players = new ArrayList<>();
		for (String playerName : e.getDacGame().getPlayerNames()) {
			Player playerInLoop = Bukkit.getPlayer(playerName);
			if (playerInLoop == null || playerInLoop.equals(player)) {
				continue;
			}
			players.add(playerInLoop);
		}
		MessageManagement.messageToPlayers(
				e.getDacGame().getDac(),
				players,
				"messages.gamePhases.playerTurn",
				placeholders
		);

		MessageManagement.messageToPlayer(e.getDacGame().getDac(), player, "messages.gamePhases.yourTurn");
		e.getPlayer().teleport(e.getDacGame().getDivingLocation());
	}
}
