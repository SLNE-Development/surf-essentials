package dev.slne.surf.essentials.commands.minecraft;


/**
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.jorel.commandapi.AbstractArgumentTree;
import dev.jorel.commandapi.Brigadier;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPIHandler;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.registries.Registries;
import org.bukkit.command.CommandSender;

public class FillBiomeCommand2 extends EssentialsCommand {
    public FillBiomeCommand2() {
        super("fillbiome", "fillBiome <from> <to> [<replace <filter>>]", "Change the biom from blocks");

        withPermission(Permissions.FILL_BIOME_PERMISSION);

        RequiredArgumentBuilder.argument("replace", ResourceOrTagArgument.resourceOrTag(EssentialsUtil.buildContext(), Registries.BIOME));

        then(locationArgument("from",  LocationType.BLOCK_POSITION)
                .then(locationArgument("to", LocationType.BLOCK_POSITION)
                        .then(biomeArgument("biome")
                                .executes((sender, args) -> )
                                .then(literal("replace")
                                        .then(BoolArgumentType.bool())))
                ));

    }
}
*/