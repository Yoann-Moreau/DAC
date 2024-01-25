package fr.ethilvan.dac.commands;

import fr.ethilvan.dac.DAC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public abstract class Subcommand {
	public abstract String getName();

	public abstract String getDescription();

	public abstract String getSyntax();

	public abstract String getPermission();

	public abstract void perform(DAC dac, CommandSender commandSender, String[] args);

	public abstract ArrayList<String> getAutoCompleteChoices(DAC dac);

	public boolean hasPermission(CommandSender commandSender) {
		if (commandSender.hasPermission(this.getPermission())) {
			return true;
		}
		commandSender.sendMessage(
				Component.text("You do not have permission to perform this command.", NamedTextColor.RED)
		);
		return false;
	}
}
