package dev.slne.surf.essentials.main.commands.general.other.world;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import static dev.slne.surf.essentials.main.commands.general.other.world.WorldCommand.creatingWorld;
import static dev.slne.surf.essentials.main.commands.general.other.world.WorldCommand.successCreate;

//TODO: fix that you can also create other environments
public class WorldCreate {
    public static void createFromArguments(Player player, String[] args) {
        if (args.length == 1) {//no world name specified
            EssentialsUtil.somethingWentWrongAsync_DE(player, "Du musst einen Namen angeben!");
            return;
        }

        WorldCreator wc = new WorldCreator(args[1]);

        if (args.length == 2) {//No environment specified
            EssentialsUtil.somethingWentWrongAsync_DE(player, "Du musst ein environment angeben!");
            return;
        }
        //checks if the environment is valid
        try {
            World.Environment.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException exception) {
            EssentialsUtil.somethingWentWrongAsync_DE(player, "Du musst ein gültiges environment angeben! \nZum Beispiel: NORMAL, NETHER, THE_END");
            return;
        }

        String allArgs = "world ";
        for (int i = 0; i < args.length; i++) allArgs += args[i] + " ";//adds all arguments to the string
        final String finalAllArgs = allArgs;

        if (!finalAllArgs.contains(" --force")) {//checks if the command ends with the flag
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Eine Welt zu erstellen kann den Server kurz zum laggen bringen!", SurfColors.WARNING))
                    .append(Component.newline())
                    .append(SurfApi.getPrefix())
                    .append(Component.text("um fortzufahren füge bitte deinem Command ", SurfColors.INFO))
                    .append(Component.text("--force", SurfColors.RED))
                    .append(Component.text(" hinzu oder klicke ", SurfColors.INFO))
                    .append(Component.text("HIER", SurfColors.TERTIARY)
                            .decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke zum bestätigen", SurfColors.DARK_GRAY)))
                            .clickEvent(ClickEvent.suggestCommand("/%s --force".formatted(finalAllArgs))))));
            return;
        }

        World.Environment env = World.Environment.valueOf(args[2].toUpperCase());//the world environment

        if (args.length == 4) {//creates a world only with environment
            creatingWorld(player, wc);
            wc.environment(env);
            wc.createWorld().setKeepSpawnInMemory(false);

            wc.createWorld();
            Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> successCreate(player, wc), 20);
            return;
        }
        //checks if the WorldType is valid
        WorldType type;
        try {
            type = WorldType.getByName(args[3]);
        } catch (NullPointerException e) {
            EssentialsUtil.somethingWentWrongAsync_DE(player, "Du musst einen gültigen WorldType angeben!");
            return;
        }
        if (args.length == 5) {//creates a world with environment and WorldType

            wc.environment(env);
            wc.type(type);

            wc.createWorld().setKeepSpawnInMemory(false);
            wc.createWorld();
            Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> successCreate(player, wc), 20);
            return;
        }
        if (!isBoolean(args[4])) {//check if the arg is a boolean
            EssentialsUtil.somethingWentWrongAsync_DE(player, "Du musst einen Boolean angeben!");
            return;
        }
        creatingWorld(player, wc);
        wc.environment(env);
        wc.type(type);
        wc.generateStructures(Boolean.parseBoolean(args[4]));
        wc.createWorld().setKeepSpawnInMemory(false);
        wc.createWorld();
        Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> successCreate(player, wc), 20);

    }

    private static Boolean isBoolean(String toCheck) {
        if (toCheck.equalsIgnoreCase("true")) return true;
        if (toCheck.equalsIgnoreCase("false")) return true;
        return false;
    }
}
