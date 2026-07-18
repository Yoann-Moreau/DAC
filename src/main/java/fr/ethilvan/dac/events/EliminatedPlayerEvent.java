package fr.ethilvan.dac.events;

import fr.ethilvan.dac.game.DacGame;
import fr.ethilvan.dac.game.EliminationCause;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class EliminatedPlayerEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final DacGame dacGame;

	private final UUID playerUuid;

	private final EliminationCause cause;


	public EliminatedPlayerEvent(DacGame dacGame, UUID playerUuid, EliminationCause cause) {
		this.dacGame = dacGame;
		this.playerUuid = playerUuid;
		this.cause = cause;
	}


	public DacGame getDacGame() {
		return this.dacGame;
	}


	public UUID getPlayerUuid() {
		return this.playerUuid;
	}


	public EliminationCause getCause() {
		return this.cause;
	}


	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
