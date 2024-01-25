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

		HashMap<String, Object> pos = new HashMap<>();
		pos.put("x", player.getLocation().getX());
		pos.put("y", player.getLocation().getY());
		pos.put("z", player.getLocation().getZ());
		pos.put("pitch", player.getLocation().getPitch());
		pos.put("yaw", player.getLocation().getYaw());

		config.set("diving", pos);

		dac.saveConfig();
	}


	@Override
	public ArrayList<String> getAutoCompleteChoices() {
		return null;
	}
}
