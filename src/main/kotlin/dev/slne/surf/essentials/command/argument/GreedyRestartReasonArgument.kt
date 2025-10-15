package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.GreedyStringArgument
import org.bukkit.command.CommandSender

class GreedyRestartReasonArgument(nodeName: String) :
    CustomArgument<String, String>(GreedyStringArgument(nodeName), { info ->
        info.input
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                listOf(
                    "Es werden Wartungsarbeiten durchgeführt.",
                    "Der Server wird geupdatet.",
                    "Es werden Probleme behoben.",
                    "Es werden Fehler behoben."
                )
            }
        )
    }
}

inline fun Argument<*>.greedyRestartReasonArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    GreedyRestartReasonArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandTree.greedyRestartReasonArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    GreedyRestartReasonArgument(nodeName).setOptional(optional).apply(block)
)