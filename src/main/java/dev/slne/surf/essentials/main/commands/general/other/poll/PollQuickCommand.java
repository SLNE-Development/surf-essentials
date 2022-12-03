package dev.slne.surf.essentials.main.commands.general.other.poll;

import org.bukkit.entity.Player;

public class PollQuickCommand {
    public static void quick(Player player, String[] args){
        String allArgs = "poll ";
        for (int i = 0; i < args.length; i++) allArgs += args[i] + " ";//adds all arguments to the string
        final String finalAllArgs = allArgs;

        int timeSeconds = 900;

        if (args[0].startsWith("time:")) timeSeconds = Integer.parseInt(args[0]);

        PollUtil.setPoll(true);
    }
}
