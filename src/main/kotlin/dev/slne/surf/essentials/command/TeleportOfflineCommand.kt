package dev.slne.surf.essentials.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.getLatestLocation
import dev.slne.surf.essentials.util.util.setOfflineLocation
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

fun teleportOfflineCommand() = commandTree("teleportoffline") {
    withPermission(EssentialsPermissionRegistry.TELEPORT_COMMAND_OFFLINE)
    withAliases("tpo", "tpoff")
    stringArgument("target") {
        playerExecutor { player, args ->
            val target: String by args

            player.sendText {
                appendPrefix()
                info("Teleportiere zu ")
                variableValue(target)
                spacer(" (offline)")
                info("...")
            }

            plugin.launch {
                val offlinePlayer = Bukkit.getOfflinePlayer(target)
                val offlineLocation = offlinePlayer.getLatestLocation() ?: run {
                    player.sendText {
                        appendPrefix()
                        error("Der Spieler wurde nicht gefunden.")
                    }
                    return@launch
                }

                player.teleportAsync(offlineLocation)

                player.sendText {
                    appendPrefix()
                    success("Du wurdest zu ")
                    variableValue(target)
                    spacer(" (offline)")
                    success(" teleportiert.")
                }
            }
        }

        entitySelectorArgumentOnePlayer("onlineTarget") {
            withPermission(EssentialsPermissionRegistry.TELEPORT_COMMAND_OFFLINE_OTHERS)
            anyExecutor { executor, args ->
                val onlineTarget: Player by args
                val target: String by args

                executor.sendText {
                    appendPrefix()
                    info("Teleportiere ")
                    variableValue(target)
                    spacer(" (offline)")
                    info(" zu ")
                    variableValue(onlineTarget.name)
                    info("...")
                }

                plugin.launch {
                    val offlinePlayer = Bukkit.getOfflinePlayer(target)
                    val onlineLocation = onlineTarget.location

                    offlinePlayer.setOfflineLocation(onlineLocation)

                    executor.sendText {
                        appendPrefix()
                        success("Du hast ")
                        variableValue(target)
                        spacer(" (offline)")
                        success(" zu ")
                        variableValue(onlineTarget.name)
                        success(" teleportiert.")
                    }
                }
            }
        }

        locationArgument("location") {
            withPermission(EssentialsPermissionRegistry.TELEPORT_COMMAND_OFFLINE_OTHERS)
            anyExecutor { executor, args ->
                val target: String by args
                val location: Location by args

                executor.sendText {
                    appendPrefix()
                    info("Teleportiere ")
                    variableValue(target)
                    spacer(" (offline)")
                    info(" zu ")
                    variableValue("${location.blockX} ${location.blockY} ${location.blockZ}")
                    info("...")
                }

                plugin.launch {
                    val offlinePlayer = Bukkit.getOfflinePlayer(target)
                    offlinePlayer.setOfflineLocation(location)

                    executor.sendText {
                        appendPrefix()
                        success("Du hast ")
                        variableValue(target)
                        spacer(" (offline)")
                        success(" zu ")
                        variableValue("X: ${location.blockX} Y: ${location.blockY} Z: ${location.blockZ}")
                        success(" teleportiert.")
                    }
                }
            }
        }
    }
}