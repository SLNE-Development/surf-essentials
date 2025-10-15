package dev.slne.surf.essentialsold.utils.abtract;

import dev.slne.surf.essentialsold.annontations.DoNotUse;
import dev.slne.surf.essentialsold.annontations.UpdateRequired;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class is used to read the {@code server.properties} file.
 *
 * @deprecated - use the built-in Bukkit methods instead. This
 * class will <b>not</b> be removed in the future, but it is
 * recommended to not use it anyway.
 */
@SuppressWarnings({"DeprecatedIsStillUsed", "unused"})
@DoNotUse("Use the built-in Bukkit methods instead")
@UpdateRequired(updateReason = "Look if paper has implemented the missing methods")
@Deprecated(forRemoval = true, since = "2.0.0")
public abstract class PropertiesUtil extends LoggingUtil {
    private static final Properties properties;

    /**
     * Gets the generator settings from the {@code server.properties} file.
     *
     * @return The generator settings
     */
    public static String properties_generatorSettings() {
        return properties.getProperty("generator-settings", "");
    }

    /**
     * Gets the op-permission-level from the {@code server.properties} file.
     *
     * @return The op-permission-level
     */
    public static int properties_opPermissionLevel() {
        return Integer.parseInt(properties.getProperty("op-permission-level", "4"));
    }

    /**
     * Gets if the nether is enabled from the {@code server.properties} file.
     *
     * @return If the nether is enabled
     */
    public static boolean properties_allowNether() {
        return Boolean.parseBoolean(properties.getProperty("allow-nether", "true"));
    }

    /**
     * Gets the level-name from the {@code server.properties} file.
     *
     * @return The level-name
     */
    public static String properties_levelName() {
        return properties.getProperty("level-name", "world");
    }

    /**
     * Gets if the enable-query is enabled from the {@code server.properties} file.
     *
     * @return If the enable-query is enabled
     */
    public static boolean properties_enableQuery() {
        return Boolean.parseBoolean(properties.getProperty("enable-query", "false"));
    }

    /**
     * Gets if the allow-flight is enabled from the {@code server.properties} file.
     *
     * @return If the allow-flight is enabled
     */
    public static boolean properties_allowFlight() {
        return Boolean.parseBoolean(properties.getProperty("allow-flight", "false"));
    }

    /**
     * Gets if the announce-player-achievements is enabled from the {@code server.properties} file.
     *
     * @return If the announce-player-achievements is enabled
     */
    public static boolean properties_announcePlayerAchievements() {
        return Boolean.parseBoolean(properties.getProperty("announce-player-achievements", "true"));
    }

    /**
     * Gets the server-port from the {@code server.properties} file.
     *
     * @return The server-port
     */
    public static int properties_serverPort() {
        return Integer.parseInt(properties.getProperty("server-port", "25565"));
    }

    /**
     * Gets the level-type from the {@code server.properties} file.
     *
     * @return The level-type
     */
    public static String properties_levelType() {
        return properties.getProperty("level-type", "DEFAULT");
    }

    /**
     * Gets if the enable-rcon is enabled from the {@code server.properties} file.
     *
     * @return If the enable-rcon is enabled
     */
    public static boolean properties_enableRcon() {
        return Boolean.parseBoolean(properties.getProperty("enable-rcon", "false"));
    }

    /**
     * Gets if the force-gamemode is enabled from the {@code server.properties} file.
     *
     * @return If the force-gamemode is enabled
     */
    public static boolean properties_forceGamemode() {
        return Boolean.parseBoolean(properties.getProperty("force-gamemode", "false"));
    }

    /**
     * Gets the level-seed from the {@code server.properties} file.
     *
     * @return The level-seed
     */
    public static String properties_levelSeed() {
        return properties.getProperty("level-seed", "");
    }

    /**
     * Gets the server-ip from the {@code server.properties} file.
     *
     * @return The server-ip
     */
    public static String properties_serverIp() {
        return properties.getProperty("server-ip", "");
    }

    /**
     * Gets the max-build-height from the {@code server.properties} file.
     *
     * @return The max-build-height
     */
    public static int properties_maxBuildHeight() {
        return Integer.parseInt(properties.getProperty("max-build-height", "320"));
    }

    /**
     * Gets if the spawn-npcs is enabled from the {@code server.properties} file.
     *
     * @return If the spawn-npcs is enabled
     */
    public static boolean properties_spawnNpcs() {
        return Boolean.parseBoolean(properties.getProperty("spawn-npcs", "true"));
    }

    /**
     * Gets if hardcore is enabled from the {@code server.properties} file.
     *
     * @return If hardcore is enabled
     */
    public static boolean properties_hardcore() {
        return Boolean.parseBoolean(properties.getProperty("hardcore", "false"));
    }

    /**
     * Gets if the snooper-enabled is enabled from the {@code server.properties} file.
     *
     * @return If the snooper-enabled is enabled
     */
    public static boolean properties_snooperEnabled() {
        return Boolean.parseBoolean(properties.getProperty("snooper-enabled", "true"));
    }

    /**
     * Gets if the online-mode is enabled from the {@code server.properties} file.
     *
     * @return If the online-mode is enabled
     */
    public static boolean properties_onlineMode() {
        return Boolean.parseBoolean(properties.getProperty("online-mode", "true"));
    }

    /**
     * Gets the resource-pack url from the {@code server.properties} file.
     *
     * @return The resource-pack url
     */
    public static String properties_resourcePack() {
        return properties.getProperty("resource-pack", "");
    }

    /**
     * Gets if pvp is enabled from the {@code server.properties} file.
     *
     * @return If pvp is enabled
     */
    public static boolean properties_pvp() {
        return Boolean.parseBoolean(properties.getProperty("pvp", "true"));
    }

    /**
     * Gets the difficulty from the {@code server.properties} file.
     *
     * @return The difficulty
     */
    public static int properties_difficulty() {
        return Integer.parseInt(properties.getProperty("difficulty", "1"));
    }

    /**
     * Gets if the enable-command-block is enabled from the {@code server.properties} file.
     *
     * @return If the enable-command-block is enabled
     */
    public static boolean properties_enableCommandBlock() {
        return Boolean.parseBoolean(properties.getProperty("enable-command-block", "false"));
    }

    /**
     * Gets the gamemode from the {@code server.properties} file.
     *
     * @return The gamemode
     */
    public static int properties_gamemode() {
        return Integer.parseInt(properties.getProperty("gamemode", "0"));
    }

    /**
     * Gets the player-idle-timeout from the {@code server.properties} file.
     *
     * @return The player-idle-timeout
     */
    public static int properties_playerIdleTimeout() {
        return Integer.parseInt(properties.getProperty("player-idle-timeout", "0"));
    }

    /**
     * Gets the max-players from the {@code server.properties} file.
     *
     * @return The max-players
     */
    public static int properties_maxPlayers() {
        return Integer.parseInt(properties.getProperty("max-players", "20"));
    }

    /**
     * Gets if spawn-monsters is enabled from the {@code server.properties} file.
     *
     * @return If spawn-monsters is enabled
     */
    public static boolean properties_spawnMonsters() {
        return Boolean.parseBoolean(properties.getProperty("spawn-monsters", "true"));
    }

    /**
     * Gets if generate-structures is enabled from the {@code server.properties} file.
     *
     * @return If generate-structures is enabled
     */
    public static boolean properties_generateStructures() {
        return Boolean.parseBoolean(properties.getProperty("generate-structures", "true"));
    }

    /**
     * Gets the view-distance from the {@code server.properties} file.
     *
     * @return The view-distance
     */
    public static int properties_viewDistance() {
        return Integer.parseInt(properties.getProperty("view-distance", "10"));
    }

    /**
     * Gets the spawn-protection from the {@code server.properties} file.
     *
     * @return The spawn-protection
     */
    public static int properties_spawnProtection() {
        return Integer.parseInt(properties.getProperty("spawn-protection", "16"));
    }

    /**
     * Gets the motd from the {@code server.properties} file.
     *
     * @return The motd
     */
    public static String properties_motd() {
        return properties.getProperty("motd", "A Minecraft Server");
    }


    static {
        properties = new Properties();

        try {
            properties.load(new FileInputStream("server.properties"));
        } catch (IOException e) {
            throw new Error("Unable to read server.properties");
        }
    }
}
