package dev.slne.surf.essentials.main.commands.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class SuicideCommand {
    public static String PERMISSION;
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("suicide", SuicideCommand::literal).setUsage("/suicide")
                .setDescription("Makes you commit suicide");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, PERMISSION));
        literal.executes(SuicideCommand::suicide);
    }

    private static int suicide(CommandContext<CommandSourceStack> context) throws CommandSyntaxException{
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.wasKilled(player.getLevel(), player);
        EssentialsUtil.sendSuccess(context.getSource(), "Du hast Selbstmord begangen!");
        return 1;
    }
}
