package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.api.paper.builder.buildLore
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.inventory.framework.open
import dev.slne.surf.api.paper.inventory.framework.view.*
import dev.slne.surf.api.paper.inventory.framework.view.container.dsl.blockRow
import dev.slne.surf.api.paper.inventory.framework.view.state.get
import dev.slne.surf.api.paper.inventory.framework.view.state.initialState
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

fun openEntityInventoryCommand() = commandTree("entityinv") {
    withPermission(EssentialsPermissionRegistry.ENTITY_INV_COMMAND)
    entitySelectorArgumentOneEntity("entity") {
        playerExecutor { player, arguments ->
            val entity: Entity by arguments
            val inventoryHolder = entity as? InventoryHolder

            if (inventoryHolder == null) {
                player.sendText {
                    appendErrorPrefix()
                    error("Dieses Entity hat kein Inventar.")
                }
                return@playerExecutor
            }

            entityInventoryView.open(
                player,
                mapOf("inventory" to inventoryHolder.inventory, "entity" to entity)
            )
        }
    }

    literalArgument("#near") {
        playerExecutor { player, _ ->
            val nearestEntity = player.getNearbyEntities(5.0, 2.0, 5.0)
                .sortedBy { it.location.distanceSquared(player.location) }
                .firstOrNull { it.entityId != player.entityId }

            if (nearestEntity == null) {
                player.sendText {
                    appendErrorPrefix()
                    error("Es wurde kein Entity in der Nähe gefunden.")
                }
                return@playerExecutor
            }

            val inventoryHolder = nearestEntity as? InventoryHolder

            if (inventoryHolder == null) {
                player.sendText {
                    appendErrorPrefix()
                    error("Das nächste Entity hat kein Inventar. (${nearestEntity.type.name})")
                }
                return@playerExecutor
            }

            try {
                entityInventoryView.open(
                    player,
                    mapOf("inventory" to inventoryHolder.inventory, "entity" to nearestEntity)
                )
            } catch (e: Exception) {
                player.sendText {
                    appendErrorPrefix()
                    error("Fehler beim Öffnen des Inventars des Entities. (${nearestEntity.type.name})")
                    e.message?.let {
                        hoverEvent(buildText {
                            error(it)
                        })
                        clickCopiesToClipboard(it)
                    }
                }
            }
        }
    }
}

val entityInventoryView: AbstractSurfView = surfView("Entity Inventory") {
    val inventoryState = initialState<Inventory>("inventory")
    val entityState = initialState<Entity>("entity")

    settings {
        rows(5)
        cancelAllInteractions()
    }

    containerDefaults {
        blockRow(2)
    }

    onInit {
        layout(
            "HHHHHHHHH",
            "MO   1234",
            "IIIIIIIII",
            "IIIIIIIII",
            "IIIIIIIII"
        )
    }

    onOpen {
        val name = when (val entity = entityState[this]) {
            is Player -> entity.name
            is LivingEntity -> entity.customName()
                ?.let { PlainTextComponentSerializer.plainText().serialize(it) }
                ?: entity.type.name.lowercase().replaceFirstChar { it.uppercase() }

            else -> entity.type.name.lowercase().replaceFirstChar { it.uppercase() }
        }

        modifyConfig().title("$name's Inventory")
    }

    onFirstRender {
        val inventory = inventoryState[this]
        val entity = entityState[this] as? LivingEntity
        val equipment = entity?.equipment

        layoutSlot('H') { index, builder ->
            builder.withItem(inventory.getItem(index) ?: ItemStack.empty())
        }

        layoutSlot('I') { index, builder ->
            builder.withItem(inventory.getItem(index + 9) ?: ItemStack.empty())
        }

        layoutSlot(
            'M',
            itemOrIfEmpty(equipment?.itemInMainHand, Material.WOODEN_SWORD, "Haupthand")
        )
        layoutSlot('O', itemOrIfEmpty(equipment?.itemInOffHand, Material.SHIELD, "Nebenhand"))
        layoutSlot('1', itemOrIfEmpty(equipment?.boots, Material.LEATHER_BOOTS, "Schuhe"))
        layoutSlot('2', itemOrIfEmpty(equipment?.leggings, Material.LEATHER_LEGGINGS, "Hose"))
        layoutSlot(
            '3',
            itemOrIfEmpty(equipment?.chestplate, Material.LEATHER_CHESTPLATE, "Brustpanzer")
        )
        layoutSlot('4', itemOrIfEmpty(equipment?.helmet, Material.LEATHER_HELMET, "Helm"))
    }
}


private fun itemOrIfEmpty(item: ItemStack?, type: Material, name: String) =
    item?.takeIf { !it.isEmpty } ?: emptySlot(type, name)


private fun emptySlot(type: Material, name: String) = buildItem(type) {
    displayName {
        spacer(name)
    }

    buildLore {
        line {
            error("Dieser Slot ist leer.")
        }
    }
}