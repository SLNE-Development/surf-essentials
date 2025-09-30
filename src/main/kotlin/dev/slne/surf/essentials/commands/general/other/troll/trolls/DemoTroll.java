package dev.slne.surf.essentials.commands.general.other.troll.trolls;

/**
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.commands.general.other.troll.Troll;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DemoTroll extends Troll {
    @Override
    public String name() {
        return "demo";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_DEMO_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> makeDemo(context.getSource(), EntityArgument.getPlayer(context, "player")));
    }

    private int makeDemo(@NotNull CommandSourceStack source, ServerPlayer targetPlayer) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(source, targetPlayer);
        Player target = targetPlayer.getBukkitEntity();

        EssentialsUtil.sendPackets(target, new ClientboundGameEventPacket(ClientboundGameEventPacket.DEMO_EVENT, ClientboundGameEventPacket.DEMO_PARAM_INTRO));
        target.playSound(target.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2F, 1F);

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                .append(Component.text(" wurde die Demo gezeigt!", Colors.SUCCESS)));

        return 1;
    }
}
 */
