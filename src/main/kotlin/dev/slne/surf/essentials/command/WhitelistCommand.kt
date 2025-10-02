package dev.slne.surf.essentials.command

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

fun whitelistCommand() = commandTree("whitelist") {
    withPermission(EssentialsPermissionRegistry.WHITELIST_COMMAND)
    literalArgument("on") {
        anyExecutor { executor, _ ->
            if (Bukkit.hasWhitelist()) {
                executor.sendText {
                    appendPrefix()
                    error("Die Whitelist ist bereits aktiviert.")
                }
                return@anyExecutor
            }

            Bukkit.setWhitelist(true)
            executor.sendText {
                appendPrefix()
                success("Die Whitelist wurde aktiviert.")
            }
        }
    }

    literalArgument("off") {
        anyExecutor { executor, _ ->
            if (!Bukkit.hasWhitelist()) {
                executor.sendText {
                    appendPrefix()
                    error("Die Whitelist ist nicht aktiviert.")
                }
                return@anyExecutor
            }

            Bukkit.setWhitelist(false)
            executor.sendText {
                appendPrefix()
                success("Die Whitelist wurde deaktiviert.")
            }
        }
    }

    literalArgument("toggle") {
        anyExecutor { executor, _ ->
            if (Bukkit.hasWhitelist()) {
                Bukkit.setWhitelist(false)
                executor.sendText {
                    appendPrefix()
                    success("Die Whitelist wurde deaktiviert.")
                }
                return@anyExecutor
            } else {
                Bukkit.setWhitelist(true)
                executor.sendText {
                    appendPrefix()
                    success("Die Whitelist wurde aktiviert.")
                }
                return@anyExecutor
            }
        }
    }

    literalArgument("status") {
        anyExecutor { executor, _ ->
            if (Bukkit.hasWhitelist()) {
                executor.sendText {
                    appendPrefix()
                    info("Die Whitelist ist aktuell ")
                    variableValue("aktiviert")
                    info(".")
                }
                return@anyExecutor
            } else {
                executor.sendText {
                    appendPrefix()
                    info("Die Whitelist ist aktuell ")
                    variableValue("deaktiviert")
                    info(".")
                }
                return@anyExecutor
            }
        }
    }

    literalArgument("player") {
        literalArgument("add") {
            stringArgument("playerName") {
                anyExecutor { executor, args ->
                    val playerName: String by args

                    plugin.launch {
                        val player = Bukkit.getOfflinePlayer(playerName)

                        if (player.isWhitelisted) {
                            executor.sendText {
                                appendPrefix()
                                error("Der Spieler ist bereits auf der Whitelist.")
                            }
                            return@launch
                        }

                        withContext(plugin.globalRegionDispatcher) {
                            player.isWhitelisted = true
                        }

                        executor.sendText {
                            appendPrefix()
                            variableValue(player.name ?: playerName)
                            success(" wurde zur Whitelist hinzugefügt.")
                        }
                    }
                }
            }
        }

        literalArgument("remove") {
            stringArgument("playerName") {
                anyExecutor { executor, args ->
                    val playerName: String by args

                    plugin.launch {
                        val player = Bukkit.getOfflinePlayer(playerName)

                        if (!player.isWhitelisted) {
                            executor.sendText {
                                appendPrefix()
                                error("Der Spieler ist nicht auf der Whitelist.")
                            }
                            return@launch
                        }

                        withContext(plugin.globalRegionDispatcher) {
                            player.isWhitelisted = false
                        }

                        executor.sendText {
                            appendPrefix()
                            variableValue(player.name ?: playerName)
                            success(" wurde von der Whitelist entfernt.")
                        }
                    }
                }
            }
        }
        literalArgument("list") {
            integerArgument("page", optional = true) {
                anyExecutor { executor, args ->
                    val page: Int? by args

                    executor.sendText {
                        appendPrefix()
                        info("Whitelist Informationen werden geladen...")
                    }

                    plugin.launch {
                        val whitelistedPlayers =
                            Bukkit.getWhitelistedPlayers().sortedByDescending { it.isOnline }

                        if (whitelistedPlayers.isEmpty()) {
                            executor.sendText {
                                appendPrefix()
                                error("Es sind keine Spieler auf der Whitelist.")
                            }
                            return@launch
                        }


                        val pagination = Pagination<OfflinePlayer> {
                            title { primary("Whitelist".toSmallCaps(), TextDecoration.BOLD) }
                            rowRenderer { row, _ ->
                                listOf(
                                    buildText {
                                        append(CommonComponents.EM_DASH)
                                        appendSpace()
                                        variableValue(row.name ?: row.uniqueId.toString())
                                        appendSpace()
                                        spacer("(")
                                        if (row.isOnline) {
                                            success("Online")
                                        } else {
                                            error("Offline")
                                        }
                                        spacer(")")
                                    }
                                )
                            }
                        }

                        executor.sendText {
                            append(pagination.renderComponent(whitelistedPlayers, page ?: 1))
                        }
                    }
                }
            }
        }
    }
}