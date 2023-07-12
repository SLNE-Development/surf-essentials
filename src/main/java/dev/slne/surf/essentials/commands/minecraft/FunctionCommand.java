package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.wrappers.FunctionWrapper;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class FunctionCommand extends EssentialsCommand {

    public FunctionCommand() {
        super("function", "function <function>", "Runs a function");

        withPermission(Permissions.FUNCTION_PERMISSION);

        then(functionArgument("function")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> executeFunction(sender.getCallee(), args.getUnchecked("function"))));
    }

    private int executeFunction(@NotNull CommandSender source, FunctionWrapper @NotNull [] commandFunctions) {
        int executedFunctions = 0;

        for (FunctionWrapper function : commandFunctions) {
            executedFunctions += function.run(source);
        }

        val isSingleFunction = executedFunctions == 1;

        if (commandFunctions.length == 1) {
            EssentialsUtil.sendSuccess(source, Component.text("Es %s ".formatted((isSingleFunction) ? "wurde" : "wurden"), Colors.SUCCESS)
                    .append(Component.text(executedFunctions, Colors.VARIABLE_VALUE))
                    .append(Component.text(" %s von der Funktion ".formatted(isSingleFunction ? "command" : "commands"), Colors.SUCCESS))
                    .append(Component.text(commandFunctions[0].getKey().asString(), Colors.VARIABLE_KEY))
                    .append(Component.text(" ausgeführt.", Colors.SUCCESS)));

        } else {
            EssentialsUtil.sendSuccess(source, Component.text("Es %s ".formatted((isSingleFunction) ? "wurde" : "wurden"), Colors.SUCCESS)
                    .append(Component.text(executedFunctions, Colors.VARIABLE_VALUE))
                    .append(Component.text(" %s aus ".formatted(isSingleFunction ? "command" : "commands"), Colors.SUCCESS))
                    .append(Component.text(commandFunctions.length, Colors.VARIABLE_VALUE))
                    .append(Component.text(" Funktionen ausgeführt.", Colors.SUCCESS)));
        }

        return executedFunctions;
    }
}
