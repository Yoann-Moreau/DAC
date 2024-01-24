package fr.ethilvan.dac.tools;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import fr.ethilvan.dac.DAC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Random;

public class PoolManagement {

	public static boolean refillPool(DAC dac, String dacName, Player player) {
		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." + dacName);
		if (config == null) {
			player.sendMessage(Component.text("Error while retrieving DAC regions", NamedTextColor.RED));
			return false;
		}
		String poolRegionName = config.getString("pool");

		Region region = RegionManagement.getExistingRegion(player, poolRegionName);
		if (region == null) {
			return false;
		}

		String worldName = config.getString("world");
		if (worldName == null) {
			player.sendMessage(Component.text("Error while retrieving world name", NamedTextColor.RED));
			return false;
		}
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


	public static String grid(DAC dac, String dacName) {
		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." + dacName);
		if (config == null) {
			return "Error while retrieving DAC regions.";
		}

		String poolRegionName = config.getString("pool");
		String worldName = config.getString("world");
		if (worldName == null) {
			return "Error while retrieving world name";
		}

		Region region = RegionManagement.getExistingRegion(worldName, poolRegionName);
		if (region == null) {
			return "Error while retrieving pool region.";
		}

		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			return "Error while retrieving world.";
		}

		for (BlockVector3 blockVector3 : region) {
			Block block = world.getBlockAt(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());
			if ((blockVector3.getBlockX() % 2 == 0 && blockVector3.getBlockZ() % 2 != 0) ||
					(blockVector3.getBlockX() % 2 != 0 && blockVector3.getBlockZ() % 2 == 0)) {
				block.setType(Material.WATER);
			}
			else {
				block.setType(Material.OBSIDIAN);
			}
		}

		return null;
	}


	public static String dac(DAC dac, String dacName) {
		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." + dacName);
		if (config == null) {
			return "Error while retrieving DAC regions.";
		}

		String poolRegionName = config.getString("pool");
		String worldName = config.getString("world");
		if (worldName == null) {
			return "Error while retrieving world name";
		}

		Region region = RegionManagement.getExistingRegion(worldName, poolRegionName);
		if (region == null) {
			return "Error while retrieving pool region.";
		}

		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			return "Error while retrieving world.";
		}

		Random random = new Random();
		int randomBlockNumber = random.nextInt((int) region.getVolume());

		int randomBlockX = 0;
		int randomBlockZ = 0;
		int i = 0;
		for (BlockVector3 blockVector3 : region) {
			Block block = world.getBlockAt(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());
			block.setType(Material.OBSIDIAN);
			if (i == randomBlockNumber) {
				randomBlockX = blockVector3.getBlockX();
				randomBlockZ = blockVector3.getBlockZ();
			}
			i++;
		}

		for (int y = region.getMinimumPoint().getBlockY(); y <= region.getMaximumPoint().getBlockY(); y++) {
			Block block = world.getBlockAt(randomBlockX, y, randomBlockZ);
			block.setType(Material.WATER);
		}

		return null;
	}


	public static String dac(DAC dac, String dacName, int randomBlockX, int randomBlockZ) {
		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." + dacName);
		if (config == null) {
			return "Error while retrieving DAC regions.";
		}

		String poolRegionName = config.getString("pool");
		String worldName = config.getString("world");
		if (worldName == null) {
			return "Error while retrieving world name";
		}

		Region region = RegionManagement.getExistingRegion(worldName, poolRegionName);
		if (region == null) {
			return "Error while retrieving pool region.";
		}

		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			return "Error while retrieving world.";
		}

		for (BlockVector3 blockVector3 : region) {
			Block block = world.getBlockAt(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());
			block.setType(Material.OBSIDIAN);
		}

		for (int y = region.getMinimumPoint().getBlockY(); y <= region.getMaximumPoint().getBlockY(); y++) {
			Block block = world.getBlockAt(randomBlockX, y, randomBlockZ);
			block.setType(Material.WATER);
		}

		return null;
	}


	public static BlockVector3 getRandomBlockInPool(Region poolRegion) {
		Random random = new Random();
		int randomBlockNumber = random.nextInt((int) poolRegion.getVolume());

		int i = 0;
		for (BlockVector3 blockVector3 : poolRegion) {
			if (i == randomBlockNumber) {
				return blockVector3;
			}
			i++;
		}

		return null;
	}


	public static boolean isPoolFilled(World world, Region poolRegion) {
		for (BlockVector3 blockVector3 : poolRegion) {
			Block block = world.getBlockAt(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());
			if (block.getType().equals(Material.WATER)) {
				return false;
			}
		}

		return true;
	}
}
