package fr.ethilvan.dac.tools;

import fr.ethilvan.dac.DAC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;


public class MessageManagement {

	public static void messageToSender(DAC dac, CommandSender sender, String messageKey) {
		String message = getMessageFromKey(dac, sender, messageKey);
		if (message == null) {
			return;
		}
		if (!(sender instanceof Player)) {
			dac.getLogger().warning(message);
			return;
		}
		sender.sendRichMessage(message);
	}

	public static void messageToSender(
			DAC dac,
			CommandSender sender,
			String messageKey,
			HashMap<String, String> placeholders
	) {
		String message = getMessageFromKey(dac, sender, messageKey);
		for (HashMap.Entry<String, String> entry : placeholders.entrySet()) {
			message = message.replaceAll(entry.getKey(), entry.getValue());
		}
		sender.sendRichMessage(message);
	}


	public static void commandMessageToPlayers(
			DAC dac,
			CommandSender sender,
			ArrayList<Player> players,
			String messageKey
	) {
		String message = getMessageFromKey(dac, sender, messageKey);
		if (message == null) {
			return;
		}
		for (Player player : players) {
			if (!player.isOnline()) {
				continue;
			}
			player.sendRichMessage(message);
		}
	}

	public static void commandMessageToPlayers(
			DAC dac,
			CommandSender sender,
			ArrayList<Player> players,
			String messageKey,
			HashMap<String, String> placeholders
	) {
		String message = getMessageFromKey(dac, sender, messageKey);
		if (message == null) {
			return;
		}
		for (HashMap.Entry<String, String> entry : placeholders.entrySet()) {
			message = message.replaceAll(entry.getKey(), entry.getValue());
		}
		for (Player player : players) {
			if (!player.isOnline()) {
				continue;
			}
			player.sendRichMessage(message);
		}
	}


	public static void messageToPlayer(
			DAC dac,
			Player player,
			String messageKey
	) {
		String message = getMessageFromKey(dac, player, messageKey);
		if (message == null) {
			return;
		}
		player.sendRichMessage(message);
	}


	public static void messageToPlayers(DAC dac, ArrayList<Player> players, String messageKey) {
		String message = getMessageFromKey(dac, messageKey);
		if (message == null) {
			return;
		}
		for (Player player : players) {
			if (!player.isOnline()) {
				continue;
			}
			player.sendRichMessage(message);
		}
	}


	public static String getMessageFromKey(DAC dac, String messageKey) {
		String message = dac.getDacConfig().getMessagesConfig().getString(messageKey);
		if (message == null) {
			dac.getLogger().warning("Message missing from config!");
		}
		return message;
	}

	public static String getMessageFromKey(DAC dac, CommandSender sender, String messageKey) {
		String message = dac.getDacConfig().getMessagesConfig().getString(messageKey);
		if (message == null) {
			sender.sendRichMessage("<red>Message missing from config!");
		}
		return message;
	}


	public static String replacePlaceholders(String message, HashMap<String, String> placeholders) {
		for (HashMap.Entry<String, String> entry : placeholders.entrySet()) {
			message = message.replaceAll(entry.getKey(), entry.getValue());
		}
		return message;
	}
}
