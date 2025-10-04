package dev.slne.surf.essentials.util.util

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
fun Duration.userContent(): String {
    if (this.isNegative) return "Unbegrenzt"

    var millis = this.toMillis()

    val days = millis / 86_400_000
    millis %= 86_400_000

    val hours = millis / 3_600_000
    millis %= 3_600_000

    val minutes = millis / 60_000
    millis %= 60_000

    val seconds = millis / 1000
    millis %= 1000

    val parts = mutableListOf<String>()
    if (days > 0) parts.add("$days ${if (days == 1L) "Tag" else "Tage"}")
    if (hours > 0) parts.add("$hours ${if (hours == 1L) "Stunde" else "Stunden"}")
    if (minutes > 0) parts.add("$minutes ${if (minutes == 1L) "Minute" else "Minuten"}")
    if (seconds > 0) parts.add("$seconds ${if (seconds == 1L) "Sekunde" else "Sekunden"}")
    if (parts.isEmpty() && millis > 0) parts.add("$millis Millisekunden")

    return parts.joinToString(", ")
}