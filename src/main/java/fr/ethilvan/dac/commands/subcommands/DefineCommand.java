package fr.ethilvan.dac.commands.subcommands;

import com.sk89q.worldedit.regions.Region;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.commands.Subcommand;
import fr.ethilvan.dac.tools.RegionManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class DefineCommand extends Subcommand {
	@Override
	public String getName() {
		return "define";
	}

	@Override
	public String getDescription() {
		return "Defines a region in which players can perform dac init.";
	}

	@Override
	public String getSyntax() {
		return "/dac define <regionName>";
	}

	@Override
	public void perform(DAC dac, CommandSender commandSender, String[] args) {

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

		RegionManagement.saveRegionToConfig(player, args[1], region, true);
	}
}
