package fr.ethilvan.dac.events;

import fr.ethilvan.dac.game.DacGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerTurnEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final DacGame dacGame;

	private final Player player;


	public PlayerTurnEvent(DacGame dacGame, String playerName) {
		this.dacGame = dacGame;
		this.player = Bukkit.getPlayer(playerName);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}


	public DacGame getDacGame() {
		return this.dacGame;
	}


	public Player getPlayer() {
		return this.player;
	}
}
