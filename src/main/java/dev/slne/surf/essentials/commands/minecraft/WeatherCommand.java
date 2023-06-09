package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

public class WeatherCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"weather"};
    }

    @Override
    public String usage() {
        return "/weather <clear | rain | thunder> [<duration>]";
    }

    @Override
    public String description() {
        return "Change game weather";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {

        literal.requires(EssentialsUtil.checkPermissions(Permissions.WEATHER_PERMISSION));

        literal.executes(context -> queryWeather(context.getSource()));

        literal.then(Commands.literal("clear")
                .executes(context -> setClear(context.getSource(), 6000))
                .then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000))
                        .executes(context -> setClear(context.getSource(), IntegerArgumentType.getInteger(context, "duration")))));

        literal.then(Commands.literal("rain")
                .executes(context -> setRain(context.getSource(), 6000))
                .then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000))
                        .executes(context -> setRain(context.getSource(), IntegerArgumentType.getInteger(context, "duration")))));

        literal.then(Commands.literal("thunder")
                .executes(context -> setThunder(context.getSource(), 6000))
                .then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000))
                        .executes(context -> setThunder(context.getSource(), IntegerArgumentType.getInteger(context, "duration")))));
    }

    private static int queryWeather(CommandSourceStack source) throws CommandSyntaxException {
        ServerLevel serverLevel = source.getLevel();

        boolean isClear = !serverLevel.isThundering() && !serverLevel.isRaining();

        int clearDuration = serverLevel.serverLevelData.getClearWeatherTime();
        int rainDuration = serverLevel.serverLevelData.getRainTime();
        int thunderDuration = serverLevel.serverLevelData.getThunderTime();

        if (isClear) {
            EssentialsUtil.sendSuccess(source, weatherComponent$adventure("Klar", serverLevel, clearDuration));
        } else if (serverLevel.isRaining()) {
            EssentialsUtil.sendSuccess(source, weatherComponent$adventure("Regen", serverLevel, rainDuration));
        } else {
            EssentialsUtil.sendSuccess(source, weatherComponent$adventure("Gewitter", serverLevel, thunderDuration));
        }
        return 1;
    }

    private static int setClear(CommandSourceStack source, int durationInSeconds) throws CommandSyntaxException {
        ServerLevel serverLevel = source.getLevel();
        serverLevel.setWeatherParameters(durationInSeconds, 0, false, false);

        EssentialsUtil.sendSuccess(source, weatherSetComponent$adventure("Klar", serverLevel, durationInSeconds));

        return durationInSeconds;
    }

    private static int setRain(CommandSourceStack source, int durationInSeconds) throws CommandSyntaxException {
        ServerLevel serverLevel = source.getLevel();
        serverLevel.setWeatherParameters(0, durationInSeconds, true, false);

        EssentialsUtil.sendSuccess(source, weatherSetComponent$adventure("Regen", serverLevel, durationInSeconds));

        return durationInSeconds;
    }

    private static int setThunder(CommandSourceStack source, int durationInSeconds) throws CommandSyntaxException {
        ServerLevel serverLevel = source.getLevel();
        serverLevel.setWeatherParameters(0, durationInSeconds, true, true);

        EssentialsUtil.sendSuccess(source, weatherSetComponent$adventure("Gewitter", serverLevel, durationInSeconds));

        return durationInSeconds;
    }


    private static Component weatherComponent$adventure(String weather, ServerLevel serverLevel, int durationInTicks) {
        return Component.text("Das Wetter in der Welt ", Colors.INFO)
                .append(Component.text(serverLevel.dimension().location().toString(), Colors.TERTIARY))
                .append(Component.text(" ist ", Colors.INFO))
                .append(Component.text(weather, Colors.TERTIARY))
                .append(Component.text(" für ", Colors.INFO))
                .append(Component.text(EssentialsUtil.ticksToString(durationInTicks), Colors.TERTIARY));
    }

    private static Component weatherSetComponent$adventure(String weather, ServerLevel serverLevel, int durationInTicks) {
        return Component.text("Das Wetter in der Welt ", Colors.INFO)
                .append(Component.text(serverLevel.dimension().location().toString(), Colors.TERTIARY))
                .append(Component.text(" wurde auf ", Colors.INFO))
                .append(Component.text(weather, Colors.TERTIARY))
                .append(Component.text(" für ", Colors.INFO))
                .append(Component.text(EssentialsUtil.ticksToString(durationInTicks), Colors.TERTIARY))
                .append(Component.text(" gesetzt.", Colors.INFO));
    }
}