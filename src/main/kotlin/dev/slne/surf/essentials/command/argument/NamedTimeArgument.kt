package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.essentials.util.time.NamedTime
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.command.CommandSender

class NamedTimeArgument(nodeName: String) :
    CustomArgument<NamedTime, String>(StringArgument(nodeName), { info ->
        NamedTime.entries.find { it.name.equals(info.input, ignoreCase = true) }
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Die Zeit wurde nicht gefunden.")
                }
            }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                NamedTime.entries.map {
                    it.name.lowercase()
                }
            }
        )
    }
}

inline fun Argument<*>.namedTimeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    NamedTimeArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandTree.namedTimeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    NamedTimeArgument(nodeName).setOptional(optional).apply(block)
)