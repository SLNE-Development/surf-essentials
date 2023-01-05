package dev.slne.surf.essentials.main.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;

public class GameruleCommand {
    public static final String PERMISSION = "surf.essentials.commands.gamerule";

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("gamerules", GameruleCommand::literal).setDescription("Change the server gamerules")
                .setUsage("/gamerule <gamerule> [<value>]");
    }
    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, PERMISSION));

        //TODO: perhaps add a gamerule description when the user hovers over the suggested gamerule
        GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            public <T extends GameRules.Value<T>> void visit(GameRules.@NotNull Key<T> key, GameRules.@NotNull Type<T> type) {
                literal.then((Commands.literal(key.getId())
                        .executes(context -> queryRule(context.getSource(), key)))

                        .then(type.createArgument("value")
                                .executes(context -> setRule(context, key))));
            }
        });
    }

    static <T extends GameRules.Value<T>> int setRule(CommandContext<CommandSourceStack> context, GameRules.Key<T> key) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        T value = source.getLevel().getGameRules().getRule(key);
        value.setFromArgument(context, "value", key);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Die Spielregel ", SurfColors.SUCCESS)
                    .append(net.kyori.adventure.text.Component.text(key.getId(), SurfColors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" wurde auf ", SurfColors.SUCCESS))
                    .append(net.kyori.adventure.text.Component.text(value.toString(), SurfColors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" gesetzt!", SurfColors.SUCCESS)));
        }else {
            source.sendSuccess(Component.translatable("commands.gamerule.set", key.getId(), value.toString()), false);
        }
        return value.getCommandResult();
    }

    static <T extends GameRules.Value<T>> int queryRule(CommandSourceStack source, GameRules.Key<T> key) throws CommandSyntaxException {
        T value = source.getLevel().getGameRules().getRule(key);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Die Spielregel ", SurfColors.INFO)
                    .append(net.kyori.adventure.text.Component.text(key.getId(), SurfColors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" ist momentan auf ", SurfColors.INFO))
                    .append(net.kyori.adventure.text.Component.text(value.toString(), SurfColors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" gesetzt!", SurfColors.INFO)));
        }else{
            source.sendSuccess(Component.translatable("commands.gamerule.query", key.getId(), value.toString()), false);
        }

        return value.getCommandResult();
    }
}
