package fr.ethilvan.dac.commands.subcommands;

import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.commands.Subcommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

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
	public void perform(DAC dac, CommandSender commandSender, String[] args) {

		if (!commandSender.hasPermission("dac.init")) {
			commandSender.sendMessage(
					Component.text("You do not have permission to perform this command.", NamedTextColor.RED)
			);
			return;
		}

		commandSender.sendMessage(
				Component.text("A DAC game has been created at your location.", NamedTextColor.GREEN)
		);
	}
}
