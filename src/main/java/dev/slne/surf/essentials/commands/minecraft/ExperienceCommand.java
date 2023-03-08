package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class ExperienceCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("experience", ExperienceCommand::literal).setUsage("/experience <query | add  set>")
                .setDescription("Query, add or set the experience of the targets");
        SurfEssentials.registerPluginBrigadierCommand("xp", ExperienceCommand::literal).setUsage("/experience <query | add  set>")
                .setDescription("Query, add or set the experience of the targets");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.EXPERIENCE_PERMISSION));

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

    private static int query(CommandSourceStack source, ServerPlayer targetUnchecked, int whatToQuery)throws CommandSyntaxException{
        ServerPlayer target = EssentialsUtil.checkSinglePlayerSuggestion(source, targetUnchecked);
        if (whatToQuery != 0 && whatToQuery != 1) throw new IllegalArgumentException("'whatToQuery' can only be 0 or 1.");
        int result = (whatToQuery == 0) ? target.experienceLevel : Math.round(target.experienceProgress * (float) target.getXpNeededForNextLevel());

        if (source.isPlayer()){
            String whatToQueryName = (whatToQuery == 0) ? " Erfahrungslevel" : " Erfahrungspunkte";

            EssentialsUtil.sendSuccess(source, PaperAdventure.asAdventure(target.getDisplayName()).colorIfAbsent(Colors.TERTIARY)
                    .append(Component.text(" hat ", Colors.INFO))
                    .append(Component.text(result, Colors.GREEN))
                    .append(Component.text(whatToQueryName, Colors.TERTIARY))
                    .append(Component.text("!", Colors.INFO)));
        }else {
            String whatToQueryName = (whatToQuery == 0) ? "levels" : "points";
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.experience.query." + whatToQueryName,
                    target.getDisplayName(), result), false);
        }
        return 1;
    }

    private static int give(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, int amount, int whatToGive) throws CommandSyntaxException{
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
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

                EssentialsUtil.sendSuccess(source, targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY)
                        .append(Component.text(" hat ", Colors.SUCCESS))
                        .append(Component.text(amount, Colors.GREEN))
                        .append(Component.text(whatToGiveName, Colors.TERTIARY))
                        .append(Component.text(" erhalten!", Colors.SUCCESS))
                        .hoverEvent(HoverEvent.showText(Component.text("Insgesamt: ", Colors.INFO)
                                .append(Component.text(experience, Colors.GREEN)))));
            }else {
                EssentialsUtil.sendSuccess(source, Component.text(targets.size(), Colors.TERTIARY)
                        .append(Component.text(" Spieler haben ", Colors.SUCCESS))
                        .append(Component.text(amount, Colors.GREEN))
                        .append(Component.text(whatToGiveName, Colors.TERTIARY))
                        .append(Component.text(" erhalten!", Colors.SUCCESS)));
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

    private static int set(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, int amount, int whatToSet) throws CommandSyntaxException{
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        if (whatToSet != 0 && whatToSet != 1) throw new IllegalArgumentException("'whatToSet' can only be 0 or 1.");

        for (ServerPlayer player : targets) {
            if ((whatToSet == 0)) player.setExperienceLevels(amount);
            else player.setExperiencePoints(amount);
        }

        if (source.isPlayer()){
            String whatToSetName = (whatToSet == 0) ? " Erfahrungslevel" : " Erfahrungspunkte";

            if (targets.size() == 1){
                EssentialsUtil.sendSuccess(source, Component.text("Die", Colors.SUCCESS)
                        .append(Component.text(whatToSetName, Colors.TERTIARY))
                        .append(Component.text(" von ", Colors.SUCCESS))
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                        .append(Component.text(" wurden auf ", Colors.SUCCESS))
                        .append(Component.text(amount, Colors.GREEN))
                        .append(Component.text(" gesetzt!", Colors.SUCCESS)));
            }else {
                EssentialsUtil.sendSuccess(source, Component.text("Die", Colors.SUCCESS)
                        .append(Component.text(whatToSetName, Colors.TERTIARY))
                        .append(Component.text(" von ", Colors.SUCCESS))
                        .append(Component.text(targets.size(), Colors.TERTIARY))
                        .append(Component.text(" Spielern wurden auf ", Colors.SUCCESS))
                        .append(Component.text(amount, Colors.GREEN))
                        .append(Component.text(" gesetzt!", Colors.SUCCESS)));
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
