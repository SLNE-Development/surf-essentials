package dev.slne.surf.essentials.command

import com.destroystokyo.paper.profile.ProfileProperty
import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.skin.SkinData
import dev.slne.surf.essentials.util.skin.retrieveSkin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player

fun skinChangeCommand() = commandTree("skin") {
    withPermission(EssentialsPermissionRegistry.SKIN_CHANGE_COMMAND)
    stringArgument("targetName") {
        playerExecutor { player, args ->
            val targetName: String by args

            player.sendText {
                appendPrefix()
                info("Die Skin-Daten werden geladen...")
            }

            plugin.launch {
                val skinData = retrieveSkin(targetName) ?: run {
                    player.sendText {
                        appendPrefix()
                        error("Der Skin konnte nicht gefunden werden.")
                    }
                    return@launch
                }

                assignSkin(player, skinData)

                player.sendText {
                    appendPrefix()
                    success("Dein Skin wurde erfolgreich zu ")
                    variableValue(targetName)
                    success(" geändert.")
                }
            }
        }

        entitySelectorArgumentOnePlayer("target") {
            withPermission(EssentialsPermissionRegistry.SKIN_CHANGE_COMMAND_OTHERS)
            anyExecutor { executor, args ->
                val targetName: String by args
                val target: Player by args

                executor.sendText {
                    appendPrefix()
                    info("Die Skin-Daten von ")
                    variableValue(targetName)
                    info(" werden geladen...")
                }

                plugin.launch {
                    val skinData = retrieveSkin(targetName) ?: run {
                        executor.sendText {
                            appendPrefix()
                            error("Der Skin von ")
                            variableValue(targetName)
                            error(" konnte nicht gefunden werden.")
                        }
                        return@launch
                    }

                    assignSkin(target, skinData)

                    executor.sendText {
                        appendPrefix()
                        success("Der Skin von ")
                        variableValue(target.name)
                        success(" wurde geändert.")
                    }

                    target.sendText {
                        appendPrefix()
                        info("Dein Skin wurde zu ")
                        variableValue(targetName)
                        info(" geändert.")
                    }
                }
            }
        }
    }

    literalArgument("reset") {
        playerExecutor { player, _ ->
            player.sendText {
                appendPrefix()
                info("Deine Skin-Daten werden zurückgesetzt...")
            }

            plugin.launch {
                val skinData = retrieveSkin(player.name) ?: run {
                    player.sendText {
                        appendPrefix()
                        error("Deine Skin-Daten konnten nicht zurückgesetzt werden.")
                    }
                    return@launch
                }

                assignSkin(player, skinData)

                player.sendText {
                    appendPrefix()
                    success("Deine Skin-Daten wurden erfolgreich zurückgesetzt.")
                }
            }
        }

        entitySelectorArgumentOnePlayer("target") {
            withPermission(EssentialsPermissionRegistry.SKIN_CHANGE_COMMAND_OTHERS)
            anyExecutor { executor, args ->
                val target: Player by args

                executor.sendText {
                    appendPrefix()
                    info("Die Skin-Daten von ")
                    variableValue(target.name)
                    info(" werden zurückgesetzt...")
                }

                plugin.launch {
                    val skinData = retrieveSkin(target.name) ?: run {
                        executor.sendText {
                            appendPrefix()
                            error("Die Skin-Daten von ")
                            variableValue(target.name)
                            error(" konnten nicht zurückgesetzt werden.")
                        }
                        return@launch
                    }

                    assignSkin(target, skinData)

                    executor.sendText {
                        appendPrefix()
                        success("Die Skin-Daten von ")
                        variableValue(target.name)
                        success(" wurden zurückgesetzt.")
                    }

                    target.sendText {
                        appendPrefix()
                        info("Deine Skin-Daten wurden zurückgesetzt.")
                    }
                }
            }
        }
    }
}

private suspend fun assignSkin(player: Player, skin: SkinData) =
    withContext(plugin.entityDispatcher(player)) {
        val profile = player.playerProfile.apply {
            setProperty(
                ProfileProperty(
                    "textures",
                    skin.value,
                    skin.signature
                )
            )
        }
        player.playerProfile = profile
    }