package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.Damageable

fun repairCommand() = commandTree("repair") {
    withPermission(EssentialsPermissionRegistry.REPAIR_COMMAND)
    playerExecutor { player, _ ->
        val itemInHand = player.inventory.itemInMainHand

        if (itemInHand.isEmpty) {
            player.sendText {
                appendPrefix()
                error("Du musst ein Item in der Hand halten.")
            }
            return@playerExecutor
        }

        itemInHand.editMeta(Damageable::class.java) {
            if (!it.hasDamage()) {
                player.sendText {
                    appendPrefix()
                    error("Das Item in deiner Hand ist nicht beschädigt.")
                }
                return@editMeta
            }

            it.damage = 0

            player.sendText {
                appendPrefix()
                success("Das Item wurde repariert.")
            }
        }
    }

    entitySelectorArgumentOnePlayer("player") {
        withPermission(EssentialsPermissionRegistry.REPAIR_COMMAND_OTHERS)
        anyExecutor { executor, args ->
            val player: Player by args
            val itemInHand = player.inventory.itemInMainHand

            if (itemInHand.isEmpty) {
                executor.sendText {
                    appendPrefix()
                    error("Der Spieler muss ein Item in der Hand halten.")
                }
                return@anyExecutor
            }

            itemInHand.editMeta(Damageable::class.java) {
                if (!it.hasDamage()) {
                    executor.sendText {
                        appendPrefix()
                        error("Das Item in der Hand des Spielers ist nicht beschädigt.")
                    }
                    return@editMeta
                }

                it.damage = 0

                executor.sendText {
                    appendPrefix()
                    success("Das Item wurde repariert.")
                }

                player.sendText {
                    appendPrefix()
                    success("Dein Item wurde repariert.")
                }
            }
        }
    }
}