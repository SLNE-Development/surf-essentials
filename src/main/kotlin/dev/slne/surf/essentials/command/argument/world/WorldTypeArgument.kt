package dev.slne.surf.essentials.command.argument.world

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.WorldType
import org.bukkit.command.CommandSender

class WorldTypeArgument(nodeName: String) :
    CustomArgument<WorldType, String>(StringArgument(nodeName), { info ->
        WorldType.entries.firstOrNull { it.name == info.input.uppercase() }
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Der Welttyp wurde nicht gefunden.")
                }
            }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                WorldType.entries.map {
                    it.name.lowercase()
                }
            }
        )
    }
}

inline fun Argument<*>.worldTypeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    WorldTypeArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandTree.worldTypeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    WorldTypeArgument(nodeName).setOptional(optional).apply(block)
)