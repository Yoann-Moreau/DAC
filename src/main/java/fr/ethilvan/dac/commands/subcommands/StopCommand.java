package fr.ethilvan.dac.commands.subcommands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.commands.Subcommand;
import fr.ethilvan.dac.game.DacGame;
import fr.ethilvan.dac.tools.PoolManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;

public class StopCommand extends Subcommand {
	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public String getDescription() {
		return "Stops the DAC game currently in progress if there is one.";
	}

	@Override
	public String getSyntax() {
		return "/dac stop [dacName]";
	}

	@Override
	public String getPermission() {
		return "dac.stop";
	}

	@Override
	public void perform(DAC dac, CommandSender commandSender, String[] args) {

		if (!this.hasPermission(commandSender)) {
			return;
		}

		Player player = Bukkit.getPlayer(commandSender.getName());
		if (player == null && args.length < 2) {
			dac.getLogger().warning("This command requires a DAC name when not used by a player.");
			return;
		}

		String dacName = "";
		if (args.length >= 2) {
			dacName = args[1];
		}

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions");
		if (config == null) {
			if (player == null) {
				dac.getLogger().warning("There are no defined DAC regions.");
				return;
			}
			player.sendMessage(Component.text("There are no defined DAC regions.", NamedTextColor.RED));
			return;
		}

		if (player == null || !dacName.isEmpty()) {
			this.stopGame(dac, player, dacName);
			return;
		}

		stopGame(dac,player);
	}


	private void stopGame(DAC dac, Player player, String dacName) {

		if (!dac.getGames().containsKey(dacName)) {
			if (player == null) {
				dac.getLogger().warning("No DAC games exists in this region.");
				return;
			}
			player.sendMessage(Component.text("No DAC games exists in this region.", NamedTextColor.RED));
			return;
		}

		DacGame dacGame = dac.getGames().get(dacName);
		if (!dacGame.isStarted()) {
			if (player == null) {
				dac.getLogger().warning("No DAC game has been started in this region.");
				return;
			}
			player.sendMessage(Component.text("No DAC game has been started in this region.", NamedTextColor.RED));
			return;
		}


		if (!this.resetAndStopGame(dac, dacGame, dacName, player)) {
			return;
		}
		if (player == null) {
			dac.getLogger().info("The game in the " + dacName + " region has been stopped.");
			return;
		}
		player.sendMessage(Component.text("The game in the " + dacName + " region has been stopped.",
				NamedTextColor.GREEN));
	}


	private void stopGame(DAC dac, Player player) {

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions");
		if (config == null) {
			player.sendMessage(Component.text("There are no defined DAC regions.", NamedTextColor.RED));
			return;
		}

		Location wgLocation = BukkitAdapter.adapt(player.getLocation());
		com.sk89q.worldedit.world.World wgWorld = BukkitAdapter.adapt(player.getWorld());

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionsManager = container.get(wgWorld);

		if (regionsManager == null) {
			player.sendMessage(Component.text("Error when retrieving world regions.", NamedTextColor.RED));
			return;
		}

		Set<String> dacNames = config.getKeys(false);
		for (String dacName : dacNames) {
			String regionName = (String) config.get(dacName + ".base");

			if (regionName == null) { // Skip iteration if the base region is not defined
				continue;
			}

			ProtectedRegion region = regionsManager.getRegion(regionName);
			RegionQuery query = container.createQuery();
			ApplicableRegionSet set = query.getApplicableRegions(wgLocation);

			for (ProtectedRegion item : set) {
				if (item.equals(region)) {

					if (!dac.getGames().containsKey(dacName)) {
						player.sendMessage(Component.text("No DAC games exists in this region.", NamedTextColor.RED));
						return;
					}

					DacGame dacGame = dac.getGames().get(dacName);
					if (!this.resetAndStopGame(dac, dacGame, dacName, player)) {
						return;
					}
					player.sendMessage(Component.text("The game in the " + dacName + " region has been stopped.",
							NamedTextColor.GREEN));
					return;
				}
			}
		}

		player.sendMessage(Component.text("You are not in a DAC region.", NamedTextColor.RED));
	}


	private boolean resetAndStopGame(DAC dac, DacGame dacGame, String dacName, Player player) {

		String message = PoolManagement.water(dac, dacName);
		if (message != null) {
			player.sendMessage(Component.text(message, NamedTextColor.RED));
			return false;
		}

		dacGame.messageAllButOnePlayer(player, Component.text("The DAC game has been stopped", NamedTextColor.RED));

		dacGame.setStarted(false);
		dacGame.setPlayerMaterials(null);
		dacGame.setPlayerLocations(null);
		dacGame.setPlayerNames(null);
		dacGame.setCurrentPlayerNames(null);
		dac.removeGame(dacName);

		return true;
	}


	@Override
	public ArrayList<String> getAutoCompleteChoices(DAC dac) {
		return new ArrayList<>(dac.getGames().keySet());
	}
}
