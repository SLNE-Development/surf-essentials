package dev.slne.surf.essentials.commands.minecraft;

import com.saicone.rtag.Rtag;
import com.saicone.rtag.RtagEntity;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SummonCommand extends EssentialsCommand {

    public SummonCommand() {
        super("summon", "summon <entity> [<location>] [<nbt>]", "Summons an entity");

        withPermission(Permissions.SUMMON_PERMISSION);

        then(entityTypeArgument("entity")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> summon(
                        sender.getCallee(),
                        args.getUnchecked("entity"),
                        sender.getLocation(),
                        null,
                        true
                ))
                .then(locationArgument("pos")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> summon(
                                sender.getCallee(),
                                args.getUnchecked("entity"),
                                args.getUnchecked("pos"),
                                null,
                                true
                        ))
                        .then(nbtCompoundArgument("nbt")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> summon(
                                        sender.getCallee(),
                                        args.getUnchecked("entity"),
                                        args.getUnchecked("pos"),
                                        args.getUnchecked("nbt"),
                                        false
                                ))
                        )
                )
        );
    }

    private int summon(CommandSender source, EntityType entityType, Location pos, @Nullable Object nbt, boolean initialize) throws WrapperCommandSyntaxException {
        val entity = createEntity(entityType, pos, nbt, initialize);
        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(entity)
                .append(Component.text(" wurde gespawnt.", Colors.SUCCESS)));
        return 1;
    }

    public static @NotNull Entity createEntity(EntityType entityType, Location pos, @Nullable Object nbt, boolean initialize) throws WrapperCommandSyntaxException {
        val level = pos.getWorld();

        if (!EssentialsUtil.isInSpawnableBounds(pos)) throw Exceptions.ERROR_OUT_OF_WORLD;

        val spawnedEntity = level.spawnEntity(pos, entityType, initialize);

        if (nbt != null) {
            val entityTag = Rtag.INSTANCE.newTag(nbt);
            RtagEntity.edit(spawnedEntity, rtagEntity -> {
                Rtag.INSTANCE.set(entityTag, entityType.key().asString(), "id");
                rtagEntity.merge(entityTag, true);
            });
        }

        return spawnedEntity;
    }
}
