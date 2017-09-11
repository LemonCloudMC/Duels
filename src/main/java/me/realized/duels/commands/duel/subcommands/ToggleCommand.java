package me.realized.duels.commands.duel.subcommands;

import me.realized.duels.commands.SubCommand;
import me.realized.duels.data.UserData;
import me.realized.duels.utilities.Helper;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ToggleCommand extends SubCommand {

	public ToggleCommand() {
		super("toggle", "toggle", "duels.toggle", "Toggle your duel requests.", 1);
	}

	@Override
	public void execute(Player sender, String[] args) {
		Optional<UserData> data = dataManager.getUser(sender.getUniqueId());

		if (!data.isPresent()) {
			Helper.pm(sender, "&c&lYour data is improperly loaded. Please try re-logging.", false);
			return;
		}

		UserData userData = data.get();

		userData.setRequestEnabled(!userData.canRequest());
		Helper.pm(sender, (userData.canRequest() ? "Toggle.enabled" : "Toggle.disabled"), true);
	}
}
