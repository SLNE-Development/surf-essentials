package dev.slne.surf.essentials.utils.permission;

public interface Permissions {

    /**
     * These are the permissions for the 'cheat' commands
     */
    String FILL_STACK_PERMISSION = "surf.essentials.commands.more";
    String FLY_SELF_PERMISSION = "surf.essentials.commands.fly";
    String FLY_OTHER_PERMISSION = "surf.essentials.commands.fly.others";
    String FEED_SELF_PERMISSION = "surf.essentials.commands.feed";
    String FEED_OTHER_PERMISSION = "surf.essentials.commands.feed.others";
    String GOD_MODE_SELF_PERMISSION = "surf.essentials.commands.godmode";
    String GOD_MODE_OTHER_PERMISSION = "surf.essentials.commands.godmode.others";
    String HEAL_SELF_PERMISSION = "surf.essentials.commands.heal";
    String HEAL_OTHER_PERMISSION = "surf.essentials.commands.heal.others";
    String REPAIR_SELF_PERMISSION = "surf.essentials.commands.repair";
    String REPAIR_OTHER_PERMISSION = "surf.essentials.commands.repair.others";
    String SUICIDE_PERMISSION = "surf.essentials.commands.suicide";
    String HURT_PERMISSION = "surf.essentials.commands.hurt";
    String ANVIL_SELF_PERMISSION = "surf.essentials.commands.anvil";
    String ANVIL_OTHER_PERMISSION = "surf.essentials.commands.anvil.others";
    String CARTOGRAPHY_TABLE_SELF_PERMISSION = "surf.essentials.commands.cartographytable";
    String CARTOGRAPHY_TABLE_OTHER_PERMISSION = "surf.essentials.commands.cartographytable.others";
    String GRINDSTONE_SELF_PERMISSION = "surf.essentials.commands.grindstone";
    String GRINDSTONE_OTHER_PERMISSION = "surf.essentials.commands.grindstone.others";
    String LOOM_SELF_PERMISSION = "surf.essentials.commands.loom";
    String LOOM_OTHER_PERMISSION = "surf.essentials.commands.loom.others";
    String SMITHING_TABLE_SELF_PERMISSION = "surf.essentials.commands.smithingtable";
    String SMITHING_TABLE_OTHER_PERMISSION = "surf.essentials.commands.smithingtable.others";
    String STONECUTTER_SELF_PERMISSION = "surf.essentials.commands.stonecutter";
    String STONECUTTER_OTHER_PERMISSION = "surf.essentials.commands.stonecutter.others";
    String WORKBENCH_SELF_PERMISSION = "surf.essentials.commands.workbench";
    String WORKBENCH_OTHER_PERMISSION = "surf.essentials.commands.workbench.others";
    String HAT_SELF_PERMISSION = "surf.essentials.commands.hat";
    String HAT_OTHER_PERMISSION = "surf.essentials.commands.hat.others";
    String UNHAT_SELF_PERMISSION = "surf.essentials.commands.unhat";
    String UNHAT_OTHER_PERMISSION = "surf.essentials.commands.unhat.others";
    String LIGHTING_PERMISSION = "surf.essentials.commands.lighting";
    String TRASH_PERMISSION_SELF = "surf.essentials.commands.trash.self";
    String TRASH_PERMISSION_OTHER = "surf.essentials.commands.trash.others";
    String SPEED_PERMISSION_SELF = "surf.essentials.commands.speed.self";
    String SPEED_PERMISSION_OTHER = "surf.essentials.commands.speed.others";


    /**
     * Permission for the recreated 'minecraft' commands
     */
    String BOSSBAR_PERMISSION = "surf.essentials.commands.bossbar";
    String DEFAULT_GAMEMODE_PERMISSION = "surf.essentials.commands.defaultgamemode";
    String DEOP_PERMISSION = "surf.essentials.commands.deop";
    String DIFFICULTY_PERMISSION = "surf.essentials.commands.difficulty";
    String EFFECT_PERMISSION = "surf.essentials.commands.effect";
    String ENCHANT_PERMISSION = "surf.essentials.commands.enchant";
    String EXPERIENCE_PERMISSION = "surf.essentials.commands.experience";
    String FORCELOAD_PERMISSION = "surf.essentials.commands.forceload";

    String GAMEMODE_CREATIVE_SELF_PERMISSION = "surf.essentials.commands.gamemode.creative";
    String GAMEMODE_CREATIVE_OTHER_PERMISSION = "surf.essentials.commands.gamemode.creative.others";
    String GAMEMODE_CREATIVE_OTHER_OFFLINE_PERMISSION = "surf.essentials.commands.gamemode.creative.others.offline";
    String GAMEMODE_SURVIVAL_SELF_PERMISSION = "surf.essentials.commands.gamemode.survival";
    String GAMEMODE_SURVIVAL_OTHER_PERMISSION = "surf.essentials.commands.gamemode.survival.others";
    String GAMEMODE_SURVIVAL_OTHER_OFFLINE_PERMISSION = "surf.essentials.commands.gamemode.survival.others.offline";
    String GAMEMODE_SPECTATOR_SELF_PERMISSION = "surf.essentials.commands.gamemode.spectator";
    String GAMEMODE_SPECTATOR_OTHER_PERMISSION = "surf.essentials.commands.gamemode.spectator.others";
    String GAMEMODE_SPECTATOR_OTHER_OFFLINE_PERMISSION = "surf.essentials.commands.gamemode.spectator.others.offline";
    String GAMEMODE_ADVENTURE_SELF_PERMISSION = "surf.essentials.commands.gamemode.adventure";
    String GAMEMODE_ADVENTURE_OTHER_PERMISSION = "surf.essentials.commands.gamemode.adventure.others";
    String GAMEMODE_ADVENTURE_OTHER_OFFLINE_PERMISSION = "surf.essentials.commands.gamemode.adventure.others.offline";

    String GAMERULE_PERMISSION = "surf.essentials.commands.gamerule";
    String GIVE_PERMISSION = "surf.essentials.commands.give";
    String KILL_SELF_PERMISSION = "surf.essentials.commands.kill";
    String KILL_OTHER_PERMISSION = "surf.essentials.commands.kill.others";
    String LIST_PERMISSION = "surf.essentials.commands.list";
    String OP_PERMISSION = "surf.essentials.commands.op";
    String PARTICLE_PERMISSION = "surf.essentials.commands.particle";
    String TIME_PERMISSION = "surf.essentials.commands.time";
    String WEATHER_PERMISSION = "surf.essentials.commands.weather";
    String CLEAR_SELF_PERMISSION = "surf.essentials.commands.clear";
    String CLEAR_OTHER_PERMISSION = "surf.essentials.commands.clear.others";
    String HELP_PERMISSION = "surf.essentials.commands.help";
    String SEED_PERMISSION = "surf.essentials.commands.seed";
    String SET_WORLD_SPAWN_PERMISSION = "surf.essentials.commands.setworldspawn";
    String SPECTATE_SELF_PERMISSION = "surf.essentials.commands.spectate";
    String SPECTATE_OTHER_PERMISSION = "surf.essentials.commands.spectate.others";
    String SUMMON_PERMISSION = "surf.essentials.commands.summon";
    String FILL_PERMISSION = "surf.essentials.commands.fill";
    String SET_BLOCK_PERMISSION = "surf.essentials.commands.setblock";
    String RIDE_PERMISSION = "surf.essentials.commands.ride";
    String DAMAGE_PERMISSION = "surf.essentials.commands.damage";


    /**
     * Permissions for the 'general' commands
     */
    String ACTION_BAR_BROADCAST_PERMISSION = "surf.essentials.commands.actionbarbroadcast";
    String ALERT_PERMISSION = "surf.essentials.commands.alert";
    String BOOK_PERMISSION = "surf.essentials.commands.book";
    String BOOK_PERMISSION_BYPASS = "surf.essentials.book.bypass";
    String BROADCAST_WORLD_PERMISSION = "surf.essentials.commands.broadcastworld";
    String GET_POS_SELF_PERMISSION = "surf.essentials.commands.getpos";
    String GET_POS_OTHER_PERMISSION = "surf.essentials.commands.getpos.others";
    String INFO_PERMISSION = "surf.essentials.commands.info";
    String RULE_SELF_PERMISSION = "surf.essentials.commands.rule";
    String RULE_OTHER_PERMISSION = "surf.essentials.commands.rule.others";
    String SPAWNER_PERMISSION = "surf.essentials.commands.spawner";
    String EDIT_SIGN_PERMISSION = "surf.essentials.listeners.sign.edit";
    String TOGGLE_SIGN_PERMISSION = "surf.essentials.commands.sign.toggle";
    String POLL_PERMISSION = "surf.essentials.commands.polls";
    String VOTE_PERMISSION = "surf.essentials.commands.vote";
    String TITLE_BROADCAST_PERMISSION = "surf.essentials.commands.titlebroadcast";
    String OFFLINE_TELEPORT_PERMISSION = "surf.essentials.commands.tpoff";
    String SET_ITEM_NAME_PERMISSION = "surf.essentials.commands.setname";
    String SET_ITEM_LORE_PERMISSION = "surf.essentials.commands.setlore";
    String TELEPORT_BACK_PERMISSION = "surf.essentials.commands.back";
    String TELEPORT_RANDOM_PERMISSION = "surf.essentials.commands.tpr";
    String CLEAR_ITEM_SELF_PERMISSION = "surf.essentials.commands.clearitem";
    String CLEAR_ITEM_OTHER_PERMISSION = "surf.essentials.commands.clearitem.others";
    String INFINITY_PERMISSION = "surf.essentials.commands.infinity";
    String TIMER_PERMISSION = "surf.essentials.commands.timer";
    String CHAT_CLEAR_SELF_PERMISSION = "surf.essentials.commands.chatclear";
    String CHAT_CLEAR_OTHER_PERMISSION = "surf.essentials.commands.chatclear.others";
    String CHAT_CLEAR_BYPASS_PERMISSION = "surf.essentials.commands.chatclear.bypass";
    String TELEPORT_PERMISSION = "surf.essentials.commands.teleport";
    String TELEPORT_TOP_SELF_PERMISSION = "surf.essentials.commands.tptop";
    String TELEPORT_TOP_OTHER_PERMISSION = "surf.essentials.commands.tptop.other";
    String WORLD_CREATE_PERMISSION = "surf.essentials.commands.world.create";
    String WORLD_CHANGE_PERMISSION = "surf.essentials.commands.world.change";
    String WORLD_LOAD_PERMISSION = "surf.essentials.commands.world.load";
    String WORLD_UNLOAD_PERMISSION = "surf.essentials.commands.world.unload";
    String WORLD_REMOVE_PERMISSION = "surf.essentials.commands.world.remove";
    String WORLD_GUI_PERMISSION = "surf.essentials.commands.world.gui";
    String WORLD_QUERY_PERMISSION = "surf.essentials.commands.world.query";
    String TELEPORT_SPAWN_SELF = "surf.essentials.commands.spawntp.self";
    String TELEPORT_SPAWN_OTHER = "surf.essentials.commands.spawntp.other";
    String DEATH_LOCATION_PERMISSION_SELF = "surf.essentials.commands.deathlocation.self";
    String DEATH_LOCATION_PERMISSION_OTHER = "surf.essentials.commands.deathlocation.other";
    String PLAYER_HEAD_PERMISSION = "surf.essentials.commands.playerhead";


    /**
     * Permission for the 'trolls'
     */
    String TROLL_ALL_PERMISSION = "surf.essentials.commands.trolls";
    String TROLL_BOOM_PERMISSION = "surf.essentials.commands.troll.boom";
    String TROLL_DEMO_PERMISSION = "surf.essentials.commands.troll.demo";
    String TROLL_ILLUSIONER_PERMISSION = "surf.essentials.commands.troll.illusioner";
    String TROLL_ANVIL_PERMISSION = "surf.essentials.commands.troll.anvil";
    String TROLL_VILLAGER_PERMISSION = "surf.essentials.commands.troll.villager";
    String TROLL_WATER_PERMISSION = "surf.essentials.commands.troll.water";
    String TROLL_MLG_PERMISSION = "surf.essentials.commands.troll.mlg";
    String TROLL_BELL_PERMISSION = "surf.essentials.commands.troll.bell";
    String TROLL_HEROBRINE_PERMISSION = "surf.essentials.commands.troll.herobrine";
    String TROLL_CAGE_PERMISSION = "surf.essentials.commands.troll.cage";
    String TROLL_CRASH_PERMISSION = "surf.essentials.commands.troll.crash";
}
