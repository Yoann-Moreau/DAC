package fr.ethilvan.dac.commands;

import fr.ethilvan.dac.DAC;
import org.bukkit.command.CommandSender;

public abstract class Subcommand {
	public abstract String getName();

	public abstract String getDescription();

	public abstract String getSyntax();

	public abstract void perform(DAC dac, CommandSender commandSender, String[] args);
}
