package fr.ethilvan.dac.tools;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import fr.ethilvan.dac.DAC;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import java.util.HashMap;
import java.util.Objects;

public class RegionManagement {

	public static Region getPlayerSelection(org.bukkit.entity.Player player) {

		Player actor = BukkitAdapter.adapt(player);
		SessionManager manager = WorldEdit.getInstance().getSessionManager();
		LocalSession session = manager.get(actor);

		Region region;
		World selectionWorld = session.getSelectionWorld();
		try {
			if (selectionWorld == null) throw new IncompleteRegionException();
			region = session.getSelection(selectionWorld);
		} catch (IncompleteRegionException ex) {
			actor.printError(TextComponent.of("Please make a WorldEdit region selection first."));
			return null;
		}
		return region;
	}


	public static boolean saveRegionToConfig(DAC dac, String regionName, Region region) {

		HashMap<String, HashMap<String, Object>> regionsMap = new HashMap<>();

		dac.getLogger().info(regionsMap.toString());
		loadRegionsFromConfig(dac, regionsMap);
		dac.getLogger().info(regionsMap.toString());

		// Check if region already exists
		if (regionsMap.containsKey(regionName)) {
			return false;
		}

		if (region instanceof CuboidRegion cuboidRegion) {
			addCuboidRegionToHashMap(dac, regionName, cuboidRegion, regionsMap);
		}
		else if (region instanceof Polygonal2DRegion polygonal2DRegion) {
			//
		}

		dac.getLogger().info(regionsMap.toString());

		dac.getConfig().set("regions", regionsMap);
		dac.saveConfig();

		return true;
	}


	public static void loadRegionsFromConfig(DAC dac, HashMap<String, HashMap<String, Object>> regionsMap) {

		ConfigurationSection regions = dac.getConfig().getConfigurationSection("regions");
		if (regions == null) {
			dac.getLogger().warning("null");
			return;
		}
		for (String key : regions.getKeys(true)) {
			if (key.contains(".")) { // skip sub keys
				continue;
			}
			if (Objects.equals(regions.get(key + ".type"), "cuboid")) {
				BlockVector3 blockVector3 = BlockVector3.ZERO;
				BlockVector3 pos1 = blockVector3.add(
						regions.getInt(key + "pos1.x"),
						regions.getInt(key + "pos1.y"),
						regions.getInt(key + "pos1.z")
				);
				BlockVector3 pos2 = blockVector3.add(
						regions.getInt(key + "pos2.x"),
						regions.getInt(key + "pos2.y"),
						regions.getInt(key + "pos2.z")
				);
				CuboidRegion cuboidRegion = new CuboidRegion(pos1, pos2);
				addCuboidRegionToHashMap(dac, key, cuboidRegion, regionsMap);
			}
		}

		dac.getLogger().info("load: " + regionsMap.toString());
	}


	private static void addCuboidRegionToHashMap(
			DAC dac,
			String regionName,
			CuboidRegion cuboidRegion,
			HashMap<String, HashMap<String, Object>> regionsMap
	) {

		dac.regions.put(regionName, cuboidRegion);
		HashMap<String, Object> regionMap = new HashMap<>();
		regionMap.put("type", "cuboid");
		HashMap<String, Object> pos1Map = new HashMap<>();
		pos1Map.put("x", cuboidRegion.getPos1().getX());
		pos1Map.put("y", cuboidRegion.getPos1().getY());
		pos1Map.put("z", cuboidRegion.getPos1().getZ());
		regionMap.put("pos1", pos1Map);
		HashMap<String, Object> pos2Map = new HashMap<>();
		pos2Map.put("x", cuboidRegion.getPos2().getX());
		pos2Map.put("y", cuboidRegion.getPos2().getY());
		pos2Map.put("z", cuboidRegion.getPos2().getZ());
		regionMap.put("pos2", pos2Map);
		regionsMap.put(regionName, regionMap);
	}

}
