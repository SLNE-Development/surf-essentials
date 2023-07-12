package dev.slne.surf.essentials.utils.permission;

import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link Permissions} class defines all permissions used by the plugin.
 * <p></p>
 * It contains permission strings for various commands and groups, as well as parent permissions.
 * <p>
 * Permissions are registered automatically when the class is loaded.
 */
@UtilityClass
public class Permissions {

    /**
     * These are the permissions for the 'cheat' commands
     */
    public final String FILL_STACK_PERMISSION;
    public final String FLY_SELF_PERMISSION;
    public final String FLY_OTHER_PERMISSION;
    public final String FEED_SELF_PERMISSION;
    public final String FEED_OTHER_PERMISSION;
    public final String GOD_MODE_SELF_PERMISSION;
    public final String GOD_MODE_OTHER_PERMISSION;
    public final String HEAL_SELF_PERMISSION;
    public final String HEAL_OTHER_PERMISSION;
    public final String REPAIR_SELF_PERMISSION;
    public final String REPAIR_OTHER_PERMISSION;
    public final String SUICIDE_PERMISSION;
    public final String HURT_PERMISSION;
    public final String ANVIL_SELF_PERMISSION;
    public final String ANVIL_OTHER_PERMISSION;
    public final String CARTOGRAPHY_TABLE_SELF_PERMISSION;
    public final String CARTOGRAPHY_TABLE_OTHER_PERMISSION;
    public final String GRINDSTONE_SELF_PERMISSION;
    public final String GRINDSTONE_OTHER_PERMISSION;
    public final String LOOM_SELF_PERMISSION;
    public final String LOOM_OTHER_PERMISSION;
    public final String SMITHING_TABLE_SELF_PERMISSION;
    public final String SMITHING_TABLE_OTHER_PERMISSION;
    public final String STONECUTTER_SELF_PERMISSION;
    public final String STONECUTTER_OTHER_PERMISSION;
    public final String WORKBENCH_SELF_PERMISSION;
    public final String WORKBENCH_OTHER_PERMISSION;
    public final String HAT_SELF_PERMISSION;
    public final String HAT_OTHER_PERMISSION;
    public final String UNHAT_SELF_PERMISSION;
    public final String UNHAT_OTHER_PERMISSION;
    public final String LIGHTING_PERMISSION;
    public final String TRASH_PERMISSION_SELF;
    public final String TRASH_PERMISSION_OTHER;
    public final String SPEED_PERMISSION_SELF;
    public final String SPEED_PERMISSION_OTHER;
    public final String POSE_SELF_PERMISSION;
    public final String POSE_OTHER_PERMISSION;


    /**
     * Permission for the recreated 'minecraft' commands
     */
    public final String ATTRIBUTE_PERMISSION;
    public final String BOSSBAR_PERMISSION;
    public final String CLONE_PERMISSION;
    public final String DATAPACK_PERMISSION;
    public final String DEFAULT_GAMEMODE_PERMISSION;
    public final String DEOP_PERMISSION;
    public final String DIFFICULTY_PERMISSION;
    public final String EFFECT_PERMISSION;
    public final String ENCHANT_PERMISSION;
    public final String EXPERIENCE_PERMISSION;
    public final String FORCELOAD_PERMISSION;

    public final String GAMEMODE_CREATIVE_SELF_PERMISSION;
    public final String GAMEMODE_CREATIVE_OTHER_PERMISSION;
    public final String GAMEMODE_CREATIVE_OTHER_OFFLINE_PERMISSION;
    public final String GAMEMODE_SURVIVAL_SELF_PERMISSION;
    public final String GAMEMODE_SURVIVAL_OTHER_PERMISSION;
    public final String GAMEMODE_SURVIVAL_OTHER_OFFLINE_PERMISSION;
    public final String GAMEMODE_SPECTATOR_SELF_PERMISSION;
    public final String GAMEMODE_SPECTATOR_OTHER_PERMISSION;
    public final String GAMEMODE_SPECTATOR_OTHER_OFFLINE_PERMISSION;
    public final String GAMEMODE_ADVENTURE_SELF_PERMISSION;
    public final String GAMEMODE_ADVENTURE_OTHER_PERMISSION;
    public final String GAMEMODE_ADVENTURE_OTHER_OFFLINE_PERMISSION;

    public final String GAMERULE_PERMISSION;
    public final String GIVE_PERMISSION;
    public final String KILL_SELF_PERMISSION;
    public final String KILL_OTHER_PERMISSION;
    public final String LIST_PERMISSION;
    public final String OP_PERMISSION;
    public final String PARTICLE_PERMISSION;
    public final String TIME_PERMISSION;
    public final String WEATHER_PERMISSION;
    public final String CLEAR_SELF_PERMISSION;
    public final String CLEAR_OTHER_PERMISSION;
    public final String HELP_PERMISSION;
    public final String SEED_PERMISSION;
    public final String SET_WORLD_SPAWN_PERMISSION;
    public final String SPECTATE_LEAVE_PERMISSION;
    public final String SPECTATE_SELF_PERMISSION;
    public final String SPECTATE_OTHER_PERMISSION;
    public final String SUMMON_PERMISSION;
    public final String FILL_PERMISSION;
    public final String SET_BLOCK_PERMISSION;
    public final String RIDE_PERMISSION;
    public final String DAMAGE_PERMISSION;
    public final String WHITELIST_PERMISSION;
    public final String RELOAD_PERMISSION;
    public final String FUNCTION_PERMISSION;
    public final String FILL_BIOME_PERMISSION;
    public final String SPAWN_ARMOR_TRIMS_PERMISSION;
    public final String EXECUTE_COMMAND_PERMISSION;
    public final String SET_PLAYER_IDLE_TIMEOUT_PERMISSION;
    public final String PLAY_SOUND_PERMISSION;
    public final String ADVANCEMENT_PERMISSION;


    /**
     * Permissions for the 'general' commands
     */
    public final String ACTION_BAR_BROADCAST_PERMISSION;
    public final String ALERT_PERMISSION;
    public final String BOOK_PERMISSION;
    public final String BOOK_PERMISSION_BYPASS;
    public final String BROADCAST_WORLD_PERMISSION;
    public final String GET_POS_SELF_PERMISSION;
    public final String GET_POS_OTHER_PERMISSION;
    public final String INFO_PERMISSION;
    public final String RULE_SELF_PERMISSION;
    public final String RULE_OTHER_PERMISSION;
    public final String SPAWNER_PERMISSION;
    public final String POLL_PERMISSION;
    public final String VOTE_PERMISSION;
    public final String TITLE_BROADCAST_PERMISSION;
    public final String OFFLINE_TELEPORT_PERMISSION;
    public final String SET_ITEM_NAME_PERMISSION;
    public final String SET_ITEM_LORE_PERMISSION;
    public final String TELEPORT_BACK_PERMISSION;
    public final String TELEPORT_RANDOM_PERMISSION;
    public final String CLEAR_ITEM_SELF_PERMISSION;
    public final String CLEAR_ITEM_OTHER_PERMISSION;
    public final String INFINITY_PERMISSION;
    public final String TIMER_PERMISSION;
    public final String CHAT_CLEAR_SELF_PERMISSION;
    public final String CHAT_CLEAR_OTHER_PERMISSION;
    public final String CHAT_CLEAR_BYPASS_PERMISSION;
    public final String TELEPORT_PERMISSION;
    public final String TELEPORT_TOP_SELF_PERMISSION;
    public final String TELEPORT_TOP_OTHER_PERMISSION;
    public final String WORLD_CREATE_PERMISSION;
    public final String WORLD_CHANGE_PERMISSION;
    public final String WORLD_LOAD_PERMISSION;
    public final String WORLD_UNLOAD_PERMISSION;
    public final String WORLD_REMOVE_PERMISSION;
    public final String WORLD_GUI_PERMISSION;
    public final String WORLD_QUERY_PERMISSION;
    public final String TELEPORT_SPAWN_SELF;
    public final String TELEPORT_SPAWN_OTHER;
    public final String DEATH_LOCATION_PERMISSION_SELF;
    public final String DEATH_LOCATION_PERMISSION_OTHER;
    public final String PLAYER_HEAD_PERMISSION;
    public final String SCREAM_PERMISSION;
    public final String SCREAM_BYPASS_PERMISSION;
    public final String NEAR_PERMISSION;


    /**
     * Permission for the 'trolls'
     */
    public final String TROLL_ALL_PERMISSION;
    public final String TROLL_BOOM_PERMISSION;
    public final String TROLL_DEMO_PERMISSION;
    public final String TROLL_ILLUSIONER_PERMISSION;
    public final String TROLL_ANVIL_PERMISSION;
    public final String TROLL_VILLAGER_PERMISSION;
    public final String TROLL_WATER_PERMISSION;
    public final String TROLL_MLG_PERMISSION;
    public final String TROLL_BELL_PERMISSION;
    public final String TROLL_HEROBRINE_PERMISSION;
    public final String TROLL_CAGE_PERMISSION;
    public final String TROLL_CRASH_PERMISSION;
    public final String TROLL_FAKE_BLOCK_PERMISSION;
    public final String TROLL_FOLLOW_BLOCK_PERMISSION;

    /**
     * Announce permissions
     */
    public final String OP_ANNOUCE_PERMISSION;
    public final String GAMEMODE_ANNOUCE_PERMISSION;


    /**
     * Parent permissions
     */
    public final Permission playerPermission;
    public final Permission modPermission;


    private final PluginManager pluginManager;
    private final Permission parentPermission;


    /**
     * Registers a new {@link Permission} with the given name and optionally parent permissions.
     *
     * @param permission The name of the permission to register.
     * @param parents    The optionally parent permissions of the permission.
     * @return The name of the registered permission.
     */
    @Contract("_, _ -> param1")
    private String register(final @NotNull String permission, Permission @NotNull ... parents) {
        EssentialsUtil.sendDebug("Registering permission: " + permission);
        final var bukkitPermission = new Permission(permission);

        EssentialsUtil.sendDebug("Linking permissions: " + permission + " -> " + parentPermission.getName());
        bukkitPermission.addParent(parentPermission, true);

        for (Permission parent : parents) {
            bukkitPermission.addParent(parent, true);
        }

        pluginManager.addPermission(bukkitPermission);
        return permission;
    }

    /**
     * Registers a new parent permission with the given name and optional description.
     *
     * @param permission  The name of the permission to register.
     * @param description The optional description of the permission.
     * @return The registered parent permission.
     */
    private @NotNull Permission registerParent(@NotNull String permission, @Nullable String description) {
        final var perm = new Permission(permission, description);
        EssentialsUtil.sendDebug("Adding parent permission: " + perm.getName());
        pluginManager.addPermission(perm);
        return perm;
    }

    /**
     * Registers a new parent permission with the given name and no description.
     *
     * @param permission The name of the permission to register.
     * @return The registered parent permission.
     */
    @SuppressWarnings("unused")
    private @NotNull Permission registerParent(@NotNull String permission) {
        return registerParent(permission, null);
    }


    /*
     * Initializes all permissions by registering them with Bukkit's PluginManager.
     * This method is called automatically when the class is loaded.
     */
    static {
        EssentialsUtil.sendDebug("Initializing permissions...");
        pluginManager = SurfEssentials.getInstance().getServer().getPluginManager();

        parentPermission = registerParent("surf.essentials.*", "Allows access to all essentials commands but also alerts (e.g. gamemode change)");
        playerPermission = registerParent("surf.essentials.groups.player", "A permission set for normal players");
        modPermission = registerParent("surf.essentials.groups.mod", "A permission set for moderators");


        // 'cheat' permissions
        FILL_STACK_PERMISSION = register("surf.essentials.commands.more");
        FLY_SELF_PERMISSION = register("surf.essentials.commands.fly", modPermission);
        FLY_OTHER_PERMISSION = register("surf.essentials.commands.fly.others");
        FEED_SELF_PERMISSION = register("surf.essentials.commands.feed", modPermission);
        FEED_OTHER_PERMISSION = register("surf.essentials.commands.feed.others");
        GOD_MODE_SELF_PERMISSION = register("surf.essentials.commands.godmode", modPermission);
        GOD_MODE_OTHER_PERMISSION = register("surf.essentials.commands.godmode.others");
        HEAL_SELF_PERMISSION = register("surf.essentials.commands.heal", modPermission);
        HEAL_OTHER_PERMISSION = register("surf.essentials.commands.heal.others");
        REPAIR_SELF_PERMISSION = register("surf.essentials.commands.repair");
        REPAIR_OTHER_PERMISSION = register("surf.essentials.commands.repair.others");
        SUICIDE_PERMISSION = register("surf.essentials.commands.suicide");
        HURT_PERMISSION = register("surf.essentials.commands.hurt");
        ANVIL_SELF_PERMISSION = register("surf.essentials.commands.anvil.self", modPermission);
        ANVIL_OTHER_PERMISSION = register("surf.essentials.commands.anvil.others");
        CARTOGRAPHY_TABLE_SELF_PERMISSION = register("surf.essentials.commands.cartographytable.self", modPermission);
        CARTOGRAPHY_TABLE_OTHER_PERMISSION = register("surf.essentials.commands.cartographytable.others");
        GRINDSTONE_SELF_PERMISSION = register("surf.essentials.commands.grindstone.self", modPermission);
        GRINDSTONE_OTHER_PERMISSION = register("surf.essentials.commands.grindstone.others");
        LOOM_SELF_PERMISSION = register("surf.essentials.commands.loom.self", modPermission);
        LOOM_OTHER_PERMISSION = register("surf.essentials.commands.loom.others");
        SMITHING_TABLE_SELF_PERMISSION = register("surf.essentials.commands.smithingtable.self", modPermission);
        SMITHING_TABLE_OTHER_PERMISSION = register("surf.essentials.commands.smithingtable.others");
        STONECUTTER_SELF_PERMISSION = register("surf.essentials.commands.stonecutter.self", modPermission);
        STONECUTTER_OTHER_PERMISSION = register("surf.essentials.commands.stonecutter.others");
        WORKBENCH_SELF_PERMISSION = register("surf.essentials.commands.workbench.self", modPermission);
        WORKBENCH_OTHER_PERMISSION = register("surf.essentials.commands.workbench.others");
        HAT_SELF_PERMISSION = register("surf.essentials.commands.hat.self", playerPermission, modPermission);
        HAT_OTHER_PERMISSION = register("surf.essentials.commands.hat.others");
        UNHAT_SELF_PERMISSION = register("surf.essentials.commands.unhat.self", playerPermission, modPermission);
        UNHAT_OTHER_PERMISSION = register("surf.essentials.commands.unhat.others");
        LIGHTING_PERMISSION = register("surf.essentials.commands.lighting");
        TRASH_PERMISSION_SELF = register("surf.essentials.commands.trash.self", modPermission);
        TRASH_PERMISSION_OTHER = register("surf.essentials.commands.trash.others");
        SPEED_PERMISSION_SELF = register("surf.essentials.commands.speed.self", modPermission);
        SPEED_PERMISSION_OTHER = register("surf.essentials.commands.speed.others");
        POSE_SELF_PERMISSION = register("surf.essentials.commands.pose.self", playerPermission, modPermission);
        POSE_OTHER_PERMISSION = register("surf.essentials.commands.pose.others", modPermission);


        // 'minecraft' permissions
        ATTRIBUTE_PERMISSION = register("surf.essentials.commands.attribute");
        BOSSBAR_PERMISSION = register("surf.essentials.commands.bossbar");
        CLONE_PERMISSION = register("surf.essentials.commands.clone");
        DATAPACK_PERMISSION = register("surf.essentials.commands.datapack");
        DEFAULT_GAMEMODE_PERMISSION = register("surf.essentials.commands.defaultgamemode");
        DEOP_PERMISSION = register("surf.essentials.commands.deop");
        DIFFICULTY_PERMISSION = register("surf.essentials.commands.difficulty");
        EFFECT_PERMISSION = register("surf.essentials.commands.effect");
        ENCHANT_PERMISSION = register("surf.essentials.commands.enchant");
        EXPERIENCE_PERMISSION = register("surf.essentials.commands.experience");
        FORCELOAD_PERMISSION = register("surf.essentials.commands.forceload");
        GAMERULE_PERMISSION = register("surf.essentials.commands.gamerule");
        GIVE_PERMISSION = register("surf.essentials.commands.give");
        KILL_SELF_PERMISSION = register("surf.essentials.commands.kill.self", modPermission);
        KILL_OTHER_PERMISSION = register("surf.essentials.commands.kill.others");
        LIST_PERMISSION = register("surf.essentials.commands.list", modPermission);
        OP_PERMISSION = register("surf.essentials.commands.op");
        PARTICLE_PERMISSION = register("surf.essentials.commands.bukkitParticle");
        TIME_PERMISSION = register("surf.essentials.commands.time", modPermission);
        WEATHER_PERMISSION = register("surf.essentials.commands.weather", modPermission);
        CLEAR_SELF_PERMISSION = register("surf.essentials.commands.clear.self");
        CLEAR_OTHER_PERMISSION = register("surf.essentials.commands.clear.others");
        HELP_PERMISSION = register("surf.essentials.commands.help");
        SEED_PERMISSION = register("surf.essentials.commands.seed");
        SET_WORLD_SPAWN_PERMISSION = register("surf.essentials.commands.setworldspawn");
        SPECTATE_LEAVE_PERMISSION = register("surf.essentials.commands.spectate.leave", modPermission);
        SPECTATE_SELF_PERMISSION = register("surf.essentials.commands.spectate.self", modPermission);
        SPECTATE_OTHER_PERMISSION = register("surf.essentials.commands.spectate.others", modPermission);
        SUMMON_PERMISSION = register("surf.essentials.commands.summon");
        FILL_PERMISSION = register("surf.essentials.commands.fill");
        SET_BLOCK_PERMISSION = register("surf.essentials.commands.setblock");
        RIDE_PERMISSION = register("surf.essentials.commands.ride");
        DAMAGE_PERMISSION = register("surf.essentials.commands.damage");
        WHITELIST_PERMISSION = register("surf.essentials.commands.whitelist");
        RELOAD_PERMISSION = register("surf.essentials.commands.reload");
        FUNCTION_PERMISSION = register("surf.essentials.commands.function");
        FILL_BIOME_PERMISSION = register("surf.essentials.commands.fillBiome");
        SPAWN_ARMOR_TRIMS_PERMISSION = register("surf.essentials.commands.internal.spawn_armor_trims", modPermission);
        EXECUTE_COMMAND_PERMISSION = register("surf.essentials.commands.execute");
        SET_PLAYER_IDLE_TIMEOUT_PERMISSION = register("surf.essentials.commands.setPlayerIdleTimeout");
        PLAY_SOUND_PERMISSION = register("surf.essentials.commands.playsound");
        ADVANCEMENT_PERMISSION = register("surf.essentials.commands.advancement");

        GAMEMODE_CREATIVE_SELF_PERMISSION = register("surf.essentials.commands.gamemode.creative.self");
        GAMEMODE_CREATIVE_OTHER_PERMISSION = register("surf.essentials.commands.gamemode.creative.others");
        GAMEMODE_CREATIVE_OTHER_OFFLINE_PERMISSION = register("surf.essentials.commands.gamemode.creative.others.offline");
        GAMEMODE_SURVIVAL_SELF_PERMISSION = register("surf.essentials.commands.gamemode.survival.self", modPermission);
        GAMEMODE_SURVIVAL_OTHER_PERMISSION = register("surf.essentials.commands.gamemode.survival.others");
        GAMEMODE_SURVIVAL_OTHER_OFFLINE_PERMISSION = register("surf.essentials.commands.gamemode.survival.others.offline");
        GAMEMODE_SPECTATOR_SELF_PERMISSION = register("surf.essentials.commands.gamemode.spectator.self", modPermission);
        GAMEMODE_SPECTATOR_OTHER_PERMISSION = register("surf.essentials.commands.gamemode.spectator.others");
        GAMEMODE_SPECTATOR_OTHER_OFFLINE_PERMISSION = register("surf.essentials.commands.gamemode.spectator.others.offline");
        GAMEMODE_ADVENTURE_SELF_PERMISSION = register("surf.essentials.commands.gamemode.adventure.self", modPermission);
        GAMEMODE_ADVENTURE_OTHER_PERMISSION = register("surf.essentials.commands.gamemode.adventure.others");
        GAMEMODE_ADVENTURE_OTHER_OFFLINE_PERMISSION = register("surf.essentials.commands.gamemode.adventure.others.offline");


        // 'general' permissions
        ACTION_BAR_BROADCAST_PERMISSION = register("surf.essentials.commands.actionbarbroadcast");
        ALERT_PERMISSION = register("surf.essentials.commands.alert");
        BOOK_PERMISSION = register("surf.essentials.commands.book");
        BOOK_PERMISSION_BYPASS = register("surf.essentials.book.bypass");
        BROADCAST_WORLD_PERMISSION = register("surf.essentials.commands.broadcastworld");
        GET_POS_SELF_PERMISSION = register("surf.essentials.commands.getpos.self", playerPermission, modPermission);
        GET_POS_OTHER_PERMISSION = register("surf.essentials.commands.getpos.others", modPermission);
        INFO_PERMISSION = register("surf.essentials.commands.info");
        RULE_SELF_PERMISSION = register("surf.essentials.commands.rule.self", playerPermission, modPermission);
        RULE_OTHER_PERMISSION = register("surf.essentials.commands.rule.others", modPermission);
        SPAWNER_PERMISSION = register("surf.essentials.commands.spawner");
        POLL_PERMISSION = register("surf.essentials.commands.polls", modPermission);
        VOTE_PERMISSION = register("surf.essentials.commands.vote", playerPermission, modPermission);
        TITLE_BROADCAST_PERMISSION = register("surf.essentials.commands.titlebroadcast");
        OFFLINE_TELEPORT_PERMISSION = register("surf.essentials.commands.tpoff", modPermission);
        SET_ITEM_NAME_PERMISSION = register("surf.essentials.commands.setname", modPermission);
        SET_ITEM_LORE_PERMISSION = register("surf.essentials.commands.setlore", modPermission);
        TELEPORT_BACK_PERMISSION = register("surf.essentials.commands.back", modPermission);
        TELEPORT_RANDOM_PERMISSION = register("surf.essentials.commands.tpr", modPermission);
        CLEAR_ITEM_SELF_PERMISSION = register("surf.essentials.commands.clearitem.self");
        CLEAR_ITEM_OTHER_PERMISSION = register("surf.essentials.commands.clearitem.others");
        INFINITY_PERMISSION = register("surf.essentials.commands.infinity", modPermission);
        TIMER_PERMISSION = register("surf.essentials.commands.timer");
        CHAT_CLEAR_SELF_PERMISSION = register("surf.essentials.commands.chatclear.self", modPermission);
        CHAT_CLEAR_OTHER_PERMISSION = register("surf.essentials.commands.chatclear.others", modPermission);
        CHAT_CLEAR_BYPASS_PERMISSION = register("surf.essentials.commands.chatclear.bypass");
        TELEPORT_PERMISSION = register("surf.essentials.commands.teleport", modPermission);
        TELEPORT_TOP_SELF_PERMISSION = register("surf.essentials.commands.tptop.self", modPermission);
        TELEPORT_TOP_OTHER_PERMISSION = register("surf.essentials.commands.tptop.other", modPermission);
        WORLD_CREATE_PERMISSION = register("surf.essentials.commands.world.create");
        WORLD_CHANGE_PERMISSION = register("surf.essentials.commands.world.change", modPermission);
        WORLD_LOAD_PERMISSION = register("surf.essentials.commands.world.load");
        WORLD_UNLOAD_PERMISSION = register("surf.essentials.commands.world.unload");
        WORLD_REMOVE_PERMISSION = register("surf.essentials.commands.world.remove");
        WORLD_GUI_PERMISSION = register("surf.essentials.commands.world.gui");
        WORLD_QUERY_PERMISSION = register("surf.essentials.commands.world.query", modPermission);
        TELEPORT_SPAWN_SELF = register("surf.essentials.commands.spawntp.self", modPermission);
        TELEPORT_SPAWN_OTHER = register("surf.essentials.commands.spawntp.other", modPermission);
        DEATH_LOCATION_PERMISSION_SELF = register("surf.essentials.commands.deathlocation.self", modPermission);
        DEATH_LOCATION_PERMISSION_OTHER = register("surf.essentials.commands.deathlocation.other", modPermission);
        PLAYER_HEAD_PERMISSION = register("surf.essentials.commands.playerhead");
        SCREAM_PERMISSION = register("surf.essentials.commands.scream", playerPermission, modPermission);
        SCREAM_BYPASS_PERMISSION = register("surf.essentials.commands.scream.bypass", modPermission);
        NEAR_PERMISSION = register("surf.essentials.commands.near", playerPermission, modPermission);


        // 'troll' permissions
        TROLL_ALL_PERMISSION = register("surf.essentials.commands.trolls");
        TROLL_BOOM_PERMISSION = register("surf.essentials.commands.troll.boom");
        TROLL_DEMO_PERMISSION = register("surf.essentials.commands.troll.demo");
        TROLL_ILLUSIONER_PERMISSION = register("surf.essentials.commands.troll.illusioner");
        TROLL_ANVIL_PERMISSION = register("surf.essentials.commands.troll.anvil");
        TROLL_VILLAGER_PERMISSION = register("surf.essentials.commands.troll.villager");
        TROLL_WATER_PERMISSION = register("surf.essentials.commands.troll.water");
        TROLL_MLG_PERMISSION = register("surf.essentials.commands.troll.mlg");
        TROLL_BELL_PERMISSION = register("surf.essentials.commands.troll.bell");
        TROLL_HEROBRINE_PERMISSION = register("surf.essentials.commands.troll.herobrine");
        TROLL_CAGE_PERMISSION = register("surf.essentials.commands.troll.cage");
        TROLL_CRASH_PERMISSION = register("surf.essentials.commands.troll.crash");
        TROLL_FAKE_BLOCK_PERMISSION = register("surf.essentials.commands.troll.fakeBlock");
        TROLL_FOLLOW_BLOCK_PERMISSION = register("surf.essentials.commands.troll.followBlock");


        // 'announce' permissions
        OP_ANNOUCE_PERMISSION = register("surf.essentials.announce.op", modPermission);
        GAMEMODE_ANNOUCE_PERMISSION = register("surf.essentials.announce.gamemode", modPermission);



        parentPermission.recalculatePermissibles();
        modPermission.recalculatePermissibles();
        playerPermission.recalculatePermissibles();
    }
}
