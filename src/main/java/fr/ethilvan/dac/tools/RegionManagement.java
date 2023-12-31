package fr.ethilvan.dac.tools;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import fr.ethilvan.dac.DAC;

import java.util.ArrayList;
import java.util.HashMap;

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


	public static boolean saveRegion(DAC dac, String regionName, Region region) {
		// Check if region already exists
		if (dac.regions.containsKey(regionName)) {
			return false;
		}

		HashMap<String, HashMap<String, Object>> regionsMap = new HashMap<>();

		loadRegionsFromConfig(dac);

		if (region instanceof CuboidRegion cuboidRegion) {
			dac.regions.put(regionName, cuboidRegion);
			HashMap<String, Object> regionMap = new HashMap<>();
			regionMap.put("type", "cuboid");
			ArrayList<Object> pointsList = new ArrayList<>();
			pointsList.add(cuboidRegion.getPos1().toParserString());
			pointsList.add(cuboidRegion.getPos2().toParserString());
			regionMap.put("points", pointsList);
			regionsMap.put(regionName, regionMap);
		}
		else if (region instanceof Polygonal2DRegion polygonal2DRegion) {
			dac.regions.put(regionName, polygonal2DRegion);
			HashMap<String, Object> regionMap = new HashMap<>();
			regionMap.put("type", "poly");
			ArrayList<Object> pointsList = new ArrayList<>();
			polygonal2DRegion.getPoints().forEach(point -> {
				pointsList.add(point.toParserString());
			});
			regionMap.put("points", pointsList);
			regionsMap.put(regionName, regionMap);
		}

		dac.getConfig().set("regions", regionsMap);
		dac.saveConfig();

		return true;
	}


	public static Object loadRegionsFromConfig(DAC dac) {
		Object regions = dac.getConfig().getObject("regions", HashMap.class);

		if (regions == null) {
			return new HashMap<>();
		}

		dac.getLogger().info(regions.toString());
		return regions;
	}

}
