package dev.slne.surf.essentials.commands.cheat;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class LatestDeathCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"latestdeath", "death", "deathlocation"};
    }

    @Override
    public String usage() {
        return "/death [<player>]";
    }

    @Override
    public String description() {
        return "Gets the latest death location";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.DEATH_LOCATION_PERMISSION_SELF, Permissions.DEATH_LOCATION_PERMISSION_OTHER));

        literal.executes(context -> getLocation(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException().getGameProfile())));

        literal.then(Commands.argument("player", GameProfileArgument.gameProfile())
                .requires(EssentialsUtil.checkPermissions(Permissions.DEATH_LOCATION_PERMISSION_OTHER))
                .executes(context -> getLocation(context.getSource(), GameProfileArgument.getGameProfiles(context, "player"))));
    }

    private int getLocation(CommandSourceStack source, Collection<GameProfile> profiles) throws CommandSyntaxException {
        if (profiles.size() > 1) throw EntityArgument.ERROR_NOT_SINGLE_PLAYER.create();
        var profile = profiles.iterator().next();
        var player = EssentialsUtil.getServerPlayer(profile.getId());
        var dataFile = EssentialsUtil.getPlayerFile(profile.getId());

        if (dataFile == null && player == null) throw EntityArgument.NO_PLAYERS_FOUND.create();

        int x, y, z;
        String dimension;

        if (player != null){
            var deathLocation = player.getLastDeathLocation();
            if (deathLocation.isEmpty()) throw ERROR_NOT_DIED.create(player.adventure$displayName);
            var deathPos = deathLocation.get();

            x = deathPos.pos().getX();
            y = deathPos.pos().getY();
            z = deathPos.pos().getZ();
            dimension = deathPos.dimension().location().toString();
        }else {
            try {
                var rawTag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), BinaryTagIO.Compression.GZIP);
                var deathTag = rawTag.getCompound("LastDeathLocation");
                var pos = deathTag.getIntArray("pos");
                var dimensionTag = deathTag.getString("dimension");

                if (pos.length == 0 || dimensionTag.isEmpty()) throw ERROR_NOT_DIED.create(Component.text(profile.getName()));

                x = pos[0];
                y = pos[1];
                z = pos[2];
                dimension = dimensionTag;
            } catch (IOException e) {
                throw new SimpleCommandExceptionType(PaperAdventure.asVanilla(Component.text("Something went wrong", Colors.RED))).create();
            }
        }

        if (source.isPlayer()) {
            EssentialsUtil.sendSuccess(source, Component.text(profile.getName(), Colors.TERTIARY)
                    .append(Component.text(" ist bei ", Colors.INFO))
                    .append(Component.text("%s %s %s".formatted(x, y, z), Colors.TERTIARY)
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Kopieren", Colors.INFO)))
                            .clickEvent(ClickEvent.copyToClipboard("%s %s %s".formatted(x, y, z))))
                    .append(Component.text(" in ", Colors.INFO))
                    .append(Component.text(dimension, Colors.SECONDARY))
                    .append(Component.text(" gestorben", Colors.INFO)));
        } else {
            EssentialsUtil.sendSystemMessage(source, Component.text(profile.getName(), Colors.TERTIARY)
                    .append(Component.text(" died at ", Colors.INFO))
                    .append(Component.text("%s %s %s".formatted(x, y, z), Colors.TERTIARY))
                    .append(Component.text(" in ", Colors.INFO))
                    .append(Component.text(dimension, Colors.SECONDARY)));
        }
        return 1;
    }

    private static final DynamicCommandExceptionType ERROR_NOT_DIED = new DynamicCommandExceptionType(player -> PaperAdventure.asVanilla(
            ((Component) player).colorIfAbsent(Colors.TERTIARY)
                    .append(Component.text(" has not died yet", Colors.RED))
    ));
}
