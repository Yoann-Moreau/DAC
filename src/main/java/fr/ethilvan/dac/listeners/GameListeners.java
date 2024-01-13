package fr.ethilvan.dac.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.events.DacGameTurnEvent;
import fr.ethilvan.dac.events.PlayerTurnEvent;
import fr.ethilvan.dac.game.DacGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class GameListeners implements Listener {

	private DAC dac;


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

			Location wgLocation = BukkitAdapter.adapt(player.getLocation());
			World wgWorld = BukkitAdapter.adapt(player.getWorld());

			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regionsManager = container.get(wgWorld);

			if (regionsManager == null) {
				player.sendMessage(Component.text("Error when retrieving world regions.", NamedTextColor.RED));
				return;
			}

			ProtectedRegion region = regionsManager.getRegion(poolRegionName);

			if (region == null) {
				Bukkit.getLogger().severe("Error when retrieving pool region.");
				return;
			}

			if (region.contains(wgLocation.getBlockX(), wgLocation.getBlockY(), wgLocation.getBlockZ()) &&
					!dacGame.isJumpOver()) {

				dacGame.setJumpOver(true);

				int x = wgLocation.getBlockX();
				int z = wgLocation.getBlockZ();

				int currentPlayerIndex = dacGame.getCurrentPlayerNames().indexOf(playerName);
				int nextIndex = currentPlayerIndex + 1;
				if (nextIndex >= dacGame.getCurrentPlayerNames().size()) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(this.dac, () -> {
						this.placePoolPillar(dacGame, region, player, x, z);
						Bukkit.getPluginManager().callEvent(new DacGameTurnEvent(dacGame));
					}, 10L);
					return;
				}
				String nextPlayerName = dacGame.getCurrentPlayerNames().get(nextIndex);
				Bukkit.getScheduler().scheduleSyncDelayedTask(this.dac, () -> {
					this.placePoolPillar(dacGame, region, player, x, z);
					Bukkit.getPluginManager().callEvent(new PlayerTurnEvent(dacGame, nextPlayerName));
				}, 10L);
			}
		}
	}


	private void placePoolPillar(DacGame dacGame, ProtectedRegion region, Player player, int x, int z) {
		int minY = region.getMinimumPoint().getBlockY();
		int maxY = region.getMaximumPoint().getBlockY();
		for (int y = minY; y <= maxY; y++) {
			player.getWorld().getBlockAt(x, y, z).setType(dacGame.getPlayerColors().get(player.getName()));
		}
		dacGame.setJumpOver(false);
	}
}
