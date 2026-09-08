package dev.slne.surf.essentials.command

import dev.jorel.commandapi.CommandAPIPaper
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun hatCommand() = commandTree("hat") {
    withPermission(EssentialsPermissionRegistry.HAT_COMMAND)
    playerExecutor { player, _ ->
        val itemInHand = player.inventory.itemInMainHand
        val helmet = player.inventory.helmet

        if (!player.hasPermission(EssentialsPermissionRegistry.HAT_COMMAND_BYPASS)) {
            if (helmet.isPreventArmorChange()) {
                throw CommandAPIPaper.failWithAdventureComponent(buildText {
                    appendErrorPrefix()
                    error("Du kannst deinen Hut nicht verändern!")
                })
            }
        }

        player.inventory.setHelmet(itemInHand)
        player.inventory.setItemInMainHand(helmet)

        if (itemInHand.type.isAir) {
            player.sendText {
                appendSuccessPrefix()
                success("Du hast deinen Hut entfernt.")
            }
        } else {
            player.sendText {
                appendSuccessPrefix()
                success("Du hast deinen Hut gesetzt.")
            }
        }
    }
    entitySelectorArgumentOnePlayer("player") {
        withPermission(EssentialsPermissionRegistry.HAT_COMMAND_OTHERS)
        playerExecutor { executor, args ->
            val player: Player by args
            val itemInHand = executor.inventory.itemInMainHand
            val helmet = player.inventory.helmet

            if (!executor.hasPermission(EssentialsPermissionRegistry.HAT_COMMAND_BYPASS)) {
                if (helmet.isPreventArmorChange()) {
                    throw CommandAPIPaper.failWithAdventureComponent(buildText {
                        appendErrorPrefix()
                        variableValue(player.name)
                        error("s Hut kann nicht verändert werden!")
                    })
                }
            }

            player.inventory.setHelmet(itemInHand)
            player.inventory.setItemInMainHand(helmet)

            if (itemInHand.type.isAir) {
                executor.sendText {
                    appendSuccessPrefix()
                    variableValue(player.name)
                    success("s Hut wurde entfernt.")
                }
                player.sendText {
                    appendSuccessPrefix()
                    success("Dir wurde der Hut entfernt.")
                }
            } else {
                executor.sendText {
                    appendSuccessPrefix()
                    variableValue(player.name)
                    success("s Hut wurde gesetzt.")
                }
                player.sendText {
                    appendSuccessPrefix()
                    success("Dir wurde der Hut gesetzt.")
                }
            }
        }
    }
}

@Suppress("UnstableApiUsage")
private fun ItemStack.isPreventArmorChange(): Boolean {
    val enchantments = getData(DataComponentTypes.ENCHANTMENTS) ?: return false
    return enchantments.enchantments().keys.any { it.key() == Enchantment.BINDING_CURSE.key() }
}