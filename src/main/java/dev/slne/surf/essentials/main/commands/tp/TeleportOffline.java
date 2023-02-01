package dev.slne.surf.essentials.main.commands.tp;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
import net.kyori.adventure.nbt.*;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.UUID;

import static net.kyori.adventure.nbt.BinaryTagIO.Compression.GZIP;

@PermissionTag(name = Permissions.OFFLINE_TELEPORT_PERMISSION, desc = "Allows you to teleport offline players")
public class TeleportOffline {
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType(net.minecraft.network.chat.Component.translatable("argument.player.toomany"));
    public static final SimpleCommandExceptionType NO_PLAYERS_FOUND = new SimpleCommandExceptionType(net.minecraft.network.chat.Component.translatable("argument.entity.notfound.player"));

    public TeleportOffline() {
        SurfEssentials.registerPluginBrigadierCommand("tpoff", this::literal).setUsage("/tpoff <player> [<location>]");
    }

    private void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.OFFLINE_TELEPORT_PERMISSION));

        literal.then(Commands.argument("player", GameProfileArgument.gameProfile())
                .executes(context -> teleportToPlayer(context, GameProfileArgument.getGameProfiles(context, "player")))
                .then(Commands.argument("location", Vec3Argument.vec3())
                        .executes(context -> teleportPlayerToLocation(context, GameProfileArgument.getGameProfiles(context, "player"),
                                Vec3Argument.getVec3(context, "location")))));
    }

    private int teleportToPlayer(CommandContext<CommandSourceStack> context, Collection<GameProfile> gameProfiles) throws CommandSyntaxException {
        if (gameProfiles.size() > 1) {
            throw ERROR_NOT_SINGLE_PLAYER.createWithContext(new StringReader(context.getInput()));
        }

        CommandSourceStack source = context.getSource();
        ServerPlayer player = context.getSource().getPlayerOrException();
        GameProfile profile = gameProfiles.iterator().next();
        UUID uuid = profile.getId();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            throw NO_PLAYERS_FOUND.create();
        }

        if (offlinePlayer.isOnline()) {
            ServerPlayer target = ((CraftPlayer) offlinePlayer.getPlayer()).getHandle();
            player.teleportTo(target.getLevel(), target.getX(), target.getY(), target.getZ(), target.getBukkitYaw(), target.getRotationVector().y);

        }else {

            SurfApi.getUser(player.getUUID()).thenAccept(user -> user.sendMessage(Component.text("Spielerdaten laden...", SurfColors.INFO)));

            try {
                player.getBukkitEntity().teleportAsync(getLocation(uuid), PlayerTeleportEvent.TeleportCause.COMMAND);
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }

        EssentialsUtil.sendSuccess(source, Component.text("Du hast dich zu ", SurfColors.SUCCESS)
                .append(Component.text(profile.getName(), SurfColors.TERTIARY))
                .append(Component.text(" teleportiert!", SurfColors.SUCCESS)));

        return 1;
    }

    private int teleportPlayerToLocation(CommandContext<CommandSourceStack> context, Collection<GameProfile> gameProfiles, Vec3 newLocation) throws CommandSyntaxException {
        if (gameProfiles.size() > 1) {
            throw ERROR_NOT_SINGLE_PLAYER.createWithContext(new StringReader(context.getInput()));
        }

        CommandSourceStack source = context.getSource();
        ServerPlayer player = context.getSource().getPlayerOrException();
        GameProfile profile = gameProfiles.iterator().next();
        UUID uuid = profile.getId();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);


        if (!offlinePlayer.hasPlayedBefore()) {
            throw NO_PLAYERS_FOUND.create();
        }

        if (offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().teleportAsync(new Location(offlinePlayer.getPlayer().getWorld(), newLocation.x(), newLocation.y(), newLocation.z()));

        } else {

            SurfApi.getUser(player.getUUID()).thenAccept(user -> user.sendMessage(Component.text("Teleportiere Spieler...", SurfColors.INFO)));

            try {
                setLocation(offlinePlayer, new Location(player.getLevel().getWorld(), newLocation.x(), newLocation.y(), newLocation.z()));
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }

        double posX = Double.parseDouble(new DecimalFormat("#.##").format(newLocation.x()));
        double posY = Double.parseDouble(new DecimalFormat("#.##").format(newLocation.y()));
        double posZ = Double.parseDouble(new DecimalFormat("#.##").format(newLocation.z()));

        EssentialsUtil.sendSuccess(source, Component.text("Der Spieler ", SurfColors.SUCCESS)
                .append(Component.text(profile.getName(), SurfColors.TERTIARY))
                .append(Component.text(" wurde zu ", SurfColors.SUCCESS))
                .append(Component.text("%s %s %s".formatted(posX, posY, posZ), SurfColors.TERTIARY))
                .append(Component.text(" teleportiert!", SurfColors.SUCCESS)));

        return 1;
    }


    private Location getLocation(UUID uuid) throws IOException {
        File dataFile = EssentialsUtil.getPlayerFile(uuid);

        if (dataFile == null) return null;
        CompoundBinaryTag tag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), GZIP);
        ListBinaryTag posTag = tag.getList("Pos");
        ListBinaryTag rotTag = tag.getList("Rotation");

        long worldUUIDMost = tag.getLong("WorldUUIDMost");
        long worldUUIDLeast = tag.getLong("WorldUUIDLeast");

        World world = Bukkit.getWorld(new UUID(worldUUIDMost, worldUUIDLeast));

        return new Location(world, posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2), rotTag.getFloat(0), rotTag.getFloat(1));
    }

    private void setLocation(OfflinePlayer player, Location location) throws IOException{
        UUID uuid = player.getUniqueId();
        File dataFile = EssentialsUtil.getPlayerFile(uuid);

        if (dataFile == null) return;
        CompoundBinaryTag rawTag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), GZIP);
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder().put(rawTag);

        ListBinaryTag.Builder<BinaryTag> posTag = ListBinaryTag.builder();
        posTag.add(DoubleBinaryTag.of(location.getX()));
        posTag.add(DoubleBinaryTag.of(location.getY()));
        posTag.add(DoubleBinaryTag.of(location.getZ()));

        ListBinaryTag.Builder<BinaryTag> rotTag = ListBinaryTag.builder();
        rotTag.add(FloatBinaryTag.of(location.getYaw()));
        rotTag.add(FloatBinaryTag.of(location.getPitch()));

        builder.put("Pos", posTag.build());
        builder.put("Rotation", rotTag.build());

        BinaryTagIO.writer().write(builder.build(), dataFile.toPath(), GZIP);
    }
}
