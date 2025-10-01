package dev.slne.surf.essentials.util

import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickOpensUrl
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor

fun SurfComponentBuilder.translatable(key: String, vararg args: ComponentLike) =
    append(Component.translatable(key, Style.empty(), *args))

fun SurfComponentBuilder.appendPrefixedKeyArrowLine(key: String, value: String) =
    appendNewPrefixedLine {
        appendKeyValue(key, value)
    }

fun SurfComponentBuilder.appendSpacedArrow() = append {
    spacer("» ")
}

fun SurfComponentBuilder.appendKeyValue(key: String, value: String) = append {
    variableKey(key)
    spacer(":")
    appendSpace()
    variableValue(value)
}

fun SurfComponentBuilder.appendLinkButton(
    text: String,
    link: String,
    color: TextColor = Colors.INFO
) = append {
    spacer("[")
    text(text, color)
    spacer("]")
    clickOpensUrl(link)
    hoverEvent(buildText {
        info("Klicke um folgenden Link zu öffnen:")
        appendNewline()
        info(link)
    })
}

fun SurfComponentBuilder.appendCommandButton(
    text: String,
    command: String,
    color: TextColor = Colors.INFO
) = append {
    spacer("[")
    text(text, color)
    spacer("]")
    clickRunsCommand(command)
}