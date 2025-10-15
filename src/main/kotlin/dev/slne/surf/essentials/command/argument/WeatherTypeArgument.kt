package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.essentials.util.weather.WeatherType
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.command.CommandSender

class WeatherTypeArgument(nodeName: String) :
    CustomArgument<WeatherType, String>(StringArgument(nodeName), { info ->
        WeatherType.entries.firstOrNull { it.name == info.input.uppercase() }
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Der Wettertyp wurde nicht gefunden.")
                }
            }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                WeatherType.entries.map {
                    it.name.lowercase()
                }
            }
        )
    }
}

inline fun Argument<*>.weatherTypeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    WeatherTypeArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandTree.weatherTypeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    WeatherTypeArgument(nodeName).setOptional(optional).apply(block)
)