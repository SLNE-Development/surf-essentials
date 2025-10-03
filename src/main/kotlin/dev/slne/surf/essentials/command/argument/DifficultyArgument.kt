package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.Difficulty
import org.bukkit.command.CommandSender

class DifficultyArgument(nodeName: String) :
    CustomArgument<Difficulty, String>(StringArgument(nodeName), { info ->
        Difficulty.entries.firstOrNull { it.name == info.input.uppercase() }
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Der Schwierigkeitsgrad wurde nicht gefunden.")
                }
            }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                Difficulty.entries.map {
                    it.name.lowercase()
                }
            }
        )
    }
}

inline fun Argument<*>.difficultyArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    DifficultyArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandTree.difficultyArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    DifficultyArgument(nodeName).setOptional(optional).apply(block)
)