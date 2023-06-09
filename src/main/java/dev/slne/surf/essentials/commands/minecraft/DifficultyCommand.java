package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;

public class DifficultyCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"difficulty"};
    }

    @Override
    public String usage() {
        return "/difficulty [<difficulty>]";
    }

    @Override
    public String description() {
        return "Get or change the server difficulty";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.DIFFICULTY_PERMISSION));

        literal.executes(context -> getDifficulty(context.getSource()));
        for (Difficulty difficulty : Difficulty.values()) {
            literal.then(Commands.literal(difficulty.getKey()).executes(context -> setDifficulty(context.getSource(), difficulty)));
        }
    }

    private static int setDifficulty(CommandSourceStack source, Difficulty difficulty) throws CommandSyntaxException {
        ServerLevel worldServer = source.getLevel();

        if (worldServer.getDifficulty() == difficulty) throw ERROR_ALREADY_DIFFICULT.create(difficulty.getKey());

        EssentialsUtil.getMinecraftServer().setDifficulty(worldServer, difficulty, true);

        EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Die Schwierigkeit wurde auf ", Colors.SUCCESS)
                .append(PaperAdventure.asAdventure(difficulty.getDisplayName()).colorIfAbsent(Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" gesetzt!", Colors.SUCCESS)));

        return 1;

    }

    private static int getDifficulty(CommandSourceStack source) {
        Difficulty difficulty = EssentialsUtil.getMinecraftServer().getWorldData().getDifficulty();

        EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Die Schwierigkeit ist ", Colors.INFO)
                .append(PaperAdventure.asAdventure(difficulty.getDisplayName()).colorIfAbsent(Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text("!", Colors.INFO)));
        return 1;
    }

    private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType(object ->
            Component.translatable("commands.difficulty.failure", object));
}
