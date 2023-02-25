package dev.slne.surf.essentials.commands.general.other.poll;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class VoteCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("vote", VoteCommand::literal).setUsage("/vote <poll>")
                .setDescription("Vote for polls");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(0, Permissions.VOTE_PERMISSION));

        literal.then(Commands.argument("poll", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            for (String poll : PollCommand.polls()) {
                                builder.suggest(poll);
                            }
                            return builder.buildFuture();
                        })
                .then(Commands.literal("yes")
                        .executes(context -> vote(context.getSource(), StringArgumentType.getString(context, "poll"), true)))
                .then(Commands.literal("no")
                        .executes(context -> vote(context.getSource(), StringArgumentType.getString(context, "poll"), false))));

    }

    private static int vote(CommandSourceStack source, String name, boolean yes) throws CommandSyntaxException {
        if (!PollCommand.isValidPoll(name)){
            EssentialsUtil.sendError(source, "Die Umfrage existiert nicht!");
            return 0;
        }
        if (PollCommand.hasVoted(source.getPlayerOrException(), name)){
            EssentialsUtil.sendError(source, "Du kannst nur einmal Abstimmen!");
            return 0;
        }
        if (yes) PollCommand.addYesCount(name);
        else PollCommand.addNoCount(name);

        PollCommand.addVotedPlayer(source.getPlayerOrException(), name);
        EssentialsUtil.sendSuccess(source, "Vielen Dank für deine Stimme!");

        return 1;
    }
}
