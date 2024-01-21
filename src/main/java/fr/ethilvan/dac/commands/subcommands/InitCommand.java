package fr.ethilvan.dac.commands.subcommands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.commands.Subcommand;
import fr.ethilvan.dac.game.DacGame;
import fr.ethilvan.dac.tools.RegionManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;

public class InitCommand extends Subcommand {
	@Override
	public String getName() {
		return "init";
	}

	@Override
	public String getDescription() {
		return "Initialize a DAC game";
	}

	@Override
	public String getSyntax() {
		return "/dac init";
	}

	@Override
	public String getPermission() {
		return "dac.init";
	}

	@Override
	public void perform(DAC dac, CommandSender commandSender, String[] args) {

		if (!this.hasPermission(commandSender)) {
			return;
		}

		Player player = Bukkit.getPlayer(commandSender.getName());
		if (player == null) {
			Bukkit.getLogger().warning("This command must be used by a player.");
			return;
		}

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions");
		if (config == null) {
			player.sendMessage(Component.text("There are no defined DAC regions.", NamedTextColor.RED));
			return;
		}

		Location wgLocation = BukkitAdapter.adapt(player.getLocation());
		World wgWorld = BukkitAdapter.adapt(player.getWorld());

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
					createDacGame(dac, player, dacName, region);
					return;
				}
			}
		}

		player.sendMessage(Component.text("You are not in a DAC region.", NamedTextColor.RED));
	}


	private void createDacGame(DAC dac, Player player, String dacName, ProtectedRegion region) {
		if (dac.getGames().containsKey(dacName)) {
			player.sendMessage(Component.text("A DAC game already exists in this region.", NamedTextColor.RED));
			return;
		}

		dac.addGame(dacName, new DacGame(dac, dacName));
		ArrayList<Player> playersInRegion = RegionManagement.getPlayersInWGRegion(player.getWorld(), region);
		for (Player playerInRegion : playersInRegion) {
			playerInRegion.sendMessage(Component.text("A DAC game has been created in the " + dacName + " region.",
					NamedTextColor.GREEN));
		}
	}
}
