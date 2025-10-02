package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.isFolia
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

fun reloadCommand() = commandTree("reload") {
    withPermission(EssentialsPermissionRegistry.RELOAD_COMMAND)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendPrefix()
            info("Der Server wird neugeladen...")
        }

        if (!Bukkit.getServer().isFolia()) {
            Bukkit.getServer().reloadData()
        } else {
            executor.sendText {
                appendPrefix()
                error(
                    "Der Server konnte nicht komplett neu geladen werden, da Folia verwendet wird.".toSmallCaps()
                )
            }
        }


        Bukkit.getServer().reloadWhitelist()
        Bukkit.getServer().reloadCommandAliases()
        Bukkit.getServer().reloadPermissions()

        executor.sendText {
            appendPrefix()
            success("Der Server wurde neu geladen.")
            appendNewPrefixedLine {
                error("Bitte beachte, das dies kein Plugin-Reload ist, da dieser nicht mehr unterstützt wird. Bei Plugin Änderungen, bitte starte den Server neu.".toSmallCaps())
            }
        }
    }
}