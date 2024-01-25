package fr.ethilvan.dac.commands.subcommands;

import com.sk89q.worldedit.regions.Region;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.commands.Subcommand;
import fr.ethilvan.dac.tools.RegionManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;

public class DefineCommand extends Subcommand {
	@Override
	public String getName() {
		return "define";
	}

	@Override
	public String getDescription() {
		return "Defines a region in which players can perform 'dac init' to create the DAC game.";
	}

	@Override
	public String getSyntax() {
		return "/dac define <regionName>";
	}

	@Override
	public String getPermission() {
		return "dac.setup";
	}

	@Override
	public void perform(DAC dac, CommandSender commandSender, String[] args) {

		if (!this.hasPermission(commandSender)) {
			return;
		}

		org.bukkit.entity.Player player = Bukkit.getPlayer(commandSender.getName());
		if (player == null) {
			Bukkit.getLogger().warning("This command must be used by a player.");
			return;
		}

		if (args.length < 2) {
			player.sendMessage(Component.text("You must provide a name for the DAC region.", NamedTextColor.RED));
			return;
		}

		Region region = RegionManagement.getPlayerSelection(player);
		if (region == null) {
			return;
		}

		String dacName = args[1];

		RegionManagement.saveRegionToConfig(dac, player, dacName, region, "base", true);

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." + dacName);
		if (config == null) {
			player.sendMessage(Component.text("Error while retrieving DAC regions.", NamedTextColor.RED));
			return;
		}

		config.set("world", player.getWorld().getName());
	}


	@Override
	public ArrayList<String> getAutoCompleteChoices(DAC dac) {
		return new ArrayList<>();
	}
}
