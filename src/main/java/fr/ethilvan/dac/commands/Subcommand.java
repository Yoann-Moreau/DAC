package fr.ethilvan.dac.commands;

import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.tools.MessageManagement;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;


public abstract class Subcommand {

	protected DAC dac;


	public Subcommand(DAC dac) {
		this.dac = dac;
	}


	public abstract String getName();

	public abstract String getDescription();

	public abstract String getSyntax();

	public abstract String getPermission();

	public abstract void perform(DAC dac, CommandSender commandSender, String[] args);

	public abstract ArrayList<String> getAutoCompleteChoices(DAC dac);


	public boolean hasPermission(DAC dac, CommandSender commandSender) {
		if (commandSender.hasPermission(this.getPermission())) {
			return true;
		}
		MessageManagement.messageToSender(dac, commandSender, "messages.commands.errors.noPermission");
		return false;
	}
}
