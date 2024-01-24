package fr.ethilvan.dac.events;

import fr.ethilvan.dac.game.DacGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DacGameTurnEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final DacGame dacGame;

	private final boolean poolFilled;

	public DacGameTurnEvent(DacGame dacGame, boolean poolFilled) {
		this.dacGame = dacGame;
		this.poolFilled = poolFilled;
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

	public boolean isPoolFilled() {
		return poolFilled;
	}
}
