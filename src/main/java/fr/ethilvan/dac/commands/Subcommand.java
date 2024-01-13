package fr.ethilvan.dac.commands;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.tools.RegionManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class Subcommand {
	public abstract String getName();

	public abstract String getDescription();

	public abstract String getSyntax();

	public abstract void perform(DAC dac, CommandSender commandSender, String[] args);

	public boolean refillPool(DAC dac, String dacName, Player player) {
		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." + dacName);
		assert config != null;
		String poolRegionName = config.getString("pool");

		Region region = RegionManagement.getExistingRegion(player, poolRegionName);
		if (region == null) {
			return false;
		}

		String worldName = config.getString("world");
		assert worldName != null;
		World world = Bukkit.getWorld(worldName);

		if (world == null) {
			player.sendMessage(Component.text("Error while retrieving world.", NamedTextColor.RED));
			return false;
		}

		for (BlockVector3 blockVector3 : region) {
			Block block = world.getBlockAt(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());
			block.setType(Material.WATER);
		}

		return true;
	}
}
