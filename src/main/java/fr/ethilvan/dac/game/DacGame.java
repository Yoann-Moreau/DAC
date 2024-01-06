package fr.ethilvan.dac.game;

import com.sk89q.worldedit.entity.Player;
import org.bukkit.Material;

import java.util.HashMap;

public class DacGame {
	private HashMap<String, Material> players;


	public DacGame() {
		this.players = new HashMap<>();
	}


	public HashMap<String, Material> getPlayers() {
		return this.players;
	}

	public void setPlayers(HashMap<String, Material> players) {
		this.players = players;
	}

	public HashMap<String, Material> addPlayer(Player player, String color) {
		if (!this.players.containsKey(player.getName())) {
			this.players.put(player.getName(), convertColorToMaterial(color));
		}
		return this.players;
	}

	public HashMap<String, Material> removePlayer(Player player) {
		this.players.remove(player.getName());
		return this.players;
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
}
