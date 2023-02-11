package dev.slne.surf.essentials.commands.minecraft;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;

@PermissionTag(name = Permissions.DIFFICULTY_PERMISSION, desc = "This is the permission for the 'difficulty' command")
public class DifficultyCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("difficulty", DifficultyCommand::literal).setUsage("/difficulty [<difficulty>]")
                .setDescription("Query or set a level difficulty");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        Difficulty[] allDifficultyLevels = Difficulty.values();
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.DIFFICULTY_PERMISSION));

        literal.executes(context -> getDifficulty(context.getSource()));
        for (Difficulty difficulty : allDifficultyLevels) {
            literal.then(Commands.literal(difficulty.getKey()).executes(context -> setDifficulty(context.getSource(), difficulty)));
        }
    }

    private static int setDifficulty(CommandSourceStack source, Difficulty difficulty) throws CommandSyntaxException {
        MinecraftServer minecraftserver = source.getServer();
        ServerLevel worldServer = source.getLevel();
        if (worldServer.getDifficulty() == difficulty) {
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, "Die Schwierigkeiten sind identisch!");
            }else throw ERROR_ALREADY_DIFFICULT.create(difficulty.getKey());
            return 0;
        } else {
            minecraftserver.setDifficulty(worldServer, difficulty, true);
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Die Schwierigkeit wurde auf ", SurfColors.SUCCESS)
                        .append(PaperAdventure.asAdventure(difficulty.getDisplayName()).colorIfAbsent(SurfColors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(" gesetzt!", SurfColors.SUCCESS)));
            }else {
                source.sendSuccess(Component.translatable("commands.difficulty.success", difficulty.getDisplayName()), false);
            }
            return 1;
        }
    }

    private static int getDifficulty(CommandSourceStack source) throws CommandSyntaxException {
        Difficulty difficulty = source.getServer().getWorldData().getDifficulty();

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Die Schwierigkeit ist ", SurfColors.INFO)
                    .append(PaperAdventure.asAdventure(difficulty.getDisplayName()).colorIfAbsent(SurfColors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text("!", SurfColors.INFO)));
        }else {
            source.sendSuccess(Component.translatable("commands.difficulty.query", difficulty.getDisplayName()), false);
        }
        return 1;
    }

    private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType(object ->
            Component.translatable("commands.difficulty.failure", object));
}
