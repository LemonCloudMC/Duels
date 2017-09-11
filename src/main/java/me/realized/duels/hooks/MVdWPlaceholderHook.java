package me.realized.duels.hooks;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import com.earth2me.essentials.User;
import me.realized.duels.Core;
import me.realized.duels.data.UserData;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MVdWPlaceholderHook extends PluginHook implements PlaceholderReplacer {

	private static final List<String> PLACEHOLDERS = Arrays.asList("duels_wins", "duels_losses", "duels_request_enabled");

	private final Core instance;

	public MVdWPlaceholderHook(Core instance) {
		super("MVdWPlaceholderAPI");
		this.instance = instance;

		if (isEnabled()) {
			for (String placeholder : PLACEHOLDERS) {
				PlaceholderAPI.registerPlaceholder(instance, placeholder, this);
			}
		}
	}

	@Override
	public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
		Player player = event.getPlayer();

		if (player == null) {
			return "Player is required.";
		}

		Optional<UserData> user = instance.getDataManager().getUser(player.getUniqueId());

		if (!user.isPresent()) {
			return "User data not found.";
		}

		UserData userData = user.get();
		switch (event.getPlaceholder()) {
			case "duels_wins":
				return String.valueOf(userData.getWins());
			case "duels_losses":
				return String.valueOf(userData.getLosses());
			case "duels_request_enabled":
				return userData.canRequest() ? "enabled" : "disabled";
		}

		return "Invalid placeholder.";
	}
}
