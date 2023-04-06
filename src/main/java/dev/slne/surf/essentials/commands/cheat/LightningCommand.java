package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class LightningCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"lighting"};
    }

    @Override
    public String usage() {
        return "/lighting <players> [<amount>]";
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission(Permissions.LIGHTING_PERMISSION));

        literal.then(Commands.argument("players", EntityArgument.players())
                .executes(context -> lightingCustom(context, EntityArgument.getPlayers(context, "players"), new AtomicInteger(1)))

                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 20))
                        .executes(context -> lightingCustom(context, EntityArgument.getPlayers(context, "players"), new AtomicInteger(IntegerArgumentType.getInteger(context, "amount"))))));
    }


    private int lightingCustom(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targetsUnchecked, AtomicInteger power) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(context.getSource(), targetsUnchecked);

        for (ServerPlayer serverPlayer : targets) {
            Player player = serverPlayer.getBukkitEntity();
            player.setPlayerWeather(WeatherType.DOWNFALL);

            Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
                if (power.get() < 1) bukkitTask.cancel();
                player.getWorld().strikeLightning(player.getLocation());
                power.getAndDecrement();
            }, 20, 5);

            Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask ->
                    player.resetPlayerWeather(), 20L * power.get() + 40);
        }

        if (context.getSource().isPlayer()){
            if (targets.size() == 1){
                EssentialsUtil.sendSuccess(context.getSource(), Component.text("Der Blitz hat ", Colors.SUCCESS)
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                        .append(Component.text(" getroffen!", Colors.SUCCESS)));
            }else {
                EssentialsUtil.sendSuccess(context.getSource(), Component.text("Der Blitz hat ", Colors.SUCCESS)
                        .append(Component.text(targets.size(), Colors.TERTIARY))
                        .append(Component.text(" Spieler getroffen.", Colors.SUCCESS)));
            }
        }else {
            if (targets.size() == 1){
                context.getSource().sendSuccess(PaperAdventure.asVanilla(Component.text("Lightning has struck ", Colors.GREEN)
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY))), false);
            }else {
                context.getSource().sendSuccess(PaperAdventure.asVanilla(Component.text("Lightning has struck ", Colors.GREEN)
                        .append(Component.text(targets.size(), Colors.TERTIARY))
                        .append(Component.text(" players", Colors.GREEN))), false);
            }
        }

        return 1;
    }
}
