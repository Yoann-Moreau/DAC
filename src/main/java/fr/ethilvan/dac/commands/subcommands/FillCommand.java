package fr.ethilvan.dac.commands.subcommands;

import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.commands.Subcommand;
import fr.ethilvan.dac.tools.PoolManagement;
import fr.ethilvan.dac.tools.RegionManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class FillCommand extends Subcommand {
	@Override
	public String getName() {
		return "fill";
	}

	@Override
	public String getDescription() {
		return "Fills the pool with the provided pattern.";
	}

	@Override
	public String getSyntax() {
		return "/dac fill <pattern>";
	}

	@Override
	public String getPermission() {
		return "dac.fill";
	}

	@Override
	public void perform(DAC dac, CommandSender commandSender, String[] args) {

		if (!this.hasPermission(commandSender)) {
			return;
		}

		Player player = Bukkit.getPlayer(commandSender.getName());
		if (player == null) {
			dac.getLogger().warning("This command must be used by a player.");
			return;
		}

		if (args.length < 2) {
			player.sendMessage(Component.text("You must provide a pattern.", NamedTextColor.RED));
			return;
		}

		String pattern = args[1];
		if (!this.getPatterns().contains(pattern)) {
			player.sendMessage("The available patterns are: " + String.join(", ", this.getPatterns()));
			return;
		}

		String dacName = RegionManagement.getDacNameByPlayer(dac, player);
		if (dacName == null) {
			return;
		}

		String message = null;
		switch (pattern) {
			case "water" -> message = PoolManagement.water(dac, dacName);
			case "grid" -> message = PoolManagement.grid(dac, dacName);
			case "dac" -> message = PoolManagement.dac(dac, dacName);
		}
		if (message != null) {
			player.sendMessage(Component.text(message, NamedTextColor.RED));
		}
	}

	@Override
	public ArrayList<String> getAutoCompleteChoices(DAC dac) {
		return this.getPatterns();
	}


	private ArrayList<String> getPatterns() {
		ArrayList<String> patterns = new ArrayList<>();
		patterns.add("water");
		patterns.add("grid");
		patterns.add("dac");
		return patterns;
	}
}
