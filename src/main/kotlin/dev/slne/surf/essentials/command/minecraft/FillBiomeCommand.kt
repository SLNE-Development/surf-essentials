package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun fillBiomeCommand() = commandTree("fillbiome") {
    withPermission(EssentialsPermissionRegistry.FILL_BIOME_COMMAND)
    anyExecutor { executor, args ->
        executor.sendText {
            appendPrefix()
            error("Dieser Befehl könnte zu Fehlern führen und wird auf manchen Plattformen nicht unterstützt.")
        }
    }
}