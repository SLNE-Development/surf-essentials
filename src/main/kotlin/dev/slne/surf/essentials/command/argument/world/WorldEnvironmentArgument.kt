package dev.slne.surf.essentials.command.argument.world

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.World
import org.bukkit.command.CommandSender

class WorldEnvironmentArgument(nodeName: String) :
    CustomArgument<World.Environment, String>(StringArgument(nodeName), { info ->
        World.Environment.entries.firstOrNull { it.name == info.input.uppercase() }
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Das Welt-Umfeld wurde nicht gefunden.")
                }
            }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                World.Environment.entries.map { it.name.lowercase() }
            }
        )
    }
}

inline fun Argument<*>.worldEnvironmentArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    WorldEnvironmentArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandTree.worldEnvironmentArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    WorldEnvironmentArgument(nodeName).setOptional(optional).apply(block)
)