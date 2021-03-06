package me.realized.duels.hooks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.realized.duels.Core;
import me.realized.duels.configuration.MainConfig;
import org.bukkit.entity.Player;

import java.util.List;

public class WorldGuardHook extends PluginHook {

    private final MainConfig config;

    public WorldGuardHook(Core instance) {
        super("WorldGuard");
        this.config = instance.getConfiguration();
    }

    public boolean canUseDuelCommands(Player player) {
        if (!isEnabled() || !config.isDuelZoneEnabled()) {
            return true;
        }

        List<String> allowedRegions = config.getDuelZoneRegions();
        WorldGuardPlugin worldguard = (WorldGuardPlugin) getPlugin();
        ApplicableRegionSet regions = worldguard.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());

        if (regions.getRegions().isEmpty()) {
            return false;
        }

        for (ProtectedRegion region : regions.getRegions()) {
            if (allowedRegions.contains(region.getId())) {
                return true;
            }
        }

        return false;
    }
}
