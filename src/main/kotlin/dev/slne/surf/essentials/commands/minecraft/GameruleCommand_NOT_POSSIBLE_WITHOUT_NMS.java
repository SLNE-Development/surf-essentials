package dev.slne.surf.essentials.commands.minecraft;

/**
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;

public class GameruleCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"gamerule"};
    }

    @Override
    public String usage() {
        return "/gamerule <gamerule> [<value>]";
    }

    @Override
    public String description() {
        return "Manage world game rules";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.GAMERULE_PERMISSION));

        GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            public <T extends GameRules.Value<T>> void visit(GameRules.@NotNull Key<T> key, GameRules.@NotNull Type<T> type) {
                literal.then((Commands.literal(key.getId())
                        .executes(context -> queryRule(context.getSource(), key)))

                        .then(type.createArgument("value")
                                .executes(context -> setRule(context, key))));
            }
        });
    }

    static <T extends GameRules.Value<T>> int setRule(CommandContext<CommandSourceStack> context, GameRules.Key<T> key) {
        CommandSourceStack source = context.getSource();
        T value = source.getLevel().getGameRules().getRule(key);
        value.setFromArgument(context, "value", key);


        EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Die Spielregel ", Colors.SUCCESS)
                .append(net.kyori.adventure.text.Component.text(key.getId(), Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" wurde auf ", Colors.SUCCESS))
                .append(net.kyori.adventure.text.Component.text(value.toString(), Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" gesetzt!", Colors.SUCCESS)));

        return value.getCommandResult();
    }

    static <T extends GameRules.Value<T>> int queryRule(CommandSourceStack source, GameRules.Key<T> key) {
        T value = source.getLevel().getGameRules().getRule(key);

        EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Die Spielregel ", Colors.INFO)
                .append(net.kyori.adventure.text.Component.text(key.getId(), Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" ist momentan auf ", Colors.INFO))
                .append(net.kyori.adventure.text.Component.text(value.toString(), Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" gesetzt!", Colors.INFO)));

        return value.getCommandResult();
    }
}
 */
