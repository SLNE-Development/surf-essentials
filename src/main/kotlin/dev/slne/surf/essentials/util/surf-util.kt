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
import java.time.Duration

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
    color: TextColor = Colors.SUCCESS
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
    color: TextColor = Colors.SUCCESS
) = append {
    spacer("[")
    text(text, color)
    spacer("]")
    clickRunsCommand(command)
}

fun Long.ticks() = (this / 50).toInt()
fun Duration.userContent() = when (toMillis()) {
    in Long.MIN_VALUE..-1 -> "Unbegrenzt"
    in 0..999 -> "${toMillis()} Millisekunden"
    in 1000..59999 -> String.format("%.1f Sekunden", toMillis() / 1000.0)
    in 60000..3599999 -> String.format("%.1f Minuten", toSeconds().toDouble() / 60.0)
    in 3600000..86399999 -> String.format("%.1f Stunden", toMinutes().toDouble() / 60.0)
    in 86400000..604799999 -> String.format("%.1f Tage", toHours().toDouble() / 24.0)

    else -> String.format("%.1f Wochen", toDays().toDouble() / 7.0)
}