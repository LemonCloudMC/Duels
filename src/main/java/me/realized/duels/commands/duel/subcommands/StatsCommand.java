package me.realized.duels.commands.duel.subcommands;

import me.realized.duels.commands.SubCommand;
import me.realized.duels.data.UserData;
import me.realized.duels.utilities.Helper;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class StatsCommand extends SubCommand {

	public StatsCommand() {
		super("stats", "stats", "duels.stats", "View stats of players.", 1);
	}

	@Override
	public void execute(Player sender, String[] args) {
		if (args.length <= 1) {

			Optional<UserData> target = dataManager.getUser(sender.getUniqueId());
			if (target.isPresent()) {
				displayStats(sender, target.get());
			}
			return;
		}

		Optional<UserData> target = dataManager.getUser(sender.getUniqueId());

		if (!target.isPresent()) {
			Helper.pm(sender, "Errors.player-not-found", true);
			return;
		}

		displayStats(sender, target.get());
	}

	private void displayStats(Player base, UserData user) {
		String wins = String.valueOf(user.getWins());
		String losses = String.valueOf(user.getLosses());
		String requests = String.valueOf(user.canRequest() ? "enabled" : "disabled");

		Helper.pm(base, "Stats.displayed", true, "{NAME}", base.getName(), "{WINS}", wins, "{LOSSES}", losses, "{REQUESTS_ENABLED}", requests);
	}
}
