package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.sound.Sound
import org.bukkit.command.CommandSender

class SoundSourceArgument(nodeName: String) :
    CustomArgument<Sound.Source, String>(StringArgument(nodeName), { info ->
        Sound.Source.entries.firstOrNull { it.name == info.input.uppercase() }
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Die Soundquelle wurde nicht gefunden.")
                }
            }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                Sound.Source.entries.map {
                    it.name.lowercase()
                }
            }
        )
    }
}

inline fun Argument<*>.soundSourceArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    SoundSourceArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandTree.soundSourceArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    SoundSourceArgument(nodeName).setOptional(optional).apply(block)
)