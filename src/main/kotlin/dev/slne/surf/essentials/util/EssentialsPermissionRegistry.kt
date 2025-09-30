package dev.slne.surf.essentials.util

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object EssentialsPermissionRegistry : PermissionRegistry() {
    const val PREFIX = "surf.essentials"

    val FLY_COMMAND = create("$PREFIX.fly.command")
    val GAME_MODE_COMMAND = create("$PREFIX.gameMode.command")
    val GAME_MODE_SWITCHER = create("$PREFIX.gameMode.switcher")
    val ADVANCEMENT_COMMAND = create("$PREFIX.advancement.command")
    val OP_COMMAND = create("$PREFIX.op.command")
    val DEOP_COMMAND = create("$PREFIX.deop.command")
}