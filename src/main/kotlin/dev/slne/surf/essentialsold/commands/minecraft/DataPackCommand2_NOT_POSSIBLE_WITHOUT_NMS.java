package dev.slne.surf.essentialsold.commands.minecraft;

/**
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.nms.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.datapack.Datapack;
import io.papermc.paper.datapack.DatapackManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.util.TriState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.packs.repository.Pack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.packs.DataPack;

import java.util.Collection;
import java.util.List;

public class DataPackCommand2 extends EssentialsCommand {
    public DataPackCommand2() {
        super("datapack", "datapack <list | enable | disable>", "Manage datapacks");

        withPermission(Permissions.DATAPACK_PERMISSION);

        then(literal("enable")
                .then(datapackArgument("datapack", TriState.FALSE)
                        .executes((sender, args) -> {
                            enablePack(
                                    sender,
                                    args.getUnchecked("datapack"),
                                    (profiles, profile) -> profile.
                            )
                        })));


    }

    private int list(CommandSender source) {
        return listEnabled(source) + listAvailable(source);
    }

    private int listEnabled(CommandSender source) {
        final DatapackManager datapackManager = source.getServer().getDatapackManager();
        final Collection<Datapack> packs = datapackManager.getEnabledPacks();

        if (packs.isEmpty()) {
            EssentialsUtil.sendError(source, "Es sind keine Datapacks aktiviert");

        } else {
            EssentialsUtil.sendSuccess(source, Component.text("Es sind ", Colors.INFO)
                    .append(Component.text(packs.size(), Colors.VARIABLE_VALUE))
                    .append(Component.text(" Datapacks aktiviert: ", Colors.INFO))
                    .append(Component.join(JoinConfiguration.commas(true), packs.stream()
                            .map(EssentialsUtil::getDisplayName)
                            .toList())));
        }
        return packs.size();
    }

    private int listAvailable(CommandSender source) {
        final DatapackManager datapackManager = source.getServer().getDatapackManager();
        final Collection<Datapack> allPacks = datapackManager.getPacks();
        final Collection<Datapack> enabledPacks = datapackManager.getEnabledPacks();
        final List<Datapack> disabledPacks = allPacks.stream()
                .filter(datapack -> !enabledPacks.contains(datapack))
                .toList();

        if (disabledPacks.isEmpty()) {
            EssentialsUtil.sendError(source, "Es sind keine weiteren Datapacks verfügbar");

        } else {
            EssentialsUtil.sendSuccess(source, Component.text("Es sind ", Colors.INFO)
                    .append(Component.text(disabledPacks.size(), Colors.VARIABLE_VALUE))
                    .append(Component.text(" Datapacks verfügbar: ", Colors.INFO))
                    .append(Component.join(JoinConfiguration.commas(true), disabledPacks.stream()
                            .map(EssentialsUtil::getDisplayName)
                            .toList())));
        }
        return disabledPacks.size();
    }

    private int disablePack(CommandSender source, Datapack pack) {
        EssentialsUtil.sendInfo(source, Component.text("Deaktiviere Datapack ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(pack, true)));

        pack.setEnabled(false);
        return 1;
    }

    private int enablePack(CommandSender source, Datapack pack, Inserter packAdder) throws CommandSyntaxException {
        final var packRepository = source.getServer().getDatapackManager();

        Collection<Datapack> packs = packRepository.getPacks();
        packAdder.apply(packs, pack);

        EssentialsUtil.sendSuccess(source, Component.text("Aktiviere Datapack ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(pack)));

        CommandAPI.reloadDatapacks();
        return packs.size();
    }


    private static Datapack getPack(CommandArguments arguments, String name, boolean enable) throws WrapperCommandSyntaxException {
        final String packName = arguments.getUnchecked(name);
        final DatapackManager datapackManager = Bukkit.getDatapackManager();
        final Datapack datapack = datapackManager.getPacks()
                .stream()
                .filter(pack -> pack.getName().equals(packName))
                .findFirst()
                .orElseThrow(() -> Exceptions.ERROR_UNKNOWN_DATA_PACK.create(packName));

        final boolean isSelectedPack = datapackManager.getEnabledPacks().contains(datapack);
        final Datapack.Compatibility compatibility = datapack.getCompatibility();

        if (enable && isSelectedPack) throw Exceptions.ERROR_DATA_PACK_ALREADY_ENABLED.create(datapack);
        if (!enable && !isSelectedPack) throw Exceptions.ERROR_DATA_PACK_ALREADY_DISABLED.create(datapack);
        if (compatibility != Datapack.Compatibility.COMPATIBLE) throw Exceptions.ERROR_DATA_PACK_INCOMPATIBLE.create(datapack);

        return datapack;
    }

    private static final SuggestionProvider<CommandSourceStack> UNSELECTED_PACKS = (context, builder) -> {
        final var packRepository = context.getSource().getServer().getPackRepository();
        final var collection = packRepository.getSelectedIds();
        final var featureFlagSet = context.getSource().enabledFeatures();

        return SharedSuggestionProvider.suggest(packRepository.getAvailablePacks()
                .stream()
                .filter((profile) -> profile.getRequestedFeatures().isSubsetOf(featureFlagSet))
                .map(Pack::getId)
                .filter((name) -> !collection.contains(name))
                .map(StringArgumentType::escapeIfRequired), builder);
    };

    private static final SuggestionProvider<CommandSourceStack> SELECTED_PACKS = (context, builder) ->
            SharedSuggestionProvider.suggest(context.getSource().getServer().getPackRepository().getSelectedIds()
                    .stream()
                    .map(StringArgumentType::escapeIfRequired), builder);

    @FunctionalInterface
    private interface Inserter {
        void apply(Collection<Datapack> profiles, Datapack profile) throws CommandSyntaxException;
    }
}
 */
