package dev.slne.surf.essentialsold.utils.abtract;

import dev.slne.surf.essentialsold.SurfEssentials;
import net.coreprotect.CoreProtectAPI;

/**
 * A utility class for working with other plugins
 *
 * @author twisti
 * @since 1.0.2
 */
public abstract class PluginUtil extends RandomUtil {

    /**
     * Checks whether the {@link CoreProtectAPI} is enabled.
     *
     * @return true if the {@link CoreProtectAPI} is enabled, false otherwise
     */
    public static boolean isCoreProtectEnabled() {
        return getCoreProtectAPI() != null;
    }

    /**
     * Returns an instance of the {@link CoreProtectAPI}
     *
     * @return the instance of the CoreProtectAPI
     */
    public static CoreProtectAPI getCoreProtectAPI() {
        return SurfEssentials.getCoreProtectApi();
    }

}
