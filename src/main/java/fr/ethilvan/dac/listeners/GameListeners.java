package fr.ethilvan.dac.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.events.DacGameTurnEvent;
import fr.ethilvan.dac.events.PlayerTurnEvent;
import fr.ethilvan.dac.game.DacGame;
import fr.ethilvan.dac.tools.RegionManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public class GameListeners implements Listener {

	private final DAC dac;


	public GameListeners(DAC dac) {
		this.dac = dac;
	}


	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {

		Player player = e.getPlayer();
		String playerName = player.getName();

		for (String dacName : this.dac.getGames().keySet()) {
			ConfigurationSection config = this.dac.getConfig().getConfigurationSection("regions." + dacName);
			assert config != null;
			String poolRegionName = config.getString("pool");
			assert poolRegionName != null;
			DacGame dacGame = this.dac.getGames().get(dacName);

			if (dacGame.getCurrentPlayerName() == null) {
				continue;
			}

			if (!dacGame.getCurrentPlayerName().equals(playerName)) {
				continue;
			}

			Region region = RegionManagement.getExistingRegion(player, poolRegionName);

			if (region == null) {
				Bukkit.getLogger().severe("Error when retrieving pool region.");
				return;
			}

			BlockVector3 blockVector3 = BukkitAdapter.asBlockVector(player.getLocation());

			if (region.contains(blockVector3) && !dacGame.isJumpOver()) {

				dacGame.setJumpOver(true);

				int x = blockVector3.getBlockX();
				int z = blockVector3.getBlockZ();

				int currentPlayerIndex = dacGame.getCurrentPlayerNames().indexOf(playerName);
				int nextIndex = currentPlayerIndex + 1;

				// Launch next dac turn
				if (nextIndex >= dacGame.getCurrentPlayerNames().size()) {

					// Remove eliminated players
					for (String eliminatedPlayerName : dacGame.getEliminatedPlayerNames()) {
						dacGame.removeCurrentPlayerName(eliminatedPlayerName);
					}

					Bukkit.getScheduler().scheduleSyncDelayedTask(this.dac, () -> {
						this.placePoolPillar(dacGame, region, player, x, z);
						player.teleport(dacGame.getPlayerLocations().get(player.getName()));
						Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(dacGame));
					}, 10L);
					return;
				}

				// Launch next player's turn
				String nextPlayerName = dacGame.getCurrentPlayerNames().get(nextIndex);
				Bukkit.getScheduler().scheduleSyncDelayedTask(this.dac, () -> {
					this.placePoolPillar(dacGame, region, player, x, z);
					player.teleport(dacGame.getPlayerLocations().get(player.getName()));
					Bukkit.getPluginManager().callEvent(new PlayerTurnEvent(dacGame, nextPlayerName));
				}, 10L);
			}
		}
	}


	private void placePoolPillar(DacGame dacGame, Region region, Player player, int x, int z) {
		int minY = region.getMinimumPoint().getBlockY();
		int maxY = region.getMaximumPoint().getBlockY();
		for (int y = minY; y <= maxY; y++) {

			if (y == maxY) {
				if (!player.getWorld().getBlockAt(x - 1, y, z).getType().equals(Material.WATER) &&
						!player.getWorld().getBlockAt(x + 1, y, z).getType().equals(Material.WATER) &&
						!player.getWorld().getBlockAt(x, y, z - 1).getType().equals(Material.WATER) &&
						!player.getWorld().getBlockAt(x, y, z + 1).getType().equals(Material.WATER)) {
					player.getWorld().getBlockAt(x, y, z).setType(Material.GLASS);
				}
				else {
					player.getWorld().getBlockAt(x, y, z).setType(dacGame.getPlayerColors().get(player.getName()));
				}
			}
			else {
				player.getWorld().getBlockAt(x, y, z).setType(dacGame.getPlayerColors().get(player.getName()));
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

			String currentPlayerName = dacGame.getCurrentPlayerName();
			if (currentPlayerName == null) {
				continue;
			}

			if (!player.getName().equals(currentPlayerName)) {
				continue;
			}

			e.setCancelled(true);
			dacGame.setJumpOver(true);
			dacGame.addEliminatedPlayerName(currentPlayerName);

			player.sendMessage(Component.text("You have been eliminated.", NamedTextColor.RED));
			dacGame.messageAllButOnePlayer(player,
					Component.text(player.getName() + " has been eliminated.", NamedTextColor.WHITE)
			);

			int currentPlayerIndex = dacGame.getCurrentPlayerNames().indexOf(currentPlayerName);
			int nextIndex = currentPlayerIndex + 1;

			if (nextIndex >= dacGame.getCurrentPlayerNames().size()) {

				if (dacGame.getPlayerNames().size() == 1) {
					dacGame.setEliminatedPlayerNames(new ArrayList<>());
					Bukkit.getScheduler().scheduleSyncDelayedTask(this.dac, () -> {
						player.teleport(dacGame.getPlayerLocations().get(player.getName()));
						Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(dacGame));
					}, 10L);
					return;
				}

				ArrayList<String> currentPlayerNames = dacGame.getCurrentPlayerNames();
				ArrayList<String> eliminatedPlayerNames = dacGame.getEliminatedPlayerNames();

				if (currentPlayerNames.size() == eliminatedPlayerNames.size() && currentPlayerNames.size() > 1) {

					dacGame.messageAllPlayers(
							Component.text("All remaining players have been eliminated this turn, let's try again.",
							NamedTextColor.GREEN)
					);

					// Launch next turn with every eliminated players
					dacGame.setEliminatedPlayerNames(new ArrayList<>());
					Bukkit.getScheduler().scheduleSyncDelayedTask(this.dac, () -> {
						player.teleport(dacGame.getPlayerLocations().get(player.getName()));
						Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(dacGame));
					}, 10L);
					return;
				}

				// Remove eliminated players
				for (String playerName : dacGame.getEliminatedPlayerNames()) {
					dacGame.removeCurrentPlayerName(playerName);
				}

				// Launch next turn without eliminated players
				Bukkit.getScheduler().scheduleSyncDelayedTask(this.dac, () -> {
					player.teleport(dacGame.getPlayerLocations().get(player.getName()));
					Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(dacGame));
				}, 10L);
				return;
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(this.dac, () -> {
				player.teleport(dacGame.getPlayerLocations().get(player.getName()));
				String nextPlayerName = dacGame.getCurrentPlayerNames().get(nextIndex);
				Bukkit.getPluginManager().callEvent(new PlayerTurnEvent(dacGame, nextPlayerName));
			}, 10L);
		}
	}
}
