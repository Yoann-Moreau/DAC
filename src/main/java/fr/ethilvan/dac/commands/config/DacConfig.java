package fr.ethilvan.dac.commands.config;

import fr.ethilvan.dac.DAC;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class DacConfig {

	private final DAC dac;

	private YamlConfiguration messagesConfig;


	public DacConfig(DAC dac) {
		this.dac = dac;
		loadMessages();
	}


	public YamlConfiguration getMessagesConfig() {
		return messagesConfig;
	}

	public void setMessagesConfig(YamlConfiguration messagesConfig) {
		this.messagesConfig = messagesConfig;
	}


	public void loadMessages() {
		File file = new File(dac.getDataFolder(), "messages.yml");
		if (!file.exists()) {
			dac.saveResource("messages.yml", false);
			messagesConfig = YamlConfiguration.loadConfiguration(file);
			return;
		}
		updateMessagesConfig(file);
	}


	private void updateMessagesConfig(File file) {
		messagesConfig = YamlConfiguration.loadConfiguration(file);
		try {
			InputStream inputStream = dac.getResource("messages.yml");
			if (inputStream == null) {
				throw new IllegalStateException("messages.yml file not found!");
			}
			YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
					new InputStreamReader(inputStream, StandardCharsets.UTF_8)
			);
			messagesConfig.setDefaults(defaults);
			messagesConfig.options().copyDefaults(true);
			messagesConfig.save(file);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
