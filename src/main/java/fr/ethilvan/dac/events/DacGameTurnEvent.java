package fr.ethilvan.dac.events;

import fr.ethilvan.dac.game.DacGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DacGameTurnEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final DacGame dacGame;

	private final ArrayList<String> playerNames;


	public DacGameTurnEvent(DacGame dacGame, ArrayList<String> playerNames) {
		this.dacGame = dacGame;
		this.playerNames = playerNames;
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


	public ArrayList<String> getPlayerNames() {
		return this.playerNames;
	}
}
