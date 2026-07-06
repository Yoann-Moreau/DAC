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
import fr.ethilvan.dac.tools.PoolManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;

public class StartCommand extends Subcommand {
	@Override
	public String getName() {
		return "start";
	}

	@Override
	public String getDescription() {
		return "Starts the DAC game of the corresponding region";
	}

	@Override
	public String getSyntax() {
		return "/dac start";
	}

	@Override
	public String getPermission() {
		return "dac.start";
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

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions");
		if (config == null) {
			player.sendMessage(Component.text("There are no defined DAC regions.", NamedTextColor.RED));
			return;
		}

		Location wgLocation = BukkitAdapter.adapt(player.getLocation());
		World wgWorld = BukkitAdapter.adapt(player.getWorld());

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionsManager = container.get(wgWorld);

		if (regionsManager == null) {
			player.sendMessage(Component.text("Error when retrieving world regions.", NamedTextColor.RED));
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

		player.sendMessage(Component.text("You are not in a DAC region.", NamedTextColor.RED));
	}


	private void startGame(DAC dac, Player player, String dacName) {
		if (!dac.getGames().containsKey(dacName)) {
			player.sendMessage(Component.text("No DAC games exists in this region.", NamedTextColor.RED));
			return;
		}

		DacGame dacGame = dac.getGames().get(dacName);
		if (dacGame.isStarted()) {
			player.sendMessage(Component.text("A DAC game has already been started in this region.", NamedTextColor.RED));
			return;
		}

		if (dacGame.getPlayerNames().isEmpty()) {
			player.sendMessage(Component.text("No players have joined this DAC game.", NamedTextColor.RED));
			return;
		}

		for (String playerName : dacGame.getPlayerNames()) {
			Player playerInLoop = Bukkit.getPlayer(playerName);
			if (playerInLoop == null) {
				dacGame.removePlayerMaterial(playerName);
				dacGame.removePlayerLocation(playerName);
				dacGame.removePlayerName(playerName);
				dacGame.removeCurrentPlayerName(playerName);
				continue;
			}

			playerInLoop.sendMessage(Component.text("The DAC game has started", NamedTextColor.GREEN));
		}

		String message = PoolManagement.water(dac, dacName);
		if (message != null) {
			player.sendMessage(Component.text(message, NamedTextColor.RED));
			return;
		}

		dacGame.randomizePlayerOrder();
		ArrayList<String> currentPlayers = new ArrayList<>(dacGame.getPlayerNames());
		dacGame.setCurrentPlayerNames(currentPlayers);
		dacGame.setStarted(true);

		dac.reloadConfig();

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." + dacName);
		if (config == null) {
			player.sendMessage(Component.text("Error while retrieving DAC regions.", NamedTextColor.RED));
			return;
		}

		String worldName = config.getString("world");
		if (worldName == null) {
			player.sendMessage(Component.text("Error while retrieving world name.", NamedTextColor.RED));
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
