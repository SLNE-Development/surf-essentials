package dev.slne.surf.essentials.main.commands.general.other.poll;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PollEndCommand {
    public static void end(Player player, String[] args){
        // Check if the correct number of arguments has been provided
        if (args.length == 1){
            // If not, send a message to the player with usage instructions
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Korrekte Benutzung: ", SurfColors.RED))
                    .append(Component.text("/poll end <name of poll>", SurfColors.TERTIARY))));
            return;
        }
        // Get the name of the poll from the arguments
        String poll = args[1];
        // Check if the poll exists
        if (!PollUtil.polls.contains(poll)){
            // If not, send an error message to the player
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Aktuell läuft keine Umfrage mit dem Namen ", SurfColors.ERROR))
                    .append(Component.text(poll, SurfColors.TERTIARY))
                    .append(Component.text("!", SurfColors.ERROR))));
            return;
        }
        // End poll
        PollUtil.endPoll(poll, true);
        // Send a success message to the player
        SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Die Umfrage wurde erfolgreich beendet!", SurfColors.SUCCESS))));
    }

}
