package fr.ethilvan.dac.events;

import fr.ethilvan.dac.game.DacGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameStartEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final DacGame dacGame;


	public GameStartEvent(DacGame dacGame) {
		this.dacGame = dacGame;
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
}
