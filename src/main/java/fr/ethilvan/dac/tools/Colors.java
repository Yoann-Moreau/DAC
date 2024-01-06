package fr.ethilvan.dac.tools;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class Colors {

	public static Set<String> getAvailableColors() {

		HashSet<String> colors = new HashSet<>();

		colors.add("AQUA");
		colors.add("BLACK");
		colors.add("BLUE");
		colors.add("DARK_AQUA");
		colors.add("DARK_BLUE");
		colors.add("DARK_GRAY");
		colors.add("DARK_GREEN");
		colors.add("DARK_PURPLE");
		colors.add("DARK_RED");
		colors.add("GOLD");
		colors.add("GRAY");
		colors.add("GREEN");
		colors.add("LIGHT_PURPLE");
		colors.add("RED");
		colors.add("WHITE");
		colors.add("YELLOW");

		return colors;
	}


	public static Material convertColorToMaterial(String color) {
		return switch (color.toUpperCase()) {
			case "AQUA" -> Material.CYAN_WOOL;
			case "BLACK" -> Material.BLACK_WOOL;
			case "BLUE" -> Material.LIGHT_BLUE_WOOL;
			case "DARK_AQUA" -> Material.BROWN_WOOL;
			case "DARK_BLUE" -> Material.BLUE_WOOL;
			case "DARK_GRAY" -> Material.GRAY_WOOL;
			case "DARK_GREEN" -> Material.GREEN_WOOL;
			case "DARK_PURPLE" -> Material.PURPLE_WOOL;
			case "DARK_RED" -> Material.RED_WOOL;
			case "GOLD" -> Material.ORANGE_WOOL;
			case "GRAY" -> Material.LIGHT_GRAY_WOOL;
			case "GREEN" -> Material.LIME_WOOL;
			case "LIGHT_PURPLE" -> Material.PINK_WOOL;
			case "RED" -> Material.MAGENTA_WOOL;
			case "WHITE" -> Material.WHITE_WOOL;
			case "YELLOW" -> Material.YELLOW_WOOL;
			default -> Material.CHISELED_STONE_BRICKS;
		};
	}


	public static String getChatColorsListInString() {
		StringBuilder colorsString = new StringBuilder();
		int i = 0;
		for (String colorString : getAvailableColors()) {
			colorsString.append("<color:").append(colorString.toLowerCase()).append(">").append(colorString);
			if (i < getAvailableColors().size() - 1) {
				colorsString.append("<color:white>, ");
			}
			i++;
		}

		return colorsString.toString();
	}
}
