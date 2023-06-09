package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.FunctionArgument;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@SuppressWarnings("unused")
public class FunctionCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"function"};
    }

    @Override
    public String usage() {
        return "/function <function>";
    }

    @Override
    public String description() {
        return "Execute Functions";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.FUNCTION_PERMISSION));

        literal.then(Commands.argument("name", FunctionArgument.functions())
                .suggests(SUGGEST_FUNCTION)
                .executes(context -> executeFunction(context.getSource(), FunctionArgument.getFunctions(context, "name"))));
    }

    private int executeFunction(@NotNull CommandSourceStack source, @NotNull Collection<CommandFunction> commandFunctions) {
        final var functionManager = source.getServer().getFunctions();
        int executedFunctions = 0;

        for (CommandFunction function : commandFunctions) {
            executedFunctions += functionManager.execute(function, source.withSuppressedOutput().withMaximumPermission(2));
        }

        final boolean isSingleFunction = executedFunctions == 1;

        if (commandFunctions.size() == 1) {
            EssentialsUtil.sendSourceSuccess(source, Component.text("Es %s ".formatted((isSingleFunction) ? "wurde" : "wurden"), Colors.SUCCESS)
                    .append(Component.text(executedFunctions, Colors.VARIABLE_VALUE))
                    .append(Component.text(" %s von der Funktion ".formatted(isSingleFunction ? "command" : "commands"), Colors.SUCCESS))
                    .append(Component.text(commandFunctions.iterator().next().getId().toString(), Colors.VARIABLE_KEY))
                    .append(Component.text(" ausgeführt.", Colors.SUCCESS)));

        } else {
            EssentialsUtil.sendSourceSuccess(source, Component.text("Es %s ".formatted((isSingleFunction) ? "wurde" : "wurden"), Colors.SUCCESS)
                    .append(Component.text(executedFunctions, Colors.VARIABLE_VALUE))
                    .append(Component.text(" %s aus ".formatted(isSingleFunction ? "command" : "commands"), Colors.SUCCESS))
                    .append(Component.text(commandFunctions.size(), Colors.VARIABLE_VALUE))
                    .append(Component.text(" Funktionen ausgeführt.", Colors.SUCCESS)));
        }

        return executedFunctions;
    }


    public static final SuggestionProvider<CommandSourceStack> SUGGEST_FUNCTION = (context, builder) -> {
        final var serverFunctionManager = context.getSource().getServer().getFunctions();
        SharedSuggestionProvider.suggestResource(serverFunctionManager.getTagNames(), builder, "#");
        return SharedSuggestionProvider.suggestResource(serverFunctionManager.getFunctionNames(), builder);
    };
}
