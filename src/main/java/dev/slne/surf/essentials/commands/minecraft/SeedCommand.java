package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;

public class SeedCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"seed"};
    }

    @Override
    public String usage() {
        return "/seed";
    }

    @Override
    public String description() {
        return "Shows you the world seed";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.SEED_PERMISSION));

        literal.executes(context -> getSeed(context.getSource()));
    }

    private int getSeed(CommandSourceStack source) {
        long seed = source.getLevel().getSeed();

        EssentialsUtil.sendSourceSuccess(source, Component.text("Der Seed in ", Colors.SUCCESS)
                .append(Component.text(source.getLevel().dimension().location().toString(), Colors.SECONDARY))
                .append(Component.text(" ist [", Colors.SUCCESS))
                .append(Component.text(seed, Colors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum kopieren", Colors.INFO)))
                        .clickEvent(ClickEvent.copyToClipboard(String.valueOf(seed))))
                .append(Component.text("]", Colors.SUCCESS)));

        return 1;
    }
}
