package dev.slne.surf.essentials.commands.general;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class NearCommand extends EssentialsCommand {
    public NearCommand() {
        super("near", "near [<radius>] [<entities>]", "Displays the players near to you", "nearby");

        withPermission(Permissions.NEAR_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> getNearEntities(sender, 200D, false));
        then(doubleArgument("radius", 1D, 1000D)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> getNearEntities(sender, args.getUnchecked("radius"), false))
                .then(literal("entities")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> getNearEntities(sender, args.getUnchecked("radius"), true))));
    }

    public int getNearEntities(NativeProxyCommandSender source, Double distance, boolean entities) {
        val callee = source.getCallee();
        val location = source.getLocation();

        val nearbyEntities = (entities) ?
                location.getWorld()
                        .getNearbyEntitiesByType(
                                LivingEntity.class,
                                location,
                                distance,
                                livingEntity -> EssentialsUtil.canSourceSeeEntity(callee, livingEntity)
                        )
                :
                location.getWorld()
                        .getNearbyEntitiesByType(
                                Player.class,
                                location,
                                distance,
                                player -> EssentialsUtil.canSourceSeeEntity(callee, player)
                        );

        EssentialsUtil.sendSuccess(callee, Component.text("%s in der Nähe: ".formatted((entities) ? "Entities" : "Spieler"), Colors.INFO)
                .append(Component.join(JoinConfiguration.commas(true), nearbyEntities.stream()
                        .map(livingEntity -> EssentialsUtil.getDisplayName(livingEntity)
                                .hoverEvent(HoverEvent.showText(Component.translatable(livingEntity.getType().translationKey(), Colors.INFO))))
                        .toList())));
        return nearbyEntities.size();
    }
}
