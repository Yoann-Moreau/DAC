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

public class PoolCommand extends Subcommand {
	@Override
	public String getName() {
		return "pool";
	}

	@Override
	public String getDescription() {
		return "Defines the pool area";
	}

	@Override
	public String getSyntax() {
		return "/dac pool <dacName>";
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
			player.sendMessage(Component.text("You must provide the name of the DAC region.", NamedTextColor.RED));
			return;
		}

		String dacName = args[1];
		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." + dacName);
		if (config == null) {
			player.sendMessage(Component.text("Error while retrieving DAC regions.", NamedTextColor.RED));
			return;
		}

		String worldName = config.getString("world");
		if (worldName == null) {
			player.sendMessage(Component.text("Error while retrieving world name.", NamedTextColor.RED));
			return;
		}

		if (!player.getWorld().getName().equals(worldName)) {
			player.sendMessage(Component.text("You must be in the same world as the base region.",
					NamedTextColor.RED));
			return;
		}

		Region region = RegionManagement.getPlayerSelection(player);
		if (region == null) {
			return;
		}

		RegionManagement.saveRegionToConfig(dac, player, dacName, region, "pool");
	}


	@Override
	public ArrayList<String> getAutoCompleteChoices(DAC dac) {
		ArrayList<String> dacNames = new ArrayList<>();

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions");
		if (config == null) {
			Bukkit.getLogger().warning("Error while retrieving DAC regions.");
			return dacNames;
		}

		dacNames.addAll(config.getKeys(false));

		return dacNames;
	}
}
