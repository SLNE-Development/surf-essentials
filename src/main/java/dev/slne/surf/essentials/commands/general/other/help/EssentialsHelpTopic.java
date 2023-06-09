package dev.slne.surf.essentials.commands.general.other.help;

import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.jetbrains.annotations.NotNull;

public class EssentialsHelpTopic extends HelpTopic { // TODO: not working at the moment
    final BrigadierCommand command;

    public EssentialsHelpTopic(BrigadierCommand command){
        this.command = command;
    }

    /**
     * Determines if a {@link Player} is allowed to see this help topic.
     * <p>
     * HelpTopic implementations should take server administrator wishes into
     * account as set by the {@link HelpTopic#amendCanSee(String)} function.
     *
     * @param player The Player in question.
     * @return True of the Player can see this help topic, false otherwise.
     */
    @Override
    public boolean canSee(@NotNull CommandSender player) {
        return true;
    }

    /**
     * Returns the name of this help topic.
     *
     * @return The topic name.
     */
    @Override
    public @NotNull String getName() {
        return command.names()[0];
    }

    /**
     * Returns a brief description that will be displayed in the topic index.
     *
     * @return A brief topic description.
     */
    @Override
    public @NotNull String getShortText() {
        return command.usage();
    }

    /**
     * Returns the full description of this help topic that is displayed when
     * the user requests this topic's details.
     * <p>
     * The result will be paginated to properly fit the user's client.
     *
     * @param forWho The player or console requesting the full text. Useful
     *               for further security trimming the command's full text based on
     *               sub-permissions in custom implementations.
     * @return A full topic description.
     */
    @Override
    public @NotNull String getFullText(@NotNull CommandSender forWho) {
        return command.description();
    }
}
