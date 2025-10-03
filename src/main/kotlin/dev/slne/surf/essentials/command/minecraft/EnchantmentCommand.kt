package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

fun enchantmentCommand() = commandTree("enchantment") {
    withAliases("enchant")
    withPermission(EssentialsPermissionRegistry.ENCHANTMENT_COMMAND)
    entitySelectorArgumentManyPlayers("players") {
        enchantmentArgument("enchantment") {
            integerArgument("level", optional = true, min = 1, max = 255) {
                anyExecutor { executor, args ->
                    val players: Collection<Player> by args
                    val enchantment: Enchantment by args
                    val level: Int? by args

                    val successfulPlayers = mutableObjectSetOf<Player>()
                    val enchantmentLevel = level ?: 1

                    players.forEach {
                        val itemInMainHand = it.inventory.itemInMainHand

                        if (itemInMainHand.isEmpty) {
                            return@forEach
                        }

                        itemInMainHand.addUnsafeEnchantment(enchantment, enchantmentLevel)
                        successfulPlayers.add(it)
                    }

                    if (!successfulPlayers.isEmpty()) {
                        executor.sendText {
                            appendPrefix()
                            success("Du hast ")
                            append(enchantment.displayName(enchantmentLevel))
                            level?.let {
                                success(" auf Stufe ")
                                variableValue(enchantmentLevel)
                            }
                            success(" an ")
                            variableValue(successfulPlayers.size.toString())
                            success(" Spieler vergeben.")
                        }
                    } else {
                        executor.sendText {
                            appendPrefix()
                            error("Es konnte keinem Spieler die Verzauberung vergeben werden.")
                        }
                    }
                }
            }
        }
    }
}