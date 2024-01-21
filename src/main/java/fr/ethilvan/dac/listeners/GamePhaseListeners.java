package fr.ethilvan.dac.listeners;

import fr.ethilvan.dac.events.DacGameTurnEvent;
import fr.ethilvan.dac.events.GameStartEvent;
import fr.ethilvan.dac.events.PlayerTurnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GamePhaseListeners implements Listener {

	@EventHandler
	public void onGameStart(GameStartEvent e) {
		Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(e.getDacGame()));
	}


	@EventHandler
	public void onDacGameTurn(DacGameTurnEvent e) {
		// End game if no players left
		if (e.getDacGame().getCurrentPlayerNames().isEmpty()) {
			return;
		}

		if (e.getDacGame().getCurrentPlayerNames().size() == 1 && e.getDacGame().getPlayerNames().size() > 1) {
			for (String playerName : e.getDacGame().getPlayerNames()) {
				Player player = Bukkit.getPlayer(playerName);
				if (player == null) {
					continue;
				}
				player.sendMessage(Component.text("The DAC game is over.", NamedTextColor.GREEN));
				player.sendMessage(Component.text("The winner is " + e.getDacGame().getCurrentPlayerNames().get(0),
						NamedTextColor.GREEN));
			}
			e.getDacGame().setStarted(false);
			e.getDacGame().setPlayerMaterials(null);
			e.getDacGame().setPlayerLocations(null);
			e.getDacGame().setPlayerNames(null);
			e.getDacGame().getDac().removeGame(e.getDacGame().getName());
			return;
		}

		for (String playerName : e.getDacGame().getPlayerNames()) {
			Player player = Bukkit.getPlayer(playerName);
			if (player != null) {
				player.sendMessage(Component.text("A new DAC turn has begun.", NamedTextColor.GREEN));
			}
		}
		Bukkit.getPluginManager().callEvent(
				new PlayerTurnEvent(e.getDacGame(),
						e.getDacGame().getCurrentPlayerNames().get(0)
				)
		);
	}


	@EventHandler
	public void onPlayerTurn(PlayerTurnEvent e) {
		e.getDacGame().setJumpOver(false);

		if (e.getPlayer() == null) {
			return;
		}

		e.getDacGame().setCurrentPlayerName(e.getPlayer().getName());

		for (String playerName : e.getDacGame().getPlayerNames()) {
			if (e.getPlayer().getName().equals(playerName)) {
				continue;
			}
			Player player = Bukkit.getPlayer(playerName);
			if (player != null) {
				player.sendMessage(Component.text("It's " + e.getPlayer().getName() + "'s turn.", NamedTextColor.GREEN));
			}
		}

		e.getPlayer().sendMessage(Component.text("It's your turn.", NamedTextColor.GOLD));
		e.getPlayer().teleport(e.getDacGame().getDivingLocation());
	}
}
