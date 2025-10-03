package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.showTitle
import net.kyori.adventure.text.minimessage.MiniMessage

fun broadcastCommand() = commandTree("broadcast") {
    withPermission(EssentialsPermissionRegistry.BROADCAST_COMAMND)
    literalArgument("chat") {
        greedyStringArgument("message") {
            anyExecutor { executor, args ->
                val message: String by args
                val component = MiniMessage.miniMessage().deserialize(message)

                server.broadcast(component)

                executor.sendText {
                    appendPrefix()
                    success("Die Nachricht wurde an alle Spieler gesendet.")
                }
            }
        }
    }

    literalArgument("actionbar") {
        greedyStringArgument("message") {
            anyExecutor { executor, args ->
                val message: String by args
                val component = MiniMessage.miniMessage().deserialize(message)

                forEachPlayer { it.sendActionBar(component) }

                executor.sendText {
                    appendPrefix()
                    success("Die Nachricht wurde an alle Spieler gesendet.")
                }
            }
        }
    }

    literalArgument("title") {
        textArgument("title") {
            textArgument("subtitle") {
                anyExecutor { executor, args ->
                    val title: String by args
                    val subtitle: String by args
                    val titleComponent = MiniMessage.miniMessage().deserialize(title)
                    val subtitleComponent = MiniMessage.miniMessage().deserialize(subtitle)

                    forEachPlayer {
                        it.showTitle {
                            title {
                                append(titleComponent)
                            }
                            subtitle {
                                append(subtitleComponent)
                            }

                            times {
                                fadeIn(20)
                                stay(60)
                                fadeOut(20)
                            }
                        }
                    }

                    executor.sendText {
                        appendPrefix()
                        success("Die Nachricht wurde an alle Spieler gesendet.")
                    }
                }
            }
        }
    }
}