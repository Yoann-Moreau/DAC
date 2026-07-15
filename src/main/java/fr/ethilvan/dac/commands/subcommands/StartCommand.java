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
import fr.ethilvan.dac.events.GameStartEvent;
import fr.ethilvan.dac.game.DacGame;
import fr.ethilvan.dac.tools.MessageManagement;
import fr.ethilvan.dac.tools.PoolManagement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;

public class StartCommand extends Subcommand {

	public StartCommand(DAC dac) {
		super(dac);
	}


	@Override
	public String getName() {
		return "start";
	}

	@Override
	public String getDescription() {
		return MessageManagement.getMessageFromKey(dac, "messages.commands.start.description");
	}

	@Override
	public String getSyntax() {
		return "/dac start";
	}

	@Override
	public String getPermission() {
		return "dac.play.start";
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
					this.startGame(dac, player, dacName);
					return;
				}
			}
		}

		MessageManagement.messageToSender(dac, player, "messages.commands.errors.notInRegion");
	}


	private void startGame(DAC dac, Player player, String dacName) {
		if (!dac.getGames().containsKey(dacName)) {
			MessageManagement.messageToSender(dac, player, "messages.commands.errors.noDacGame");
			return;
		}

		DacGame dacGame = dac.getGames().get(dacName);
		if (dacGame.isStarted()) {
			MessageManagement.messageToSender(dac, player, "messages.commands.start.alreadyStarted");
			return;
		}

		if (dacGame.getPlayerNames().isEmpty()) {
			MessageManagement.messageToSender(dac, player, "messages.commands.start.noPlayers");
			return;
		}

		ArrayList<Player>  playersInGame = new ArrayList<>();
		for (String playerName : dacGame.getPlayerNames()) {
			Player playerInLoop = Bukkit.getPlayer(playerName);
			if (playerInLoop == null) {
				dacGame.removePlayerDacColor(playerName);
				dacGame.removePlayerLocation(playerName);
				dacGame.removePlayerName(playerName);
				dacGame.removeCurrentPlayerName(playerName);
				continue;
			}
			playersInGame.add(playerInLoop);
		}
		MessageManagement.commandMessageToPlayers(dac, player, playersInGame, "messages.commands.start.started");

		String message = PoolManagement.waterPattern(dac, dacName);
		if (message != null) {
			player.sendRichMessage(message);
			return;
		}

		dacGame.randomizePlayerOrder();
		ArrayList<String> currentPlayers = new ArrayList<>(dacGame.getPlayerNames());
		dacGame.setCurrentPlayerNames(currentPlayers);
		dacGame.setStarted(true);

		dac.reloadConfig();

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." + dacName);
		if (config == null) {
			MessageManagement.messageToSender(dac, player,  "messages.errors.dacRegionsRetrieve");
			return;
		}

		String worldName = config.getString("world");
		if (worldName == null) {
			MessageManagement.messageToSender(dac, player,  "messages.errors.worldNameRetrieve");
			return;
		}

		double x = config.getDouble("diving.x");
		double y = config.getDouble("diving.y");
		double z = config.getDouble("diving.z");
		float yaw = (float) config.getDouble("diving.yaw");
		float pitch = (float) config.getDouble("diving.pitch");

		dacGame.setDivingLocation(new org.bukkit.Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch));

		Bukkit.getPluginManager().callEvent(new GameStartEvent(dacGame));
	}


	@Override
	public ArrayList<String> getAutoCompleteChoices(DAC dac) {
		return new ArrayList<>();
	}
}
