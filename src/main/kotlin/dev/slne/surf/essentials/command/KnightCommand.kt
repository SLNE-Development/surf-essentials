package dev.slne.surf.essentials.command

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.meta
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

fun knightCommand() = commandTree("knight") {
    withPermission(EssentialsPermissionRegistry.KNIGHT_COMMAND)
    playerExecutor { player, _ ->
        plugin.launch(plugin.entityDispatcher(player)) {
            player.inventory.helmet = buildItem(Material.GOLDEN_HELMET) {
                meta {
                    addEnchant(Enchantment.AQUA_AFFINITY, 10, true)
                    addEnchant(Enchantment.RESPIRATION, 10, true)
                    addEnchant(Enchantment.PROTECTION, 30, true)
                    isUnbreakable = true
                }

                displayName {
                    variableValue("Ritterhelm")
                }
            }

            player.inventory.chestplate = buildItem(Material.GOLDEN_CHESTPLATE) {
                meta {
                    addEnchant(Enchantment.PROTECTION, 30, true)
                    isUnbreakable = true
                }

                displayName {
                    variableValue("Ritterbrustplatte")
                }
            }

            player.inventory.leggings = buildItem(Material.GOLDEN_LEGGINGS) {
                meta {
                    addEnchant(Enchantment.SWIFT_SNEAK, 10, true)
                    addEnchant(Enchantment.PROTECTION, 30, true)
                    isUnbreakable = true
                }

                displayName {
                    variableValue("Ritterhose")
                }
            }

            player.inventory.boots = buildItem(Material.GOLDEN_BOOTS) {
                meta {
                    addEnchant(Enchantment.DEPTH_STRIDER, 10, true)
                    addEnchant(Enchantment.PROTECTION, 30, true)
                    isUnbreakable = true
                }

                displayName {
                    variableValue("Ritterschuhe")
                }
            }

            player.inventory.setItem(0, buildItem(Material.GOLDEN_SWORD) {
                meta {
                    addEnchant(Enchantment.FIRE_ASPECT, 10, true)
                    addEnchant(Enchantment.SHARPNESS, 30, true)
                    addEnchant(Enchantment.LOOTING, 10, true)
                    isUnbreakable = true
                }

                displayName {
                    variableValue("Ritters' Klinge")
                }
            })

            player.sendText {
                appendPrefix()
                success("Du bist nun ein edler Ritter!")
            }
        }
    }
}