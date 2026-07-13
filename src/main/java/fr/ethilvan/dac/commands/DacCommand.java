package fr.ethilvan.dac.commands;

import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class DacCommand implements TabExecutor {

	private final DAC dac;

	private final ArrayList<Subcommand> subcommands = new ArrayList<>();

	public DacCommand(DAC dac) {
		this.dac = dac;

		subcommands.add(new InitCommand(dac));
		subcommands.add(new JoinCommand(dac));
		subcommands.add(new StartCommand(dac));
		subcommands.add(new StopCommand(dac));
		subcommands.add(new FillCommand(dac));
		subcommands.add(new DefineCommand(dac));
		subcommands.add(new PoolCommand(dac));
		subcommands.add(new DivingCommand(dac));
		subcommands.add(new DeleteCommand(dac));
	}


	public ArrayList<Subcommand> getSubcommands() {
		return subcommands;
	}


	@Override
	public boolean onCommand(
			@NotNull CommandSender commandSender,
			@NotNull Command command,
			@NotNull String s,
			@NotNull String[] args
	) {

		if (args.length > 0) {
			for (Subcommand subcommand : subcommands) {
				if (args[0].equalsIgnoreCase(subcommand.getName())) {
					subcommand.perform(dac, commandSender, args);
				}
			}
		}
		else {
			commandSender.sendMessage("-------------------DAC commands-------------------");
			for (Subcommand subcommand : getSubcommands()) {
				commandSender.sendRichMessage("<gold>" + subcommand.getSyntax() + "<gray>: <white>" +
						subcommand.getDescription());
			}
			commandSender.sendMessage("--------------------------------------------------");
		}

		return true;
	}


	@Override
	public @Nullable List<String> onTabComplete(
			@NotNull CommandSender commandSender,
			@NotNull Command command,
			@NotNull String label,
			@NotNull String[] args
	) {

		if (args.length == 1) {
			ArrayList<String> subcommandNames = new ArrayList<>();

			for (Subcommand subcommand : getSubcommands()) {
				if (subcommand.hasPermission(dac, commandSender)) {
					subcommandNames.add(subcommand.getName());
				}
			}

			return subcommandNames;
		}
		else if (args.length == 2) {
			for (Subcommand subcommand : getSubcommands()) {
				if (subcommand.getName().equals(args[0])) {
					return subcommand.getAutoCompleteChoices(this.dac);
				}
			}
		}

		return new ArrayList<>();
	}

}
