package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.NamespacedKeyArgument
import dev.slne.surf.essentials.util.GameRuleWrapper
import dev.slne.surf.api.core.messages.adventure.buildText
import org.bukkit.GameRule
import org.bukkit.NamespacedKey

class GameruleArgument(nodeName: String) :
    CustomArgument<GameRule<*>, NamespacedKey>(NamespacedKeyArgument(nodeName), { info ->
        GameRuleWrapper.getByKey(info.input)
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendErrorPrefix()
                    error("Die Spielregel wurde nicht gefunden.")
                }
            }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                GameRuleWrapper.all().map { it.key.asString() }
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