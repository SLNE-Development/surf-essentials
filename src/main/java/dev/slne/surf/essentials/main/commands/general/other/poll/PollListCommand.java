package dev.slne.surf.essentials.main.commands.general.other.poll;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import org.bukkit.entity.Player;

public class PollListCommand {
    public static void list(Player player, String[] args) {
        // Create a ComponentBuilder to build the message
        ComponentBuilder builder = Component.text();

        // Append the prefix and the header to the message
        builder.append(SurfApi.getPrefix()
                .append(Component.text("Aktuelle Umfragen: ", SurfColors.INFO)));

        // Append each poll to the message with a newline and a bullet
        for (String poll : PollUtil.polls) {
            builder.append(Component.newline()
                    .append(SurfApi.getPrefix()
                            .append(Component.text("                  - ", SurfColors.INFO)))
                    .append(Component.text(poll, SurfColors.TERTIARY)));
        }

        // Send the message to the player
        SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(builder.build()));
    }

}
