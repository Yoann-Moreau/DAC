package fr.ethilvan.dac.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.events.DacGameTurnEvent;
import fr.ethilvan.dac.events.EliminatedPlayerEvent;
import fr.ethilvan.dac.events.PlayerTurnEvent;
import fr.ethilvan.dac.game.DacGame;
import fr.ethilvan.dac.game.EliminationCause;
import fr.ethilvan.dac.tools.MessageManagement;
import fr.ethilvan.dac.tools.PoolManagement;
import fr.ethilvan.dac.tools.RegionManagement;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.UUID;


public class GameListeners implements Listener {

	private final DAC dac;


	public GameListeners(DAC dac) {
		this.dac = dac;
	}


	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {

		Player player = e.getPlayer();
		UUID playerUuid = player.getUniqueId();

		for (String dacName : this.dac.getGames().keySet()) {
			ConfigurationSection config = this.dac.getConfig().getConfigurationSection("regions." + dacName);
			if (config == null) {
				player.sendMessage(Component.text("Error while retrieving DAC regions."));
				return;
			}
			String poolRegionName = config.getString("pool");
			if (poolRegionName == null) {
				player.sendMessage(Component.text("Error while retrieving pool region name."));
				return;
			}
			DacGame dacGame = this.dac.getGames().get(dacName);

			if (dacGame.getCurrentPlayerUuid() == null) {
				continue;
			}

			if (!dacGame.getCurrentPlayerUuid().equals(playerUuid)) {
				continue;
			}

			Region region = RegionManagement.getExistingRegion(player, poolRegionName);

			if (region == null) {
				dac.getLogger().severe("Error when retrieving pool region.");
				return;
			}

			BlockVector3 blockVector3 = BukkitAdapter.asBlockVector(player.getLocation());

			if (region.contains(blockVector3) && !dacGame.isJumpOver()) {

				dacGame.setJumpOver(true);

				int x = blockVector3.x();
				int z = blockVector3.z();

				ArrayList<Player> players = dacGame.getPlayers(dacGame.getPlayerUuids());

				int currentPlayerIndex = dacGame.getCurrentPlayerUuids().indexOf(playerUuid);
				int nextIndex = currentPlayerIndex + 1;

				// Launch next dac turn
				if (nextIndex >= dacGame.getCurrentPlayerUuids().size()) {

					// Remove eliminated players
					for (UUID eliminatedPlayerUuid : dacGame.getEliminatedPlayerUuids()) {
						dacGame.removeCurrentPlayerUuid(eliminatedPlayerUuid);
					}

					Bukkit.getScheduler().scheduleSyncDelayedTask(this.dac, () -> {
						this.placePoolPillar(dacGame, region, player, x, z);
						player.teleport(dacGame.getPlayerLocations().get(player.getUniqueId()));
						boolean poolFilled = PoolManagement.isPoolFilled(player.getWorld(), region);
						Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(dacGame, poolFilled));
					}, 10L);
					return;
				}

				// Launch next player's turn
				UUID nextPlayerUuid = dacGame.getCurrentPlayerUuids().get(nextIndex);
				Bukkit.getScheduler().scheduleSyncDelayedTask(this.dac, () -> {
					this.placePoolPillar(dacGame, region, player, x, z);
					player.teleport(dacGame.getPlayerLocations().get(player.getUniqueId()));
					boolean poolFilled = PoolManagement.isPoolFilled(player.getWorld(), region);
					if (poolFilled && !dacGame.isSuddenDeath()) {
						dacGame.setSuddenDeathDacLocation(PoolManagement.getRandomBlockInPool(region));
						MessageManagement.messageToPlayers(dac, players, "messages.gamePhases.suddenDeath");
						dacGame.setSuddenDeath(true);
						Bukkit.getPluginManager().callEvent(new PlayerTurnEvent(dacGame, nextPlayerUuid));
						return;
					}
					Bukkit.getPluginManager().callEvent(new PlayerTurnEvent(dacGame, nextPlayerUuid));
				}, 10L);
			}
		}
	}


	private void placePoolPillar(DacGame dacGame, Region region, Player player, int x, int z) {
		int minY = region.getMinimumPoint().y();
		int maxY = region.getMaximumPoint().y();
		for (int y = minY; y <= maxY; y++) {

			if (y == maxY) {
				if (!player.getWorld().getBlockAt(x - 1, y, z).getType().equals(Material.WATER) &&
						!player.getWorld().getBlockAt(x + 1, y, z).getType().equals(Material.WATER) &&
						!player.getWorld().getBlockAt(x, y, z - 1).getType().equals(Material.WATER) &&
						!player.getWorld().getBlockAt(x, y, z + 1).getType().equals(Material.WATER)) {
					player.getWorld().getBlockAt(x, y, z).setType(Material.GLASS);
				}
				else {
					player.getWorld().getBlockAt(x, y, z).setType(dacGame.getPlayerDacColors().get(player.getUniqueId()).getMaterial());
				}
			}
			else {
				player.getWorld().getBlockAt(x, y, z).setType(dacGame.getPlayerDacColors().get(player.getUniqueId()).getMaterial());
			}
		}
	}


	@EventHandler
	public void onPlayerFallDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player player)) {
			return;
		}

		if (e.getCause() != EntityDamageEvent.DamageCause.FALL) {
			return;
		}

		for (String dacName : this.dac.getGames().keySet()) {
			DacGame dacGame = this.dac.getGames().get(dacName);

			UUID currentPlayerUuid = dacGame.getCurrentPlayerUuid();
			if (currentPlayerUuid == null) {
				continue;
			}

			if (!player.getUniqueId().equals(currentPlayerUuid)) {
				continue;
			}

			e.setCancelled(true);
			dacGame.setJumpOver(true);

			Bukkit.getPluginManager().callEvent(
					new EliminatedPlayerEvent(dacGame, currentPlayerUuid, EliminationCause.FALL_DAMAGE)
			);
		}
	}
}
