package me.realized.duels.api;

import me.realized.duels.Core;
import me.realized.duels.data.UserData;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 *
 * A static API for Duels.
 *
 * @author Realized
 *
 */

public class DuelsAPI {

    private static final Core instance = Core.getInstance();

    /**
     * @param uuid - UUID of the player to get userdata.
     *
     * @return UserData of the player if exists or null.
     */
    public static Optional<UserData> getUser(UUID uuid) {
        return instance.getDataManager().getUser(uuid);
    }

    /**
     *
     * @param player - player to get userdata.
     *
     * @return UserData of the player if exists or null.
     */
    public static Optional<UserData> getUser(Player player) {
        return instance.getDataManager().getUser(player.getUniqueId());
    }

    /**
     *
     * @param player - player to check if in match.
     *
     * @return true if player is in match, false otherwise.
     */
    public static boolean isInMatch(Player player) {
        return instance.getArenaManager().isInMatch(player);
    }

    /**
     * @return version string of the plugin.
     */
    public static String getVersion() {
        return instance.getDescription().getVersion();
    }
}
