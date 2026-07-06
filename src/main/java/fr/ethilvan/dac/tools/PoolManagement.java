package fr.ethilvan.dac.tools;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import fr.ethilvan.dac.DAC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

public class PoolManagement {

	public static String water(DAC dac, String dacName) {
		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions." + dacName);
		if (config == null) {
			return "Error while retrieving DAC regions.";
		}
		String poolRegionName = config.getString("pool");
		if (poolRegionName == null) {
			return "Error while retrieving pool region name.";
		}

		String worldName = config.getString("world");
		if (worldName == null) {
			return "Error while retrieving world name.";
		}

		Region poolRegion = RegionManagement.getExistingRegion(worldName, poolRegionName);
		if (poolRegion == null) {
			return "Error while retrieving pool region.";
		}

		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			return "Error while retrieving world.";
		}

		for (BlockVector3 blockVector3 : poolRegion) {
			Block block = world.getBlockAt(blockVector3.x(), blockVector3.y(), blockVector3.z());
			block.setType(Material.WATER);
		}

		return null;
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
			Block block = world.getBlockAt(blockVector3.x(), blockVector3.y(), blockVector3.z());
			if ((blockVector3.x() % 2 == 0 && blockVector3.z() % 2 != 0) ||
					(blockVector3.x() % 2 != 0 && blockVector3.z() % 2 == 0)) {
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
			Block block = world.getBlockAt(blockVector3.x(), blockVector3.y(), blockVector3.z());
			block.setType(Material.OBSIDIAN);
			if (i == randomBlockNumber) {
				randomBlockX = blockVector3.x();
				randomBlockZ = blockVector3.z();
			}
			i++;
		}

		for (int y = region.getMinimumPoint().y(); y <= region.getMaximumPoint().y(); y++) {
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
			Block block = world.getBlockAt(blockVector3.x(), blockVector3.y(), blockVector3.z());
			block.setType(Material.OBSIDIAN);
		}

		for (int y = region.getMinimumPoint().y(); y <= region.getMaximumPoint().y(); y++) {
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
			Block block = world.getBlockAt(blockVector3.x(), blockVector3.y(), blockVector3.z());
			if (block.getType().equals(Material.WATER)) {
				return false;
			}
		}

		return true;
	}
}
