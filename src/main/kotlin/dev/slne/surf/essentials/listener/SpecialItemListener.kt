package dev.slne.surf.essentials.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.service.settingsService
import dev.slne.surf.essentials.service.specialItemService
import dev.slne.surf.essentials.util.util.appendNewLineArrow
import dev.slne.surf.essentials.util.util.translatable
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.inventory.meta.ItemMeta
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object SpecialItemListener : Listener {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm")

    @EventHandler
    fun onPickup(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return
        val itemStack = event.item.itemStack

        if (!specialItemService.isSpecial(itemStack)) {
            return
        }

        if (specialItemService.isAnnounced(itemStack)) {
            return
        }

        specialItemService.markAsAnnounced(itemStack)
        specialItemService.setSpecializedDate(itemStack, System.currentTimeMillis())

        itemStack.editMeta(ItemMeta::class.java) {
            val lore = it.lore() ?: mutableListOf<Component>()

            lore.addLast(buildText {
                appendNewLineArrow()
                text("Gefunden von ".toSmallCaps(), Colors.WHITE)
                variableValue(player.name.toSmallCaps(), TextDecoration.BOLD)
            }.decoration(TextDecoration.ITALIC, false))

            lore.addLast(buildText {
                appendNewLineArrow()
                text("Gefunden am ".toSmallCaps(), Colors.WHITE)
                variableValue(ZonedDateTime.now().format(dateTimeFormatter))
            }.decoration(TextDecoration.ITALIC, false))

            it.lore(lore)
        }

        forEachPlayer {
            it.sendText {
                appendPrefix()
                variableValue(player.name)
                success(" hat ")

                append {
                    translatable(itemStack.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
                    hoverEvent(itemStack.asHoverEvent())
                }
                success(" erhalten!")
            }

            plugin.launch {
                if (plugin.hasSettingsApi()) {
                    if (settingsService.hasSoundsEnabled(it.uniqueId)) {
                        it.playSound(sound {
                            type(Sound.ENTITY_ENDER_DRAGON_GROWL)
                        }, net.kyori.adventure.sound.Sound.Emitter.self())
                    }
                } else {
                    it.playSound(sound {
                        type(Sound.ENTITY_ENDER_DRAGON_GROWL)
                    }, net.kyori.adventure.sound.Sound.Emitter.self())
                }
            }
        }
    }
}