package fr.ethilvan.dac.commands.subcommands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
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
import fr.ethilvan.dac.tools.PoolManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;


public class StopCommand extends Subcommand {

	public StopCommand(DAC dac) {
		super(dac);
	}


	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public String getDescription() {
		return MessageManagement.getMessageFromKey(dac, "messages.commands.stop.description");
	}

	@Override
	public String getSyntax() {
		return "/dac stop [dacName]";
	}

	@Override
	public String getPermission() {
		return "dac.play.stop";
	}

	@Override
	public void perform(DAC dac, CommandSender commandSender, String[] args) {

		if (!this.hasPermission(dac, commandSender)) {
			return;
		}

		Player player = Bukkit.getPlayer(commandSender.getName());
		if (player == null && args.length < 2) {
			dac.getLogger().warning(
					MessageManagement.getMessageFromKey(dac, "messages.commands.errors.dacNameNeeded")
			);
			return;
		}

		String dacName = "";
		if (args.length >= 2) {
			dacName = args[1];
		}

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions");
		if (config == null) {
			if (player == null) {
				dac.getLogger().warning(
						MessageManagement.getMessageFromKey(dac, "messages.commands.errors.noDefinedRegions")
				);
				return;
			}
			MessageManagement.messageToPlayer(dac, player, "messages.commands.errors.noDefinedRegions");
			return;
		}

		if (player == null || !dacName.isEmpty()) {
			this.stopGame(dac, player, dacName);
			return;
		}

		stopGame(dac, player);
	}


	private void stopGame(DAC dac, Player player, String dacName) {

		if (!dac.getGames().containsKey(dacName)) {
			if (player == null) {
				dac.getLogger().warning(MessageManagement.getMessageFromKey(dac, "messages.commands.errors.noDacGame"));
				return;
			}
			MessageManagement.messageToPlayer(dac, player, "messages.commands.errors.noDacGame");
			return;
		}

		DacGame dacGame = dac.getGames().get(dacName);
		if (!dacGame.isStarted()) {
			if (player == null) {
				dac.getLogger().warning(
						MessageManagement.getMessageFromKey(dac, "messages.commands.stop.noGameStarted")
				);
				return;
			}
			MessageManagement.messageToPlayer(dac, player, "messages.commands.stop.noGameStarted");
			return;
		}


		if (!this.resetAndStopGame(dac, dacGame, dacName, player)) {
			return;
		}
		if (player == null) {
			dac.getLogger().info(
					MessageManagement.getMessageFromKey(dac, "messages.commands.stop.success")
			);
			return;
		}
		HashMap<String, String> placeholders = new HashMap<>();
		placeholders.put("\\{dac-name}", dacName);
		MessageManagement.messageToPlayer(dac, player, "messages.commands.stop.success", placeholders);
	}


	private void stopGame(DAC dac, Player player) {

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions");
		if (config == null) {
			MessageManagement.messageToPlayer(dac, player, "messages.commands.errors.noDefinedRegions");
			return;
		}

		Location wgLocation = BukkitAdapter.adapt(player.getLocation());
		com.sk89q.worldedit.world.World wgWorld = BukkitAdapter.adapt(player.getWorld());

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionsManager = container.get(wgWorld);

		if (regionsManager == null) {
			MessageManagement.messageToPlayer(dac, player, "messages.commands.errors.worldRegionsRetrieve");
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

					if (!dac.getGames().containsKey(dacName)) {
						MessageManagement.messageToPlayer(dac, player, "messages.commands.errors.noDacGame");
						return;
					}

					DacGame dacGame = dac.getGames().get(dacName);
					if (!this.resetAndStopGame(dac, dacGame, dacName, player)) {
						return;
					}
					HashMap<String, String> placeholders = new HashMap<>();
					placeholders.put("\\{dac-name}", dacName);
					MessageManagement.messageToPlayer(dac, player, "messages.commands.stop.success", placeholders);
					return;
				}
			}
		}

		MessageManagement.messageToPlayer(dac, player, "messages.commands.errors.notInRegion");
	}


	private boolean resetAndStopGame(DAC dac, DacGame dacGame, String dacName, Player player) {

		String message = PoolManagement.waterPattern(dac, dacName);
		if (message != null) {
			player.sendMessage(Component.text(message, NamedTextColor.RED));
			return false;
		}

		ArrayList<Player> players = new ArrayList<>();
		for (UUID playerUuid : dacGame.getPlayerUuids()) {
			if (player != null && playerUuid.equals(player.getUniqueId())) {
				continue;
			}
			players.add(Bukkit.getPlayer(playerUuid));
		}
		HashMap<String, String> placeholders = new HashMap<>();
		placeholders.put("\\{dac-name}", dacName);
		MessageManagement.messageToPlayers(dac, players, "messages.commands.stop.success", placeholders);

		dacGame.setStarted(false);
		dacGame.setPlayerDacColors(null);
		dacGame.setPlayerLocations(null);
		dacGame.setPlayerUuids(null);
		dacGame.setCurrentPlayerUuids(null);
		dac.removeGame(dacName);

		return true;
	}


	@Override
	public ArrayList<String> getAutoCompleteChoices(DAC dac) {
		return new ArrayList<>(dac.getGames().keySet());
	}
}
