package fr.ethilvan.dac.tools;

import fr.ethilvan.dac.DAC;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;


public class DacManagement {

	public static ArrayList<String> getDacNames(DAC dac) {
		ArrayList<String> dacNames = new ArrayList<>();

		ConfigurationSection config = dac.getConfig().getConfigurationSection("regions");
		if (config == null) {
			dac.getLogger().warning("Error while retrieving DAC regions.");
			return dacNames;
		}

		dacNames.addAll(config.getKeys(false));

		return dacNames;
	}
}
