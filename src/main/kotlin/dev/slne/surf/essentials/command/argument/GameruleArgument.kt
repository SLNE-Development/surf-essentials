package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.GameRule
import org.bukkit.command.CommandSender

class GameruleArgument(nodeName: String) :
    CustomArgument<GameRule<*>, String>(StringArgument(nodeName), { info ->
        GameRule.getByName(info.input) ?: throw CustomArgumentException.fromAdventureComponent {
            buildText {
                appendPrefix()
                error("Die Spielregel wurde nicht gefunden.")
            }
        }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                GameRule.values().map { it.name }
            }
        )
    }
}

inline fun CommandTree.gameruleArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    GameruleArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.gameruleArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    GameruleArgument(nodeName).setOptional(optional).apply(block)
)