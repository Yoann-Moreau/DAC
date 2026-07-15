package fr.ethilvan.dac.tools;

import org.bukkit.Material;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;


public enum DacColor {

	AQUA(Material.CYAN_WOOL),
	BLACK(Material.BLACK_WOOL),
	BLUE(Material.BLUE_WOOL),
	DARK_BLUE(Material.BLUE_WOOL),
	DARK_GRAY(Material.GRAY_WOOL),
	DARK_GREEN(Material.GREEN_WOOL),
	DARK_PURPLE(Material.PURPLE_WOOL),
	DARK_RED(Material.RED_WOOL),
	GOLD(Material.ORANGE_WOOL),
	GRAY(Material.LIGHT_GRAY_WOOL),
	GREEN(Material.LIME_WOOL),
	LIGHT_PURPLE(Material.PINK_WOOL),
	RED(Material.MAGENTA_WOOL),
	WHITE(Material.WHITE_WOOL),
	YELLOW(Material.YELLOW_WOOL);


	private final Material material;


	DacColor(Material material) {
		this.material = material;
	}


	public Material getMaterial() {
		return this.material;
	}


	public static Set<DacColor> getAvailableColors() {
		return new HashSet<>(EnumSet.allOf(DacColor.class));
	}


	public static String getChatColorsListInString() {
		StringBuilder colorsString = new StringBuilder();
		int i = 0;
		for (DacColor dacColor : getAvailableColors()) {
			String colorString = dacColor.name();
			colorsString.append("<color:").append(colorString.toLowerCase()).append(">").append(colorString);
			if (i < getAvailableColors().size() - 1) {
				colorsString.append("<color:white>, ");
			}
			i++;
		}

		return colorsString.toString();
	}
}
