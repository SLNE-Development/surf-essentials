package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.advancement.Advancement
import org.bukkit.command.CommandSender

class AdvancementCriterionArgument(nodeName: String) :
    CustomArgument<String, String>(GreedyStringArgument(nodeName), { info ->
        info.previousArgs.getUnchecked<Advancement>("advancement")?.let {
            if (it.criteria.contains(info.input)) {
                info.input
            } else {
                throw CustomArgumentException.fromAdventureComponent {
                    buildText {
                        appendPrefix()
                        error("Die Bedingung '$info.input' existiert nicht im Fortschritt für das Advancement ${it.key}.")
                    }
                }
            }
        } ?: info.input
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                it.previousArgs.getUnchecked<Advancement>("advancement")?.criteria
            }
        )
    }
}

inline fun Argument<*>.advancementCriterionArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    AdvancementCriterionArgument(nodeName).setOptional(optional).apply(block)
)