package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;

public class SetWorldSpawnCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"setworldspawn", "worldspawn"};
    }

    @Override
    public String usage() {
        return "/setworldspawn [<query> | <pos [<angel>]>]";
    }

    @Override
    public String description() {
        return "Query or set the worldspawn";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.SET_WORLD_SPAWN_PERMISSION));

        literal.executes(context -> set(context.getSource(), context.getSource().getPlayerOrException().blockPosition(), 0.0F));

        literal.then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(context -> set(context.getSource(), BlockPosArgument.getSpawnablePos(context, "pos"), 0.0F))
                .then(Commands.argument("angle", AngleArgument.angle())
                        .executes(context -> set(context.getSource(), BlockPosArgument.getSpawnablePos(context, "pos"), AngleArgument.getAngle(context, "angle")))));
    }

    private int set(CommandSourceStack source, BlockPos pos, float angle) throws CommandSyntaxException {
        source.getLevel().setDefaultSpawnPos(pos, angle);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Der Welt spawn wurde bei ", Colors.SUCCESS)
                    .append(Component.text("%s %s %s".formatted(pos.getX(), pos.getY(), pos.getZ()), Colors.TERTIARY))
                    .append(Component.text(" mit einem Winkel von ", Colors.SUCCESS))
                    .append(Component.text(angle, Colors.TERTIARY))
                    .append(Component.text(" gesetzt.", Colors.SUCCESS)));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ(), angle), false);
        }

        return 1;
    }
}
