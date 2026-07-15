package fr.ethilvan.dac.listeners;

import com.sk89q.worldedit.regions.Region;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.events.DacGameTurnEvent;
import fr.ethilvan.dac.events.GameStartEvent;
import fr.ethilvan.dac.events.PlayerTurnEvent;
import fr.ethilvan.dac.tools.PoolManagement;
import fr.ethilvan.dac.tools.RegionManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

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

		if (e.isPoolFilled()) {
			if (!e.getDacGame().isSuddenDeath()) {
				e.getDacGame().messageAllPlayers(Component.text("The pool has been filled! It's time for sudden death!",
						NamedTextColor.GOLD));
				e.getDacGame().setSuddenDeath(true);
			}
			DAC dac = e.getDacGame().getDac();
			ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." +
					e.getDacGame().getName());
			if (config == null) {
				dac.getLogger().severe("Error while retrieving DAC regions.");
				return;
			}
			String poolRegionName = config.getString("pool");
			if (poolRegionName == null) {
				dac.getLogger().severe("Error while retrieving pool region name.");
				return;
			}
			String worldName = config.getString("world");
			if (worldName == null) {
				dac.getLogger().severe("Error while retrieving world name.");
				return;
			}
			Region poolRegion = RegionManagement.getExistingRegion(worldName, poolRegionName);
			if (poolRegion == null) {
				dac.getLogger().severe("Error while retrieving pool region.");
				return;
			}
			e.getDacGame().setSuddenDeathDacLocation(PoolManagement.getRandomBlockInPool(poolRegion));
		}

		if (e.getDacGame().getCurrentPlayerNames().size() == 1 && e.getDacGame().getPlayerNames().size() > 1) {
			e.getDacGame().messageAllPlayers(Component.text("The DAC game is over.", NamedTextColor.GREEN));
			e.getDacGame().messageAllPlayers(Component.text("The winner is " +
					e.getDacGame().getCurrentPlayerNames().getFirst(), NamedTextColor.GREEN));
			e.getDacGame().setStarted(false);
			e.getDacGame().setPlayerMaterials(null);
			e.getDacGame().setPlayerLocations(null);
			e.getDacGame().setPlayerNames(null);
			e.getDacGame().setCurrentPlayerNames(null);
			e.getDacGame().getDac().removeGame(e.getDacGame().getName());
			return;
		}

		e.getDacGame().messageAllPlayers(Component.text("A new DAC turn has begun.", NamedTextColor.GREEN));
		Bukkit.getPluginManager().callEvent(
				new PlayerTurnEvent(e.getDacGame(),
						e.getDacGame().getCurrentPlayerNames().get(0)
				)
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

		e.getDacGame().messageAllButOnePlayer(e.getPlayer(),
				Component.text("It's " + e.getPlayer().getName() + "'s turn.", NamedTextColor.GREEN));

		e.getPlayer().sendMessage(Component.text("It's your turn.", NamedTextColor.GOLD));
		e.getPlayer().teleport(e.getDacGame().getDivingLocation());
	}
}
