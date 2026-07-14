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
import fr.ethilvan.dac.tools.Colors;
import fr.ethilvan.dac.tools.MessageManagement;
import fr.ethilvan.dac.tools.RegionManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class JoinCommand extends Subcommand {

	public JoinCommand(DAC dac) {
		super(dac);
	}


	@Override
	public String getName() {
		return "join";
	}

	@Override
	public String getDescription() {
		return MessageManagement.getMessageFromKey(dac, "messages.commands.join.description");
	}

	@Override
	public String getSyntax() {
		return "/dac join <color>";
	}

	@Override
	public String getPermission() {
		return "dac.join";
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

		if (args.length < 2) {
			MessageManagement.messageToSender(dac, player, "messages.commands.join.noColorProvided");
			return;
		}

		String color = args[1];

		if (!Colors.getAvailableColors().contains(color.toUpperCase())) {
			String colorsString = Colors.getChatColorsListInString();
			MiniMessage mm = MiniMessage.miniMessage();
			String message = MessageManagement.getMessageFromKey(dac, "messages.commands.join.wrongColorName");
			message = message.replaceAll("\\{colors}", colorsString);
			Component parsed = mm.deserialize(message);
			player.sendMessage(parsed);
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
			MessageManagement.messageToSender(dac, player, "messages.commands.errors.worldRegionsRetrieve");
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
					boolean hasErrors = errorsWhileJoiningDacGame(dac, player, dacName, color, regionsManager);
					if (hasErrors) {
						return;
					}
					HashMap<String, String> placeholders = new HashMap<>();
					placeholders.put("\\{dac-name\\}", dacName);
					MessageManagement.messageToSender(dac, player, "messages.commands.join.success", placeholders);
					return;
				}
			}
		}

		MessageManagement.messageToSender(dac, player, "messages.commands.errors.notInRegion");
	}


	private boolean errorsWhileJoiningDacGame(
			DAC dac,
			Player player,
			String dacName,
			String color,
			RegionManager regionsManager
	) {
		if (!dac.getGames().containsKey(dacName)) {
			MessageManagement.messageToSender(dac, player, "messages.commands.join.noGameInRegion");
			return true;
		}

		DacGame dacGame = dac.getGames().get(dacName);
		if (dacGame.getPlayerMaterials().containsKey(player.getName())) {
			MessageManagement.messageToSender(dac, player, "messages.commands.join.alreadyJoined");
			return true;
		}

		if (dacGame.getPlayerMaterials().containsValue(Colors.convertColorToMaterial(color))) {
			MessageManagement.messageToSender(dac, player, "messages.commands.join.colorTaken");
			return true;
		}

		dacGame.addPlayerMaterial(player.getName(), color);
		dacGame.addPlayerLocation(player.getName(), player.getLocation());
		dacGame.addPlayerName(player.getName());

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." + dacName);
		if (config == null) {
			HashMap<String, String> placeholders = new HashMap<>();
			placeholders.put("\\{dac-name\\}", dacName);
			MessageManagement.messageToSender(dac, player, "messages.commands.join.noRegions", placeholders);
			return true;
		}

		String baseRegionName = config.getString("base");

		if (baseRegionName == null) {
			HashMap<String, String> placeholders = new HashMap<>();
			placeholders.put("\\{dac-name}", dacName);
			MessageManagement.messageToSender(dac, player, "messages.commands.join.noBaseRegion", placeholders);
			return true;
		}

		ProtectedRegion region = regionsManager.getRegion(baseRegionName);
		if (region == null) {
			MessageManagement.messageToSender(dac, player, "messages.commands.errors.noWorldGuardRegion");
			return true;
		}

		ArrayList<Player> playersInRegion = RegionManagement.getPlayersInWGRegion(player.getWorld(), region);
		HashMap<String, String> placeholders = new HashMap<>();
		placeholders.put("\\{dac-name}", dacName);
		placeholders.put("\\{color}", color.toLowerCase());
		placeholders.put("\\{color-name}", color.toUpperCase());
		placeholders.put("\\{player-name\\}", player.getName());
		MiniMessage mm = MiniMessage.miniMessage();
		String message = MessageManagement.getMessageFromKey(dac, "messages.commands.join.joined");
		message = MessageManagement.replacePlaceholders(message, placeholders);
		Component parsed = mm.deserialize(message);
		for (Player playerInRegion : playersInRegion) {
			if (playerInRegion.equals(player)) {
				continue;
			}
			playerInRegion.sendMessage(parsed);
		}
		return false;
	}


	@Override
	public ArrayList<String> getAutoCompleteChoices(DAC dac) {
		return new ArrayList<>(Colors.getAvailableColors());
	}
}
