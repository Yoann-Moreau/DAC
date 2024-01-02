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
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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


	public static void saveRegionToConfig(
			org.bukkit.entity.Player player,
			String regionName,
			Region region
	) {

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

		RegionManager regionsManager = container.get(BukkitAdapter.adapt(player.getWorld()));

		if (regionsManager == null) {
			player.sendMessage(Component.text("Error when retrieving world regions.", NamedTextColor.RED));
			return;
		}

		if (regionsManager.getRegions().containsKey(regionName)) {
			player.sendMessage(Component.text("A region with that name already exists.", NamedTextColor.RED));
			return;
		}

		if (region instanceof CuboidRegion cuboidRegion) {
			regionsManager.addRegion(new ProtectedCuboidRegion(regionName, cuboidRegion.getPos1(), cuboidRegion.getPos2()));
		}
		else if (region instanceof Polygonal2DRegion polygonal2DRegion) {
			regionsManager.addRegion(new ProtectedPolygonalRegion(
					regionName,
					polygonal2DRegion.getPoints(),
					polygonal2DRegion.getMinimumY(),
					polygonal2DRegion.getMaximumY()
			));
		}
	}
}
