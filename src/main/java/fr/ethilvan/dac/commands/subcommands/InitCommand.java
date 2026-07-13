package fr.ethilvan.dac.commands.subcommands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.commands.Subcommand;
import fr.ethilvan.dac.game.DacGame;
import fr.ethilvan.dac.tools.MessageManagement;
import fr.ethilvan.dac.tools.RegionManagement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class InitCommand extends Subcommand {

	public InitCommand(DAC dac) {
		super(dac);
	}


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
	public String getPermission() {
		return "dac.init";
	}

	@Override
	public void perform(DAC dac, CommandSender sender, String[] args) {

		if (!this.hasPermission(dac, sender)) {
			return;
		}

		Player player = Bukkit.getPlayer(sender.getName());
		if (player == null) {
			MessageManagement.messageToSender(dac, sender, "messages.commands.errors.notAPlayer");
			return;
		}

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions");
		if (config == null) {
			MessageManagement.messageToSender(dac, player, "messages.commands.errors.noDefinedRegions");
			return;
		}

		Location wgLocation = BukkitAdapter.adapt(player.getLocation());
		World wgWorld = BukkitAdapter.adapt(player.getWorld());

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionsManager = container.get(wgWorld);

		if (regionsManager == null) {
			MessageManagement.messageToSender(dac, player, "messages.commands.errors.regionsRetrieve");
			return;
		}

		Set<String> dacNames = config.getKeys(false);
		for (String dacName : dacNames) {
			String regionName = (String) config.get(dacName + ".base");

			if (regionName == null) { // Skip iteration if the base region is not defined
				continue;
			}

			ProtectedRegion region = regionsManager.getRegion(regionName);
			RegionQuery query = container.createQuery();
			ApplicableRegionSet set = query.getApplicableRegions(wgLocation);

			for (ProtectedRegion item : set) {
				if (item.equals(region)) {
					createDacGame(dac, player, dacName, region);
					MessageManagement.messageToSender(dac, player, "messages.commands.init.success");
					return;
				}
			}
		}

		MessageManagement.messageToSender(dac, player, "messages.commands.errors.notInRegion");
	}


	private void createDacGame(DAC dac, Player sender, String dacName, ProtectedRegion region) {
		if (dac.getGames().containsKey(dacName)) {
			MessageManagement.messageToSender(dac, sender, "messages.commands.init.gameAlreadyInProgress");
			return;
		}

		dac.addGame(dacName, new DacGame(dac, dacName));
		ArrayList<Player> playersInRegion = RegionManagement.getPlayersInWGRegion(sender.getWorld(), region);
		HashMap<String, String> placeholders = new HashMap<>();
		placeholders.put("\\{dac-name\\}", dacName);
		MessageManagement.commandMessageToPlayers(
				dac,
				sender,
				playersInRegion,
				"messages.commands.init.newGameCreated",
				placeholders
		);
	}


	@Override
	public ArrayList<String> getAutoCompleteChoices(DAC dac) {
		return new ArrayList<>();
	}
}
