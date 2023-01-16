package dev.slne.surf.essentials.main.commands.minecraft;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class ExperienceCommand {
    public static String PERMISSION;
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("experience", ExperienceCommand::literal).setUsage("/experience <query | add  set>")
                .setDescription("Query, add or set the experience of the targets");
        SurfEssentials.registerPluginBrigadierCommand("xp", ExperienceCommand::literal).setUsage("/experience <query | add  set>")
                .setDescription("Query, add or set the experience of the targets");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, PERMISSION));

        literal.then(Commands.literal("query")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> query(context.getSource(), EntityArgument.getPlayer(context, "player"), 1))
                        .then(Commands.literal("levels")
                                .executes(context -> query(context.getSource(), EntityArgument.getPlayer(context, "player"), 0)))
                        .then(Commands.literal("points")
                                .executes(context -> query(context.getSource(), EntityArgument.getPlayer(context, "player"), 1)))));

        literal.then(Commands.literal("add")
                .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(context -> give(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                        IntegerArgumentType.getInteger(context, "amount"), 1))
                                .then(Commands.literal("levels")
                                        .executes(context -> give(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                                IntegerArgumentType.getInteger(context, "amount"), 0)))
                                .then(Commands.literal("points")
                                        .executes(context -> give(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                                IntegerArgumentType.getInteger(context, "amount"), 1))))));

        literal.then(Commands.literal("set")
                .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(context -> set(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                        IntegerArgumentType.getInteger(context, "amount"), 1))
                                .then(Commands.literal("levels")
                                        .executes(context -> set(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                                IntegerArgumentType.getInteger(context, "amount"), 0)))
                                .then(Commands.literal("points")
                                        .executes(context -> set(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                                IntegerArgumentType.getInteger(context, "amount"), 1))))));
    }

    private static int query(CommandSourceStack source, Player target, int whatToQuery)throws CommandSyntaxException{
        if (whatToQuery != 0 && whatToQuery != 1) throw new IllegalArgumentException("'whatToQuery' can only be 0 or 1.");
        int result = (whatToQuery == 0) ? target.experienceLevel : Math.round(target.experienceProgress * (float) target.getXpNeededForNextLevel());

        if (source.isPlayer()){
            String whatToQueryName = (whatToQuery == 0) ? " Erfahrungslevel" : " Erfahrungspunkte";

            EssentialsUtil.sendSuccess(source, PaperAdventure.asAdventure(target.getDisplayName()).colorIfAbsent(SurfColors.TERTIARY)
                    .append(Component.text(" hat ", SurfColors.INFO))
                    .append(Component.text(result, SurfColors.GREEN))
                    .append(Component.text(whatToQueryName, SurfColors.TERTIARY))
                    .append(Component.text("!", SurfColors.INFO)));
        }else {
            String whatToQueryName = (whatToQuery == 0) ? "levels" : "points";
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.experience.query." + whatToQueryName,
                    target.getDisplayName(), result), false);
        }
        return 1;
    }

    private static int give(CommandSourceStack source, Collection<ServerPlayer> targets, int amount, int whatToGive) throws CommandSyntaxException{
        if (whatToGive != 0 && whatToGive != 1) throw new IllegalArgumentException("'whatToGive' can only be 0 or 1.");

        for (ServerPlayer player : targets) {
            if ((whatToGive == 0)) player.giveExperienceLevels(amount);
            else player.giveExperiencePoints(amount);
        }

        if (source.isPlayer()){
            String whatToGiveName = (whatToGive == 0) ? " Erfahrungslevel" : " Erfahrungspunkte";

            if (targets.size() == 1){
                ServerPlayer target = targets.iterator().next();
                int experience = (whatToGive == 0) ? target.experienceLevel : Math.round(target.experienceProgress * (float) target.getXpNeededForNextLevel());

                EssentialsUtil.sendSuccess(source, targets.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY)
                        .append(Component.text(" hat ", SurfColors.SUCCESS))
                        .append(Component.text(amount, SurfColors.GREEN))
                        .append(Component.text(whatToGiveName, SurfColors.TERTIARY))
                        .append(Component.text(" erhalten!", SurfColors.SUCCESS))
                        .hoverEvent(HoverEvent.showText(Component.text("Insgesamt: ", SurfColors.INFO)
                                .append(Component.text(experience, SurfColors.GREEN)))));
            }else {
                EssentialsUtil.sendSuccess(source, Component.text(targets.size(), SurfColors.TERTIARY)
                        .append(Component.text(" Spieler haben ", SurfColors.SUCCESS))
                        .append(Component.text(amount, SurfColors.GREEN))
                        .append(Component.text(whatToGiveName, SurfColors.TERTIARY))
                        .append(Component.text(" erhalten!", SurfColors.SUCCESS)));
            }
        }else {
            String whatToGiveName = (whatToGive == 0) ? "levels" : "points";
            if (targets.size() == 1) {
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.experience.add." + whatToGiveName + ".success.single",
                        amount, targets.iterator().next().getDisplayName()), false);
            } else {
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.experience.add." + whatToGiveName + ".success.multiple",
                        amount, targets.size()), false);
            }
        }
        return 1;
    }

    private static int set(CommandSourceStack source, Collection<ServerPlayer> targets, int amount, int whatToSet) throws CommandSyntaxException{
        if (whatToSet != 0 && whatToSet != 1) throw new IllegalArgumentException("'whatToSet' can only be 0 or 1.");

        for (ServerPlayer player : targets) {
            if ((whatToSet == 0)) player.setExperienceLevels(amount);
            else player.setExperiencePoints(amount);
        }

        if (source.isPlayer()){
            String whatToSetName = (whatToSet == 0) ? " Erfahrungslevel" : " Erfahrungspunkte";

            if (targets.size() == 1){
                EssentialsUtil.sendSuccess(source, Component.text("Die", SurfColors.SUCCESS)
                        .append(Component.text(whatToSetName, SurfColors.TERTIARY))
                        .append(Component.text(" von ", SurfColors.SUCCESS))
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                        .append(Component.text(" wurden auf ", SurfColors.SUCCESS))
                        .append(Component.text(amount, SurfColors.GREEN))
                        .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));
            }else {
                EssentialsUtil.sendSuccess(source, Component.text("Die", SurfColors.SUCCESS)
                        .append(Component.text(whatToSetName, SurfColors.TERTIARY))
                        .append(Component.text(" von ", SurfColors.SUCCESS))
                        .append(Component.text(targets.size(), SurfColors.TERTIARY))
                        .append(Component.text(" Spielern wurden auf ", SurfColors.SUCCESS))
                        .append(Component.text(amount, SurfColors.GREEN))
                        .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));
            }
        }else {
            String whatToSetName = (whatToSet == 0) ? "levels" : "points";
            if (targets.size() == 1) {
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.experience.set." + whatToSetName + ".success.single",
                        amount, targets.iterator().next().getDisplayName()), false);
            } else {
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.experience.set." + whatToSetName + ".success.multiple",
                        amount, targets.size()), false);
            }
        }
        return 1;
    }

}
