package dev.slne.surf.essentialsold.commands.minecraft;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.World;

import java.util.Objects;
import java.util.function.BiConsumer;

public class WeatherCommand extends EssentialsCommand {
    public WeatherCommand() {
        super("weather", "weather <clear | rain | thunder> [<duration>]", "Change game weather");

        withPermission(Permissions.WEATHER_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> queryWeather(sender));

        then(weatherTypeArgument("weather")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setWeather(
                        sender,
                        Objects.requireNonNull(args.getUnchecked("weather")),
                        6000
                ))
                .then(timeArgument("duration")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> setWeather(
                                sender,
                                Objects.requireNonNull(args.getUnchecked("weather")),
                                args.getUnchecked("duration")
                        ))
                )
        );
    }

    private int queryWeather(NativeProxyCommandSender source) {
        val callee = source.getCallee();
        val world = source.getWorld();
        val isClear = !world.isThundering() && !world.hasStorm();

        val clearDuration = world.getClearWeatherDuration();
        val rainDuration = world.getWeatherDuration();
        val thunderDuration = world.getThunderDuration();


        if (isClear) {
            EssentialsUtil.sendSuccess(callee, weatherComponent$adventure("Klar", world, clearDuration));
        } else if (world.isThundering()) {
            EssentialsUtil.sendSuccess(callee, weatherComponent$adventure("Gewitter", world, thunderDuration));
        } else {
            EssentialsUtil.sendSuccess(callee, weatherComponent$adventure("Regen", world, rainDuration));
        }
        return 1;
    }

    private int setWeather(NativeProxyCommandSender source, WeatherType weatherType, Integer durationInTicks) {
        val world = source.getWorld();
        weatherType.setWeather(world, durationInTicks);

        EssentialsUtil.sendSuccess(source.getCallee(), Component.text("Das Wetter in der Welt ", Colors.INFO)
                .append(EssentialsUtil.getDisplayName(world))
                .append(Component.text(" wurde auf ", Colors.INFO))
                .append(Component.text(weatherType.name, Colors.VARIABLE_VALUE))
                .append(Component.text(" für ", Colors.INFO))
                .append(Component.text(EssentialsUtil.ticksToString(durationInTicks), Colors.VARIABLE_VALUE))
                .append(Component.text(" gesetzt.", Colors.INFO)));

        return durationInTicks;
    }

    private Component weatherComponent$adventure(String weather, World world, int durationInTicks) {
        return Component.text("Das Wetter in der Welt ", Colors.INFO)
                .append(EssentialsUtil.getDisplayName(world))
                .append(Component.text(" ist ", Colors.INFO))
                .append(Component.text(weather, Colors.VARIABLE_VALUE))
                .append(Component.text(" für ", Colors.INFO))
                .append(Component.text(EssentialsUtil.ticksToString(durationInTicks), Colors.VARIABLE_VALUE));
    }

    /**
     * Weather types
     */
    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public enum WeatherType {
        CLEAR("Klar", (world, duration) -> {
            world.setThundering(false);
            world.setStorm(false);
            world.setClearWeatherDuration(duration);
        }),
        RAIN("Regen", (world, duration) -> {
            world.setThundering(false);
            world.setStorm(true);
            world.setWeatherDuration(duration);
        }),
        THUNDER("Gewitter", (world, duration) -> {
            world.setStorm(true);
            world.setThundering(true);
            world.setThunderDuration(duration);
        });

        String name; // Name of the weather type
        BiConsumer<World, Integer> setWeather; // Function to set the weather

        /**
         * Set the weather in the given world
         *
         * @param world           The world to set the weather in
         * @param durationInTicks The duration of the weather in ticks
         */
        public void setWeather(World world, int durationInTicks) {
            setWeather.accept(world, durationInTicks);
        }
    }
}
