package fr.ethilvan.dac.tools;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import fr.ethilvan.dac.DAC;
import fr.ethilvan.dac.worldedit.Selection;
import org.bukkit.configuration.MemorySection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

		HashMap<String, Object> regions = new HashMap<>();

		loadRegionsFromConfig(dac, regions);

		// Check if region already exists
		if (regions.containsKey(regionName)) {
			return false;
		}

		Selection selection = new Selection(region);
		Map<String, Object> map = selection.serialize();
		regions.put(regionName, map);

		dac.getConfig().set("regions", regions);
		dac.saveConfig();

		return true;
	}


	public static void loadRegionsFromConfig(DAC dac, HashMap<String, Object> regionsMap) {

		Object regions = dac.getConfig().get("regions");
		if (regions == null) {
			return;
		}

		if (regions instanceof MemorySection memorySection) {

			for (String key : memorySection.getKeys(false)) {
				if (Objects.equals(memorySection.get(key + ".type"), "cuboid")) {
					BlockVector3 blockVector3 = BlockVector3.ZERO;
					BlockVector3 pos1 = blockVector3.add(
							memorySection.getInt(key + "pos1.x"),
							memorySection.getInt(key + "pos1.y"),
							memorySection.getInt(key + "pos1.z")
					);
					BlockVector3 pos2 = blockVector3.add(
							memorySection.getInt(key + "pos2.x"),
							memorySection.getInt(key + "pos2.y"),
							memorySection.getInt(key + "pos2.z")
					);
					CuboidRegion cuboidRegion = new CuboidRegion(pos1, pos2);
					addCuboidRegionToHashMap(dac, key, cuboidRegion, regionsMap);
				}
				else if (Objects.equals(memorySection.get(key + ".type"), "poly2d")) {
					Polygonal2DRegion polygonal2DRegion = new Polygonal2DRegion();
					polygonal2DRegion.setMinimumY(memorySection.getInt(key + ".minY"));
					polygonal2DRegion.setMaximumY(memorySection.getInt(key + ".maxY"));
					@NotNull List<Map<?, ?>> points = memorySection.getMapList(key + ".points");
					for (Map<?, ?> point : points) {
						BlockVector2 blockVector2 = BlockVector2.ZERO;
						BlockVector2 pos = blockVector2.add(
								Integer.parseInt(point.get("x").toString()),
								Integer.parseInt(point.get("z").toString())
						);
						polygonal2DRegion.addPoint(pos);
					}
					addPoly2dRegionToHashMap(dac, key, polygonal2DRegion, regionsMap);
				}
			}
		}
		else {
			if (regions instanceof HashMap<?, ?> regionsHashMap) {
				for (Object key : regionsHashMap.keySet()) {
					Object regionMap = regionsHashMap.get(key);
					if (regionMap instanceof HashMap<?, ?> hashMap) {
						Region region = Selection.deserialize(hashMap).getRegion();
						if (region instanceof CuboidRegion cuboidRegion) {
							addCuboidRegionToHashMap(dac, (String) key, cuboidRegion, regionsMap);
						}
						else if (region instanceof Polygonal2DRegion polygonal2DRegion) {
							addPoly2dRegionToHashMap(dac, (String) key, polygonal2DRegion, regionsMap);
						}
					}
				}
			}
		}
	}


	private static void addCuboidRegionToHashMap(
			DAC dac,
			String regionName,
			CuboidRegion cuboidRegion,
			HashMap<String, Object> regionsMap
	) {

		dac.regions.put(regionName, cuboidRegion);
		Selection selection = new Selection(cuboidRegion);
		Map<String, Object> regionMap = selection.serialize();
		regionsMap.put(regionName, regionMap);
	}


	private static void addPoly2dRegionToHashMap(
			DAC dac,
			String regionName,
			Polygonal2DRegion polygonal2DRegion,
			HashMap<String, Object> regionsMap
	) {

		dac.regions.put(regionName, polygonal2DRegion);
		Selection selection = new Selection(polygonal2DRegion);
		Map<String, Object> regionMap = selection.serialize();
		regionsMap.put(regionName, regionMap);
	}

}
