package dev.slne.surf.essentials.handler

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.api.core.util.dateTimeFormatter
import dev.slne.surf.api.paper.packet.lore.SurfPaperPacketLoreHandler
import dev.slne.surf.api.paper.packet.lore.SurfPaperPacketLorePriority
import dev.slne.surf.essentials.service.SignService
import io.papermc.paper.persistence.PersistentDataContainerView
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.apache.commons.text.WordUtils
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object SignVisualHandler : SurfPaperPacketLoreHandler {

    override val priority = SurfPaperPacketLorePriority.LAST

    override fun handleLore(
        loreToDisplay: MutableList<Component>,
        pdc: PersistentDataContainerView,
        itemStack: ItemStack
    ) {
        val signerName = pdc.get(SignService.keySignedBy, PersistentDataType.STRING) ?: return
        val signedText = pdc.get(SignService.keySignedText, PersistentDataType.STRING)
        val signedAt = pdc.get(SignService.keySignedAt, PersistentDataType.LONG) ?: return

        val signedAtFormatted = dateTimeFormatter.format(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(signedAt),
                ZoneId.systemDefault()
            )
        )

        loreToDisplay.add(Component.space())

        loreToDisplay.add(buildText {
            text("✦ ", Colors.VARIABLE_VALUE)
            text("Signiert von ".toSmallCaps(), Colors.WHITE)
            append(miniMessage.deserialize(signerName))
            text(" ✦", Colors.VARIABLE_VALUE)
        }.decoration(TextDecoration.ITALIC, false))

        loreToDisplay.add(buildText {
            text("» ", Colors.VARIABLE_VALUE)
            text("Signiert am ".toSmallCaps(), Colors.WHITE)
            variableValue(signedAtFormatted, TextDecoration.BOLD)
            text(" «", Colors.VARIABLE_VALUE)
        }.decoration(TextDecoration.ITALIC, false))

        if (signedText != null) {
            loreToDisplay.add(Component.empty())
            loreToDisplay.add(buildText {
                text("» ", Colors.VARIABLE_VALUE)
                text("Beschreibung:".toSmallCaps(), Colors.WHITE)
            }.decoration(TextDecoration.ITALIC, false))

            signedText
                .split("<br>")
                .flatMap { rawLine ->
                    WordUtils.wrap(rawLine, 50)
                        .split('\n')
                }
                .forEach { line ->
                    if (line.isBlank()) {
                        loreToDisplay.add(Component.empty())
                        return@forEach
                    }

                    loreToDisplay.add(buildText {
                        append(
                            miniMessage.deserialize(line)
                        ).colorIfAbsent(Colors.WHITE)
                    }.decoration(TextDecoration.ITALIC, false))
                }

            loreToDisplay.add(Component.empty())
        }
    }
}