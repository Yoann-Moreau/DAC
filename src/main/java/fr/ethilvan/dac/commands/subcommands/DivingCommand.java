package fr.ethilvan.dac.commands.subcommands;

import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.commands.Subcommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;

public class DivingCommand extends Subcommand {
	@Override
	public String getName() {
		return "diving";
	}

	@Override
	public String getDescription() {
		return "Sets the position of the player on the diving platform.";
	}

	@Override
	public String getSyntax() {
		return "/dac diving <dacName>";
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

		String regionName = args[1];
		ConfigurationSection regions = dac.getConfig().getConfigurationSection("regions");

		if (regions == null) {
			regions = dac.getConfig().createSection("regions");
		}

		ConfigurationSection section = regions.getConfigurationSection(regionName);
		if (section == null) {
			player.sendMessage(Component.text("You must first use 'dac define'"));
			return;
		}

		HashMap<String, Object> pos = new HashMap<>();
		pos.put("x", player.getLocation().getX());
		pos.put("y", player.getLocation().getY());
		pos.put("z", player.getLocation().getZ());
		pos.put("pitch", player.getLocation().getPitch());
		pos.put("yaw", player.getLocation().getYaw());

		section.set("diving", pos);
		section.set("world", player.getWorld().getName());

		dac.saveConfig();
	}


	@Override
	public ArrayList<String> getAutoCompleteChoices() {
		return null;
	}
}
