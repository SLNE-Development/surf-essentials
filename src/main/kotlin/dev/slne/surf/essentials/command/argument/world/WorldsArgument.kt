package dev.slne.surf.essentials.command.argument.world

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.CommandSender

class WorldsArgument(nodeName: String) :
    CustomArgument<World, String>(StringArgument(nodeName), { info ->
        Bukkit.getWorld(info.input) ?: throw CustomArgumentException.fromAdventureComponent {
            buildText {
                appendPrefix()
                error("Die Welt wurde nicht gefunden.")
            }
        }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                Bukkit.getWorlds().map { it.name }
            }
        )
    }
}

inline fun CommandTree.worldsArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    WorldsArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.worldsArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    WorldsArgument(nodeName).setOptional(optional).apply(block)
)