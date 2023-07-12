package dev.slne.surf.essentials.commands.minecraft;

import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class SeedCommand extends EssentialsCommand {
    public SeedCommand() {
        super("seed", "seed", "Shows you the world seed", "seed");

        withPermission(Permissions.SEED_PERMISSION);
        executesNative((sender, args) -> {
            val world = sender.getWorld();
            val seed = world.getSeed();

            EssentialsUtil.sendSuccess(sender.getCallee(), Component.text("Der Seed in der Welt ", Colors.SUCCESS)
                    .append(EssentialsUtil.getDisplayName(world))
                    .append(Component.text(" ist [", Colors.GREEN))
                    .append(Component.text(seed, Colors.VARIABLE_VALUE)
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke zum kopieren", Colors.INFO)))
                            .clickEvent(ClickEvent.copyToClipboard(String.valueOf(seed))))
                    .append(Component.text("]", Colors.GREEN)));

            return 1;
        });
    }
}
