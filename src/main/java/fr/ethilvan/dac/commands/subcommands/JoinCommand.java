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
import fr.ethilvan.dac.tools.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Set;

public class JoinCommand extends Subcommand {
	@Override
	public String getName() {
		return "join";
	}

	@Override
	public String getDescription() {
		return "Joins a DAC game with the specified color";
	}

	@Override
	public String getSyntax() {
		return "/dac join <color>";
	}

	@Override
	public void perform(DAC dac, CommandSender commandSender, String[] args) {

		Player player = Bukkit.getPlayer(commandSender.getName());
		if (player == null) {
			Bukkit.getLogger().warning("This command must be used by a player.");
			return;
		}

		if (args.length < 2) {
			player.sendMessage(Component.text("You must provide a color.", NamedTextColor.RED));
			return;
		}

		String color = args[1];

		if (!Colors.getAvailableColors().contains(color.toUpperCase())) {
			String colorsString = Colors.getChatColorsListInString();
			MiniMessage mm = MiniMessage.miniMessage();
			Component parsed = mm.deserialize("<color:red>The available colors are: " + colorsString);
			player.sendMessage(parsed);
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
					joinDacGame(dac, player, dacName, color);
					return;
				}
			}
		}

		player.sendMessage(Component.text("You are not in a DAC region.", NamedTextColor.RED));
	}


	private void joinDacGame(DAC dac, Player player, String dacName, String color) {
		if (!dac.getGames().containsKey(dacName)) {
			player.sendMessage(Component.text("No DAC games exists in this region.", NamedTextColor.RED));
			return;
		}

		DacGame dacGame = dac.getGames().get(dacName);
		if (dacGame.getPlayers().containsKey(player.getName())) {
			player.sendMessage(Component.text("You have already joined a game in this region.", NamedTextColor.RED));
			return;
		}

		dacGame.addPlayer(player, color);
		dacGame.addLocation(player, player.getLocation());
		player.sendMessage(Component.text("You have joined the DAC game in the " + dacName + " region.",
				NamedTextColor.GREEN));
	}
}
