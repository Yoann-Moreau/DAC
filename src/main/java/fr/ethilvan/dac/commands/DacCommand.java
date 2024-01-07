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

		subcommands.add(new InitCommand());
		subcommands.add(new DefineCommand());
		subcommands.add(new PoolCommand());
		subcommands.add(new DivingCommand());
		subcommands.add(new JoinCommand());
		subcommands.add(new StartCommand());
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
			for (int i = 0; i < getSubcommands().size(); i++) {
				if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
					getSubcommands().get(i).perform(dac, commandSender, args);
				}
			}
		} else {
			commandSender.sendMessage("--------------------------------------------------");
			for (int i = 0; i < getSubcommands().size(); i++) {
				commandSender.sendMessage(getSubcommands().get(i).getSyntax() + " - " +
						getSubcommands().get(i).getDescription());
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

			for (int i = 0; i < getSubcommands().size(); i++) {
				subcommandNames.add(getSubcommands().get(i).getName());
			}

			return subcommandNames;
		}

		return new ArrayList<>();
	}

}
