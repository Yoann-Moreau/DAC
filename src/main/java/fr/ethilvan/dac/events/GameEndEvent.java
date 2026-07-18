package fr.ethilvan.dac.events;

import fr.ethilvan.dac.game.DacGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


public class GameEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final DacGame dacGame;


	public GameEndEvent(DacGame dacGame) {
		this.dacGame = dacGame;
	}


	public DacGame getDacGame() {
		return dacGame;
	}


	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
