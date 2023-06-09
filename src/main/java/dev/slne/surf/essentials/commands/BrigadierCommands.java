package dev.slne.surf.essentials.commands;

import com.mojang.brigadier.tree.CommandNode;
import dev.slne.surf.essentials.commands.cheat.*;
import dev.slne.surf.essentials.commands.cheat.gui.*;
import dev.slne.surf.essentials.commands.general.*;
import dev.slne.surf.essentials.commands.general.other.ActionbarBroadcast;
import dev.slne.surf.essentials.commands.general.other.TimerCommand;
import dev.slne.surf.essentials.commands.general.other.TitlebroadcastCommand;
import dev.slne.surf.essentials.commands.general.other.help.HelpCommand;
import dev.slne.surf.essentials.commands.general.other.poll.PollCommand;
import dev.slne.surf.essentials.commands.general.other.poll.VoteCommand;
import dev.slne.surf.essentials.commands.general.other.troll.TrollManager;
import dev.slne.surf.essentials.commands.general.other.world.WorldCommand;
import dev.slne.surf.essentials.commands.minecraft.*;
import dev.slne.surf.essentials.commands.minecraft.internal.SpawnArmorTrimsCommand;
import dev.slne.surf.essentials.commands.tp.*;
import dev.slne.surf.essentials.utils.EssentialsUtil;

/**
 * This class is responsible for registering and unregistering all the commands provided by the plugin using Brigadier.
 *
 * @author twisti
 * @since 1.0.0
 */
public class BrigadierCommands {

    /**
     * Registers all the commands provided by the plugin using Brigadier.
     */
    public void register() {
        new BroadcastWorldCommand();
        new ListCommand();
        new LightningCommand();
        new KillCommand();
        new EnchantCommand();
        new WorkbenchCommand();
        new AnvilCommand();
        new SmithingTableCommand();
        new CartographyTableCommand();
        new GrindstoneCommand();
        new LoomCommand();
        new StonecutterCommand();
        new GiveCommand();
        new GetPosCommand();
        new TrollManager();
        new HurtCommand();
        new BossbarCommand();
        new OpCommand();
        new DeopCommand();
        new EffectCommand();
        new DefaultGamemodeCommand();
        new DifficultyCommand();
        new ExperienceCommand();
        new ForceloadCommand();
        new GamemodeCommand();
        new GameruleCommand();
        new ParticleCommand();
        new FillStackCommand();
        new FlyCommand();
        new FoodCommand();
        new GodmodeCommand();
        new HealCommand();
        new RepairCommand();
        new SuicideCommand();
        new TimeCommand();
        new WeatherCommand();
        new PollCommand();
        new VoteCommand();
        new AlertCommand();
        new BookCommand();
        new InfoCommand();
        new RuleCommand();
        new SpawnerChangeCommand();
        new TitlebroadcastCommand();
        new ActionbarBroadcast();
        new TeleportOffline();
        new SetItemNameCommand();
        new SetItemLoreCommand();
        new TeleportBackCommand();
        new RandomTeleportCommand();
        new ClearItemCommand();
        new InfinityCommand();
        new UnhatCommand();
        new HatCommand();
        new TimerCommand();
        new ChatClearCommand();
        new TeleportCommand();
        new ClearInventoryCommand();
        new HelpCommand();
        new SeedCommand();
        new SetWorldSpawnCommand();
        new SpectateCommand();
        new SummonCommand();
        new TeleportToTopCommand();
        new WorldCommand();
        new FillCommand();
        new SetBlockCommand();
        new RideCommand();
        new DamageCommand();
        new SpawnCommand();
        new LatestDeathCommand();
        new PlayerHeadCommand();
        new TrashCommand();
        new SpeedCommand();
        new WhitelistCommand();
        new ScreamCommand();
        new NearCommand();
        new DataPackCommand();
        new AttributeCommand();
        new ReloadCommand();
        new FillBiomeCommand();
        new SpawnArmorTrimsCommand();
        new ExecuteCommand();
        new SetPlayerIdleTimeoutCommand();
        new PlaySoundCommand();
        new AdvancementCommand();
    }

    /**
     * Unregisters all commands registered by this plugin from the server's {@link com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack>}.
     * <p>
     * This should be called in the plugins {@link org.bukkit.plugin.Plugin#onDisable()} method to ensure that no remaining {@link CommandNode<net.minecraft.commands.CommandSourceStack>}s
     * exist on the server after the plugin has been unloaded.
     */
    public synchronized void unregister() {
        EssentialsUtil.unregisterDispatcherCommand(EssentialsUtil.getRegisteredCommands().stream().map(CommandNode::getName).toList());
    }
}
