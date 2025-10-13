package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun fillBiomeCommand() = commandTree("fillbiome") {
    withPermission(EssentialsPermissionRegistry.FILL_BIOME_COMMAND)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendPrefix()
            error("Dieser Befehl wurde deaktiviert, da er auf manchen Plattformen zu Fehlern führen könnte. Wenn du ihn wirklich benötigst, nutze /minecraft:fillbiome.")
        }
    }
}