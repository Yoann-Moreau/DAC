package fr.ethilvan.dac.worldedit;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Selection implements ConfigurationSerializable {

	private final Region region;

	public Selection(Region region) {
		this.region = region;
	}

	public Region getRegion() {
		return this.region;
	}

	@Override
	public @NotNull Map<String, Object> serialize() {

		HashMap<String, Object> map = new HashMap<>();

		if (this.region instanceof CuboidRegion cuboidRegion) {
			map.put("type", "cuboid");

			HashMap<String, Object> pos1 = new HashMap<>();
			pos1.put("x", cuboidRegion.getPos1().getX());
			pos1.put("y", cuboidRegion.getPos1().getY());
			pos1.put("z", cuboidRegion.getPos1().getZ());
			map.put("pos1", pos1);

			HashMap<String, Object> pos2 = new HashMap<>();
			pos2.put("x", cuboidRegion.getPos2().getX());
			pos2.put("y", cuboidRegion.getPos2().getY());
			pos2.put("z", cuboidRegion.getPos2().getZ());
			map.put("pos2", pos2);
		}
		else if (this.region instanceof Polygonal2DRegion polygonal2DRegion) {
			map.put("type", "poly2d");
			map.put("minY", polygonal2DRegion.getMinimumY());
			map.put("maxY", polygonal2DRegion.getMaximumY());

			ArrayList<HashMap<String, Integer>> pointsList = new ArrayList<>();
			for (BlockVector2 point : polygonal2DRegion.getPoints()) {
				HashMap<String, Integer> pointMap = new HashMap<>();
				pointMap.put("x", point.getX());
				pointMap.put("z", point.getZ());
				pointsList.add(pointMap);
			}
			map.put("points", pointsList);
		}

		return map;
	}


	public static Selection deserialize(Map<?, ?> map) {
		BlockVector3 blockVector3 = BlockVector3.ZERO;
		if (map.get("type").equals("cuboid")) {
			HashMap<?, ?> pos1Map = (HashMap<?, ?>) map.get("pos1");
			BlockVector3 pos1 = blockVector3.add(
					(int) pos1Map.get("x"),
					(int) pos1Map.get("y"),
					(int) pos1Map.get("z")
			);
			HashMap<?, ?> pos2Map = (HashMap<?, ?>) map.get("pos2");
			BlockVector3 pos2 = blockVector3.add(
					(int) pos2Map.get("x"),
					(int) pos2Map.get("y"),
					(int) pos2Map.get("z")
			);
			return new Selection(new CuboidRegion(pos1, pos2));
		}
		else if (map.get("type").equals("poly2d")) {
			Polygonal2DRegion polygonal2DRegion = new Polygonal2DRegion();
			List<Map<?, ?>> points = (List<Map<?, ?>>) map.get("points");
			BlockVector2 blockVector2 = BlockVector2.ZERO;
			for (Map<?, ?> point : points) {
				BlockVector2 pos = blockVector2.add(
						(int) point.get("x"),
						(int) point.get("z")
				);
				polygonal2DRegion.addPoint(pos);
			}
			int minY = (int) map.get("minY");
			int maxY = (int) map.get("maxY");
			polygonal2DRegion.setMaximumY(maxY);
			polygonal2DRegion.setMinimumY(minY);
			return new Selection(polygonal2DRegion);
		}

		return new Selection(new CuboidRegion(blockVector3, blockVector3));
	}
}
