package dev.slne.surf.essentials.main.commands.general.other.poll;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PollDeleteCommand {
    public static void deletePoll(Player player, String[] args){
        // Check if the correct number of arguments has been provided
        if (args.length == 1){
            // If not, send a message to the player with usage instructions
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Korrekte Benutzung: ", SurfColors.RED))
                    .append(Component.text("/poll delete <name of poll>", SurfColors.TERTIARY))));
            return;
        }
        // Get the name of the poll from the arguments
        String poll = args[1];
        // Check if the poll exists
        if (!PollUtil.polls.contains(poll)){
            // If not, send an error message to the player
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Die Umfrage ", SurfColors.ERROR))
                    .append(Component.text(poll, SurfColors.TERTIARY))
                    .append(Component.text(" existiert nicht", SurfColors.ERROR))));
            return;
        }
        // Delete poll
        PollUtil.deletePoll(poll);
        // Send a success message to the player
        SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Die Umfrage wurde gelöscht!", SurfColors.SUCCESS))));
    }

}
