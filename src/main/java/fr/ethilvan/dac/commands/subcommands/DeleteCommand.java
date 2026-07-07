package fr.ethilvan.dac.commands.subcommands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.commands.Subcommand;
import fr.ethilvan.dac.tools.DacManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;


public class DeleteCommand extends Subcommand {

	@Override
	public String getName() {
		return "delete";
	}


	@Override
	public String getDescription() {
		return "Deletes a DAC (base region, pool region and diving position)";
	}


	@Override
	public String getSyntax() {
		return "/dac delete <dacName>";
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

		Player player = Bukkit.getPlayer(commandSender.getName());
		if (player == null) {
			dac.getLogger().warning("This command must be used by a player.");
			return;
		}

		if (args.length < 2) {
			player.sendMessage(Component.text("You must provide the name of the DAC region.", NamedTextColor.RED));
			return;
		}

		String dacName = args[1];
		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions");
		if (config == null) {
			player.sendMessage(Component.text("Error while retrieving DAC regions.", NamedTextColor.RED));
			return;
		}

		ConfigurationSection configDac = config.getConfigurationSection(dacName);
		if (configDac == null) {
			player.sendMessage(Component.text("This DAC does not exist.", NamedTextColor.RED));
			return;
		}

		String worldName = configDac.getString("world");
		if (worldName == null) {
			player.sendMessage(Component.text("Error while retrieving world name.", NamedTextColor.RED));
			return;
		}

		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			player.sendMessage(Component.text("Error while retrieving world.", NamedTextColor.RED));
			return;
		}

		String dacBaseName = configDac.getString("base");
		String dacPoolName = configDac.getString("pool");
		if  (dacBaseName == null || dacPoolName == null) {
			player.sendMessage(Component.text("Error while retrieving DAC region names.", NamedTextColor.RED));
			return;
		}

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionsManager = container.get(BukkitAdapter.adapt(world));

		if (regionsManager == null) {
			player.sendMessage(Component.text("Error when retrieving world regions.", NamedTextColor.RED));
			return;
		}

		// Removes DAC from config file
		config.set(dacName, null);
		dac.saveConfig();

		// Removes regions from WorldGuard config
		regionsManager.removeRegion(dacBaseName);
		regionsManager.removeRegion(dacPoolName);

		player.sendMessage(Component.text("DAC (" + dacName + ") has been successfully deleted.", NamedTextColor.GREEN));
	}


	@Override
	public ArrayList<String> getAutoCompleteChoices(DAC dac) {
		return DacManagement.getDacNames(dac);
	}
}
