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
		Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(e.getDacGame(), e.getDacGame().getCurrentPlayerNames()));
	}


	@EventHandler
	public void onDacGameTurn(DacGameTurnEvent e) {
		for (String playerName : e.getDacGame().getPlayerNames()) {
			Player player = Bukkit.getPlayer(playerName);
			if (player != null) {
				player.sendMessage(Component.text("A new DAC turn has begun.", NamedTextColor.GREEN));
			}
		}
		Bukkit.getPluginManager().callEvent(new PlayerTurnEvent(e.getDacGame(), e.getPlayerNames().get(0)));
	}


	@EventHandler
	public void onPlayerTurn(PlayerTurnEvent e) {
		if (e.getPlayer() == null) {
			return;
		}

		e.getPlayer().sendMessage(Component.text("It's your turn.", NamedTextColor.GOLD));
		e.getPlayer().teleport(e.getDacGame().getDivingLocation());
	}
}
