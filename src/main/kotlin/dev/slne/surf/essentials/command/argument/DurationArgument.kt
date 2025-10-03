package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.command.CommandSender
import java.time.Duration
import java.time.temporal.ChronoUnit

class DurationArgument(nodeName: String) :
    CustomArgument<Duration, String>(StringArgument(nodeName), { info ->
        parseDuration(info.input) ?: throw CustomArgumentException.fromAdventureComponent {
            buildText {
                appendPrefix()
                error("Bitte gebe eine gültige Dauer an.")
            }
        }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                listOf(
                    "10s",
                    "30s",
                    "1m",
                    "5m",
                    "10m",
                    "30m",
                    "1h",
                    "2h",
                    "6h",
                    "12h",
                    "1d",
                    "2d",
                    "3d",
                    "1w",
                    "infinite"
                )
            }
        )
    }
}

inline fun Argument<*>.durationArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    DurationArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandTree.durationArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    DurationArgument(nodeName).setOptional(optional).apply(block)
)

private val regex = Regex("^(?:\\d+[smhdw]|infinite)$")
private fun parseDuration(input: String): Duration? {
    val match = regex.matchEntire(input.trim()) ?: return null

    if (match.value == "infinite") {
        return Duration.of(Long.MAX_VALUE, ChronoUnit.FOREVER)
    }

    val (valueStr, unit) = match.destructured
    val value = valueStr.toLongOrNull() ?: return null

    return when (unit.lowercase()) {
        "ms" -> value
        "s" -> value * 1000
        "m" -> value * 60 * 1000
        "h" -> value * 60 * 60 * 1000
        "d" -> value * 24 * 60 * 60 * 1000
        "w" -> value * 7 * 24 * 60 * 60 * 1000
        else -> null
    }?.let {
        Duration.ofMillis(it)
    }
}