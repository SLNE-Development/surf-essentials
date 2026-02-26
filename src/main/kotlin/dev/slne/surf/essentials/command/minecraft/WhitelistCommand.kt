package dev.slne.surf.essentials.command.minecraft

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.arguments.AsyncPlayerProfileArgument
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.bukkit.api.command.executors.anyExecutorSuspend
import dev.slne.surf.surfapi.bukkit.api.command.util.awaitAsyncPlayerProfile
import dev.slne.surf.surfapi.bukkit.api.command.util.idOrThrow
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

fun whitelistCommand() = commandTree("whitelist") {
    withPermission(EssentialsPermissionRegistry.WHITELIST_COMMAND)
    literalArgument("on") {
        anyExecutor { executor, _ ->
            if (Bukkit.hasWhitelist()) {
                executor.sendText {
                    appendErrorPrefix()
                    error("Die Whitelist ist bereits aktiviert.")
                }
                return@anyExecutor
            }

            Bukkit.setWhitelist(true)
            executor.sendText {
                appendSuccessPrefix()
                success("Die Whitelist wurde aktiviert.")
            }
        }
    }

    literalArgument("off") {
        anyExecutor { executor, _ ->
            if (!Bukkit.hasWhitelist()) {
                executor.sendText {
                    appendErrorPrefix()
                    error("Die Whitelist ist nicht aktiviert.")
                }
                return@anyExecutor
            }

            Bukkit.setWhitelist(false)
            executor.sendText {
                appendSuccessPrefix()
                success("Die Whitelist wurde deaktiviert.")
            }
        }
    }

    literalArgument("toggle") {
        anyExecutor { executor, _ ->
            if (Bukkit.hasWhitelist()) {
                Bukkit.setWhitelist(false)
                executor.sendText {
                    appendSuccessPrefix()
                    success("Die Whitelist wurde deaktiviert.")
                }
                return@anyExecutor
            } else {
                Bukkit.setWhitelist(true)
                executor.sendText {
                    appendSuccessPrefix()
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
                    appendInfoPrefix()
                    info("Die Whitelist ist aktuell ")
                    variableValue("aktiviert")
                    info(".")
                }
                return@anyExecutor
            } else {
                executor.sendText {
                    appendInfoPrefix()
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
            argument(AsyncPlayerProfileArgument("offlinePlayer")) {
                anyExecutorSuspend { executor, args ->
                    val profile = args.awaitAsyncPlayerProfile("offlinePlayer")
                    val id = profile.idOrThrow()
                    val offlinePlayer = Bukkit.getOfflinePlayer(id)

                    if (offlinePlayer.isWhitelisted) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Der Spieler ist bereits auf der Whitelist.")
                        }
                        return@anyExecutorSuspend
                    }

                    offlinePlayer.isWhitelisted = true

                    executor.sendText {
                        appendSuccessPrefix()
                        success("Der Spieler ")
                        variableValue(offlinePlayer.name ?: offlinePlayer.uniqueId.toString())
                        success(" wurde zur Whitelist hinzugefügt.")
                    }
                }
            }
        }

        literalArgument("remove") {
            argument(AsyncPlayerProfileArgument("offlinePlayer")) {
                anyExecutorSuspend { executor, args ->
                    val profile = args.awaitAsyncPlayerProfile("offlinePlayer")
                    val id = profile.idOrThrow()
                    val offlinePlayer = Bukkit.getOfflinePlayer(id)

                    if (!offlinePlayer.isWhitelisted) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Der Spieler ist nicht auf der Whitelist.")
                        }
                        return@anyExecutorSuspend
                    }

                    offlinePlayer.isWhitelisted = false

                    executor.sendText {
                        appendSuccessPrefix()
                        success("Der Spieler ")
                        variableValue(offlinePlayer.name ?: offlinePlayer.uniqueId.toString())
                        success(" wurde von der Whitelist entfernt.")
                    }
                }
            }
        }
        literalArgument("list") {
            integerArgument("page", optional = true) {
                anyExecutor { executor, args ->
                    val page: Int? by args

                    executor.sendText {
                        appendInfoPrefix()
                        info("Whitelist Informationen werden geladen...")
                    }

                    plugin.launch {
                        val whitelistedPlayers =
                            Bukkit.getWhitelistedPlayers().sortedByDescending { it.isOnline }

                        if (whitelistedPlayers.isEmpty()) {
                            executor.sendText {
                                appendErrorPrefix()
                                error("Es sind keine Spieler auf der Whitelist.")
                            }
                            return@launch
                        }


                        val pagination = Pagination<OfflinePlayer> {
                            title {
                                primary("Whitelist".toSmallCaps(), TextDecoration.BOLD)
                                spacer(" (${whitelistedPlayers.size})")
                            }
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