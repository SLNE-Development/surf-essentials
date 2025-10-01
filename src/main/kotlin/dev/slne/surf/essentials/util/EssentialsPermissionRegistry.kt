package dev.slne.surf.essentials.util

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object EssentialsPermissionRegistry : PermissionRegistry() {
    const val PREFIX = "surf.essentials"

    val FLY_COMMAND = create("$PREFIX.fly.command")
    val FLY_COMMAND_OTHERS = create("$PREFIX.fly.command.others")
    val GAME_MODE_SWITCHER = create("$PREFIX.gameMode.gameModeSwitcher")
    val GAME_MODE_COMMAND = create("$PREFIX.gameMode.command")
    val GAME_MODE_COMMAND_OTHERS = create("$PREFIX.gameMode.command.others")
    val ADVANCEMENT_COMMAND = create("$PREFIX.advancement.command")
    val OP_COMMAND = create("$PREFIX.op.command")
    val DEOP_COMMAND = create("$PREFIX.deop.command")
    val HEAL_COMMAND = create("$PREFIX.heal.command")
    val HEAL_COMMAND_OTHERS = create("$PREFIX.heal.command.others")
    val HAT_COMMAND = create("$PREFIX.hat.command")
    val HAT_COMMAND_OTHERS = create("$PREFIX.hat.command.others")
    val LIST_COMMAND = create("$PREFIX.list.command")
    val LIST_COMMAND_WORLD = create("$PREFIX.list.command.world")
    val TRASH_COMMAND = create("$PREFIX.trash.command")
    val TRASH_COMMAND_OTHERS = create("$PREFIX.trash.command.others")
    val SPAWN_COMMAND = create("$PREFIX.spawn.command")
    val SPAWN_COMMAND_WORLD = create("$PREFIX.spawn.command.world")
    val BACK_COMMAND = create("$PREFIX.back.command")
    val CLEAR_COMMAND = create("$PREFIX.clear.command")
    val CLEAR_COMMAND_OTHERS = create("$PREFIX.clear.command.others")
    val REPAIR_COMMAND = create("$PREFIX.repair.command")
    val REPAIR_COMMAND_OTHERS = create("$PREFIX.repair.command.others")
    val GIVE_COMMAND = create("$PREFIX.give.command")
    val CHANGESLOT_COMMAND = create("$PREFIX.changeslot.command")
    val STRIKE_COMMAND = create("$PREFIX.strike.command")
    val INFO_COMMAND = create("$PREFIX.info.command")
}