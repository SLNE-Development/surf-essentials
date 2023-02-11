package dev.slne.surf.essentials.commands.minecraft;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;

@PermissionTag(name = Permissions.SEED_PERMISSION, desc = "Allows you to get the world seed")
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

    private int getSeed(CommandSourceStack source) throws CommandSyntaxException{
        long seed = source.getLevel().getSeed();

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Der Seed in ", SurfColors.SUCCESS)
                    .append(Component.text(source.getLevel().dimension().location().toString(), SurfColors.SECONDARY))
                    .append(Component.text(" ist [", SurfColors.SUCCESS))
                    .append(Component.text(seed, SurfColors.TERTIARY)
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke zum kopieren", SurfColors.INFO)))
                            .clickEvent(ClickEvent.copyToClipboard(String.valueOf(seed))))
                    .append(Component.text("]", SurfColors.SUCCESS)));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.seed.success",
                    net.minecraft.network.chat.Component.literal(String.valueOf(seed)).withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }
}
