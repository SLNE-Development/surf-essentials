package dev.slne.surf.essentialsold.commands.minecraft;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Difficulty;

public class DifficultyCommand extends EssentialsCommand {
    public DifficultyCommand() {
        super("difficulty", "difficulty <difficulty>", "Get or change the server difficulty");

        withPermission(Permissions.DIFFICULTY_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> getDifficulty(sender));
        then(difficultyArgument("difficulty")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setDifficulty(sender, args.getUnchecked("difficulty"))));
    }


    private static int setDifficulty(NativeProxyCommandSender source, Difficulty difficulty) throws WrapperCommandSyntaxException {
        val world = source.getWorld();

        if (world.getDifficulty() == difficulty) throw Exceptions.ERROR_ALREADY_THAT_DIFFICULT.create(difficulty);
        world.setDifficulty(difficulty);

        EssentialsUtil.sendSuccess(source.getCallee(), Component.text("Die Schwierigkeit wurde auf ", Colors.SUCCESS)
                .append(Component.translatable(difficulty.translationKey(), Colors.VARIABLE_VALUE))
                .append(Component.text(" gesetzt!", Colors.SUCCESS)));

        return 1;
    }

    private static int getDifficulty(NativeProxyCommandSender source) {
        final Difficulty difficulty = source.getWorld().getDifficulty();

        EssentialsUtil.sendSuccess(source.getCallee(), Component.text("Die Schwierigkeit ist ", Colors.INFO)
                .append(Component.translatable(difficulty.translationKey(), Colors.VARIABLE_VALUE))
                .append(Component.text("!", Colors.INFO)));
        return 1;
    }
}
