package dev.slne.surf.essentials.main.commands.minecraft;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

@PermissionTag(name = Permissions.WEATHER_PERMISSION, desc = "This is the permission for the 'weather' command")
public class WeatherCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("weather", WeatherCommand::literal).setUsage("/weather [<clear | rain | thunder>]")
                .setDescription("Change or get the current weather");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){

        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.WEATHER_PERMISSION));

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
                .executes(context -> setThunder(context.getSource(), IntegerArgumentType.getInteger(context, "duration")))
                .then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000))
                        .executes(context -> setThunder(context.getSource(), 6000))));
    }

    private static int queryWeather(CommandSourceStack source) throws CommandSyntaxException{
        ServerLevel serverLevel = source.getLevel();

        boolean isClear = !serverLevel.isThundering() && !serverLevel.isRaining();

        int clearDuration = serverLevel.serverLevelData.getClearWeatherTime();
        int rainDuration = serverLevel.serverLevelData.getRainTime();
        int thunderDuration = serverLevel.serverLevelData.getThunderTime();

        if (isClear){
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, weatherComponent$adventure("Klar", serverLevel, clearDuration));
            }else {
                source.sendSuccess(weatherComponent("clear", serverLevel, clearDuration), false);
            }
        }else if (serverLevel.isRaining()){
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, weatherComponent$adventure("Regen", serverLevel, rainDuration));
            }else {
                source.sendSuccess(weatherComponent("rain", serverLevel, rainDuration), false);
            }
        }else {
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, weatherComponent$adventure("Gewitter", serverLevel, thunderDuration));
            }else {
                source.sendSuccess(weatherComponent("thunder", serverLevel, thunderDuration), false);
            }
        }
        return 1;
    }

    private static int setClear(CommandSourceStack source, int durationInSeconds) throws CommandSyntaxException{
        ServerLevel serverLevel = source.getLevel();

        serverLevel.setWeatherParameters(durationInSeconds,0, false, false);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, weatherSetComponent$adventure("Klar", serverLevel, durationInSeconds));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.weather.set.clear"), false);
        }
        return durationInSeconds;
    }

    private static int setRain(CommandSourceStack source, int durationInSeconds) throws CommandSyntaxException{
        ServerLevel serverLevel = source.getLevel();

        serverLevel.setWeatherParameters(0, durationInSeconds, true, false);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, weatherSetComponent$adventure("Regen", serverLevel, durationInSeconds));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.weather.set.rain"), false);
        }
        return durationInSeconds;
    }

    private static int setThunder(CommandSourceStack source, int durationInSeconds) throws CommandSyntaxException{
        ServerLevel serverLevel = source.getLevel();

        serverLevel.setWeatherParameters(0, durationInSeconds, true, true);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, weatherSetComponent$adventure("Gewitter", serverLevel, durationInSeconds));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.weather.set.thunder"), false);
        }
        return durationInSeconds;
    }




    private static Component weatherComponent$adventure(String weather, ServerLevel serverLevel, int durationInTicks){
        return Component.text("Das Wetter in der Welt ", SurfColors.INFO)
                .append(Component.text(serverLevel.dimension().location().toString(), SurfColors.TERTIARY))
                .append(Component.text(" ist ", SurfColors.INFO))
                .append(Component.text(weather, SurfColors.TERTIARY))
                .append(Component.text(" für ", SurfColors.INFO))
                .append(Component.text(EssentialsUtil.ticksToString(durationInTicks), SurfColors.TERTIARY));
    }

    private static Component weatherSetComponent$adventure(String weather, ServerLevel serverLevel, int durationInTicks){
        return Component.text("Das Wetter in der Welt ", SurfColors.INFO)
                .append(Component.text(serverLevel.dimension().location().toString(), SurfColors.TERTIARY))
                .append(Component.text(" wurde auf ", SurfColors.INFO))
                .append(Component.text(weather, SurfColors.TERTIARY))
                .append(Component.text(" für ", SurfColors.INFO))
                .append(Component.text(EssentialsUtil.ticksToString(durationInTicks), SurfColors.TERTIARY));
    }

    private static net.minecraft.network.chat.Component weatherComponent(String weather, ServerLevel serverLevel, int durationInTicks){
        return net.minecraft.network.chat.Component.literal("The weather in the world ")
                .withStyle(ChatFormatting.GRAY)
                .append(serverLevel.dimension().location().toString())
                .withStyle(ChatFormatting.GOLD)
                .append(net.minecraft.network.chat.Component.literal(" is "))
                .withStyle(ChatFormatting.GRAY)
                .append(net.minecraft.network.chat.Component.literal(weather))
                .withStyle(ChatFormatting.YELLOW)
                .append(net.minecraft.network.chat.Component.literal(" for "))
                .withStyle(ChatFormatting.GRAY)
                .append(net.minecraft.network.chat.Component.literal(EssentialsUtil.ticksToString(durationInTicks)))
                .withStyle(ChatFormatting.GREEN);
    }
}