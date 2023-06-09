package dev.slne.surf.essentials.commands.general.other.poll;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class VoteCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"vote"};
    }

    @Override
    public String usage() {
        return "/vote <poll> <yes | no>";
    }

    @Override
    public String description() {
        return "Vote for polls";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(EssentialsUtil.checkPermissions(0, Permissions.VOTE_PERMISSION));

        literal.then(Commands.argument("poll", StringArgumentType.word())
                        .suggests(PollCommand.activePollSuggestions())
                .then(Commands.literal("yes")
                        .executes(context -> vote(context.getSource(), StringArgumentType.getString(context, "poll"), true)))
                .then(Commands.literal("no")
                        .executes(context -> vote(context.getSource(), StringArgumentType.getString(context, "poll"), false))));

    }

    private int vote(CommandSourceStack source, String name, boolean yes) throws CommandSyntaxException {
        if (!Poll.checkPollExists(name)){
            EssentialsUtil.sendError(source, "Die Umfrage existiert nicht!");
            return 0;
        }
        final var player = source.getPlayerOrException().getBukkitEntity();

        Poll.getPoll(name).thenAcceptAsync(poll -> {
            if (!poll.addVote(player, yes)){
                EssentialsUtil.sendError(player, "Du kannst nur einmal voten");
            }
            EssentialsUtil.sendSuccess(player, "Vielen Dank für deine Stimme!");
        });
        return 1;
    }
}
