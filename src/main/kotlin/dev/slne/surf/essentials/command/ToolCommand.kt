package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

fun toolCommand() = commandTree("tool") {
    withPermission(EssentialsPermissionRegistry.TOOL_COMMAND)

    literalArgument("repair") {
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
    }

    literalArgument("upgrade") {
        withPermission(EssentialsPermissionRegistry.TOOL_COMMAND_UPGRADE)
        playerExecutor { player, _ ->
            val itemInHand = player.inventory.itemInMainHand

            if (itemInHand.type.isAir) {
                player.sendText {
                    appendPrefix()
                    error("Du musst ein Item in der Hand halten.")
                }
                return@playerExecutor
            }

            val upgradedItem = upgradeItem(itemInHand) ?: run {
                player.sendText {
                    appendPrefix()
                    error("Dieses Item kann nicht verbessert werden.")
                }
                return@playerExecutor
            }

            player.inventory.setItemInMainHand(upgradedItem)

            player.sendText {
                appendPrefix()
                success("Das Item wurde verbessert.")
            }
        }
    }

    literalArgument("downgrade") {
        withPermission(EssentialsPermissionRegistry.TOOL_COMMAND_DOWNGRADE)
        playerExecutor { player, _ ->
            val itemInHand = player.inventory.itemInMainHand

            if (itemInHand.type.isAir) {
                player.sendText {
                    appendPrefix()
                    error("Du musst ein Item in der Hand halten.")
                }
                return@playerExecutor
            }

            val downgradedItem = downgradeItem(itemInHand) ?: run {
                player.sendText {
                    appendPrefix()
                    error("Dieses Item kann nicht verschlechtert werden.")
                }
                return@playerExecutor
            }

            player.inventory.setItemInMainHand(downgradedItem)

            player.sendText {
                appendPrefix()
                success("Das Item wurde verschlechtert.")
            }
        }
    }
}

private fun upgradeItem(item: ItemStack) = when (item.type) {
    Material.IRON_INGOT -> item.withType(Material.GOLD_INGOT)
    Material.GOLD_INGOT -> item.withType(Material.DIAMOND)
    Material.DIAMOND -> item.withType(Material.NETHERITE_INGOT)

    Material.WOODEN_SWORD -> item.withType(Material.STONE_SWORD)
    Material.STONE_SWORD -> item.withType(Material.IRON_SWORD)
    Material.IRON_SWORD -> item.withType(Material.GOLDEN_SWORD)
    Material.GOLDEN_SWORD -> item.withType(Material.DIAMOND_SWORD)
    Material.DIAMOND_SWORD -> item.withType(Material.NETHERITE_SWORD)

    Material.WOODEN_PICKAXE -> item.withType(Material.STONE_PICKAXE)
    Material.STONE_PICKAXE -> item.withType(Material.IRON_PICKAXE)
    Material.IRON_PICKAXE -> item.withType(Material.GOLDEN_PICKAXE)
    Material.GOLDEN_PICKAXE -> item.withType(Material.DIAMOND_PICKAXE)
    Material.DIAMOND_PICKAXE -> item.withType(Material.NETHERITE_PICKAXE)

    Material.WOODEN_AXE -> item.withType(Material.STONE_AXE)
    Material.STONE_AXE -> item.withType(Material.IRON_AXE)
    Material.IRON_AXE -> item.withType(Material.GOLDEN_AXE)
    Material.GOLDEN_AXE -> item.withType(Material.DIAMOND_AXE)
    Material.DIAMOND_AXE -> item.withType(Material.NETHERITE_AXE)

    Material.WOODEN_SHOVEL -> item.withType(Material.STONE_SHOVEL)
    Material.STONE_SHOVEL -> item.withType(Material.IRON_SHOVEL)
    Material.IRON_SHOVEL -> item.withType(Material.GOLDEN_SHOVEL)
    Material.GOLDEN_SHOVEL -> item.withType(Material.DIAMOND_SHOVEL)
    Material.DIAMOND_SHOVEL -> item.withType(Material.NETHERITE_SHOVEL)

    Material.WOODEN_HOE -> item.withType(Material.STONE_HOE)
    Material.STONE_HOE -> item.withType(Material.IRON_HOE)
    Material.IRON_HOE -> item.withType(Material.GOLDEN_HOE)
    Material.GOLDEN_HOE -> item.withType(Material.DIAMOND_HOE)
    Material.DIAMOND_HOE -> item.withType(Material.NETHERITE_HOE)

    Material.LEATHER_HELMET -> item.withType(Material.CHAINMAIL_HELMET)
    Material.CHAINMAIL_HELMET -> item.withType(Material.IRON_HELMET)
    Material.IRON_HELMET -> item.withType(Material.GOLDEN_HELMET)
    Material.GOLDEN_HELMET -> item.withType(Material.DIAMOND_HELMET)
    Material.DIAMOND_HELMET -> item.withType(Material.NETHERITE_HELMET)

    Material.LEATHER_CHESTPLATE -> item.withType(Material.CHAINMAIL_CHESTPLATE)
    Material.CHAINMAIL_CHESTPLATE -> item.withType(Material.IRON_CHESTPLATE)
    Material.IRON_CHESTPLATE -> item.withType(Material.GOLDEN_CHESTPLATE)
    Material.GOLDEN_CHESTPLATE -> item.withType(Material.DIAMOND_CHESTPLATE)
    Material.DIAMOND_CHESTPLATE -> item.withType(Material.NETHERITE_CHESTPLATE)

    Material.LEATHER_LEGGINGS -> item.withType(Material.CHAINMAIL_LEGGINGS)
    Material.CHAINMAIL_LEGGINGS -> item.withType(Material.IRON_LEGGINGS)
    Material.IRON_LEGGINGS -> item.withType(Material.GOLDEN_LEGGINGS)
    Material.GOLDEN_LEGGINGS -> item.withType(Material.DIAMOND_LEGGINGS)
    Material.DIAMOND_LEGGINGS -> item.withType(Material.NETHERITE_LEGGINGS)

    Material.LEATHER_BOOTS -> item.withType(Material.CHAINMAIL_BOOTS)
    Material.CHAINMAIL_BOOTS -> item.withType(Material.IRON_BOOTS)
    Material.IRON_BOOTS -> item.withType(Material.GOLDEN_BOOTS)
    Material.GOLDEN_BOOTS -> item.withType(Material.DIAMOND_BOOTS)
    Material.DIAMOND_BOOTS -> item.withType(Material.NETHERITE_BOOTS)

    Material.BUCKET -> item.withType(Material.WATER_BUCKET)
    Material.WATER_BUCKET -> item.withType(Material.LAVA_BUCKET)
    Material.LAVA_BUCKET -> item.withType(Material.MILK_BUCKET)
    else -> null
}

private fun downgradeItem(item: ItemStack) = when (item.type) {
    Material.NETHERITE_INGOT -> item.withType(Material.DIAMOND)
    Material.DIAMOND -> item.withType(Material.GOLD_INGOT)
    Material.GOLD_INGOT -> item.withType(Material.IRON_INGOT)
    Material.NETHERITE_SWORD -> item.withType(Material.DIAMOND_SWORD)
    Material.DIAMOND_SWORD -> item.withType(Material.GOLDEN_SWORD)
    Material.GOLDEN_SWORD -> item.withType(Material.IRON_SWORD)
    Material.IRON_SWORD -> item.withType(Material.STONE_SWORD)
    Material.STONE_SWORD -> item.withType(Material.WOODEN_SWORD)

    Material.NETHERITE_PICKAXE -> item.withType(Material.DIAMOND_PICKAXE)
    Material.DIAMOND_PICKAXE -> item.withType(Material.GOLDEN_PICKAXE)
    Material.GOLDEN_PICKAXE -> item.withType(Material.IRON_PICKAXE)
    Material.IRON_PICKAXE -> item.withType(Material.STONE_PICKAXE)
    Material.STONE_PICKAXE -> item.withType(Material.WOODEN_PICKAXE)

    Material.NETHERITE_AXE -> item.withType(Material.DIAMOND_AXE)
    Material.DIAMOND_AXE -> item.withType(Material.GOLDEN_AXE)
    Material.GOLDEN_AXE -> item.withType(Material.IRON_AXE)
    Material.IRON_AXE -> item.withType(Material.STONE_AXE)
    Material.STONE_AXE -> item.withType(Material.WOODEN_AXE)

    Material.NETHERITE_SHOVEL -> item.withType(Material.DIAMOND_SHOVEL)
    Material.DIAMOND_SHOVEL -> item.withType(Material.GOLDEN_SHOVEL)
    Material.GOLDEN_SHOVEL -> item.withType(Material.IRON_SHOVEL)
    Material.IRON_SHOVEL -> item.withType(Material.STONE_SHOVEL)
    Material.STONE_SHOVEL -> item.withType(Material.WOODEN_SHOVEL)

    Material.NETHERITE_HOE -> item.withType(Material.DIAMOND_HOE)
    Material.DIAMOND_HOE -> item.withType(Material.GOLDEN_HOE)
    Material.GOLDEN_HOE -> item.withType(Material.IRON_HOE)
    Material.IRON_HOE -> item.withType(Material.STONE_HOE)
    Material.STONE_HOE -> item.withType(Material.WOODEN_HOE)

    Material.NETHERITE_HELMET -> item.withType(Material.DIAMOND_HELMET)
    Material.DIAMOND_HELMET -> item.withType(Material.GOLDEN_HELMET)
    Material.GOLDEN_HELMET -> item.withType(Material.IRON_HELMET)
    Material.IRON_HELMET -> item.withType(Material.CHAINMAIL_HELMET)
    Material.CHAINMAIL_HELMET -> item.withType(Material.LEATHER_HELMET)

    Material.NETHERITE_CHESTPLATE -> item.withType(Material.DIAMOND_CHESTPLATE)
    Material.DIAMOND_CHESTPLATE -> item.withType(Material.GOLDEN_CHESTPLATE)
    Material.GOLDEN_CHESTPLATE -> item.withType(Material.IRON_CHESTPLATE)
    Material.IRON_CHESTPLATE -> item.withType(Material.CHAINMAIL_CHESTPLATE)
    Material.CHAINMAIL_CHESTPLATE -> item.withType(Material.LEATHER_CHESTPLATE)

    Material.NETHERITE_LEGGINGS -> item.withType(Material.DIAMOND_LEGGINGS)
    Material.DIAMOND_LEGGINGS -> item.withType(Material.GOLDEN_LEGGINGS)
    Material.GOLDEN_LEGGINGS -> item.withType(Material.IRON_LEGGINGS)
    Material.IRON_LEGGINGS -> item.withType(Material.CHAINMAIL_LEGGINGS)
    Material.CHAINMAIL_LEGGINGS -> item.withType(Material.LEATHER_LEGGINGS)

    Material.NETHERITE_BOOTS -> item.withType(Material.DIAMOND_BOOTS)
    Material.DIAMOND_BOOTS -> item.withType(Material.GOLDEN_BOOTS)
    Material.GOLDEN_BOOTS -> item.withType(Material.IRON_BOOTS)
    Material.IRON_BOOTS -> item.withType(Material.CHAINMAIL_BOOTS)
    Material.CHAINMAIL_BOOTS -> item.withType(Material.LEATHER_BOOTS)

    Material.MILK_BUCKET -> item.withType(Material.LAVA_BUCKET)
    Material.LAVA_BUCKET -> item.withType(Material.WATER_BUCKET)
    Material.WATER_BUCKET -> item.withType(Material.BUCKET)
    else -> null
}