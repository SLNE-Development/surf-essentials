package dev.slne.surf.essentials.commands;

import com.mojang.brigadier.tree.CommandNode;
import dev.slne.surf.essentials.commands.cheat.*;
import dev.slne.surf.essentials.commands.cheat.gui.*;
import dev.slne.surf.essentials.commands.general.*;
import dev.slne.surf.essentials.commands.general.other.ActionbarBroadcast;
import dev.slne.surf.essentials.commands.general.other.TimerCommand;
import dev.slne.surf.essentials.commands.general.other.TitlebroadcastCommand;
import dev.slne.surf.essentials.commands.general.other.poll.PollCommand;
import dev.slne.surf.essentials.commands.general.other.poll.VoteCommand;
import dev.slne.surf.essentials.commands.general.other.troll.TrollManager;
import dev.slne.surf.essentials.commands.general.other.world.WorldCommand;
import dev.slne.surf.essentials.commands.general.sign.SignToggleCommand;
import dev.slne.surf.essentials.commands.minecraft.*;
import dev.slne.surf.essentials.commands.tp.*;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import net.minecraft.commands.CommandSourceStack;

public class BrigadierCommands {
    public synchronized void register(){
        //world-broadcast command
        BroadcastWorldCommand.register();
        //list command
        ListCommand.register();
        //lighting command
        LightningCommand.register();
        //kill command
        KillCommand.register();
        //enchant command
        EnchantCommand.register();
        //workbench command
        WorkbenchCommand.register();
        //anvil command
        AnvilCommand.register();
        //smithing table command
        SmithingTableCommand.register();
        //cartographytable command
        CartographyTableCommand.register();
        //grindtsone command
        GrindstoneCommand.register();
        //loom command
        LoomCommand.register();
        //stonecutter
        StonecutterCommand.register();
        //give command
        GiveCommand.register();
        //getpos command
        GetPosCommand.register();
        //troll command
        TrollManager.register();
        //hurt command
        HurtCommand.register();
        //bossbar command
        BossbarCommand.register();
        //op command
        OpCommand.register();
        //deop command
        DeopCommand.register();
        //effect command
        EffectCommand.register();
        //default game mode command
        DefaultGamemodeCommand.register();
        //difficulty command
        DifficultyCommand.register();
        //experience command
        ExperienceCommand.register();
        //forceload command
        ForceloadCommand.register();
        //game mode command
        GamemodeCommand.register();
        //gamerule command
        GameruleCommand.register();
        //particle command
        ParticleCommand.register();
        //fill stack command
        FillStackCommand.register();
        //fly command
        FlyCommand.register();
        //feed command
        FoodCommand.register();
        //godmode command
        GodmodeCommand.register();
        //heal command
        HealCommand.register();
        //repair command
        RepairCommand.register();
        //speed command
        SpeedCommand.register();
        // suicide command
        SuicideCommand.register();
        //time command
        TimeCommand.register();
        //weather command
        WeatherCommand.register();
        //poll command
        PollCommand.register();
        //vote command
        VoteCommand.register();
        //alert command
        AlertCommand.register();
        //book command
        BookCommand.register();
        //info command
        InfoCommand.register();
        //rule command
        RuleCommand.register();
        //spawner command
        SpawnerChangeCommand.register();
        //title-broadcast command
        TitlebroadcastCommand.register();
        //actionbar-broadcast command
        new ActionbarBroadcast();
        //teleport offline
        new TeleportOffline();
        // set item name command
        new SetItemNameCommand();
        // set item lore command
        new SetItemLoreCommand();
        // back command
        new TeleportBackCommand();
        // random teleport
        new RandomTeleportCommand();
        // clear item command
        new ClearItemCommand();
        // infinity command
        new InfinityCommand();
        // unhat command
        new UnhatCommand();
        // hat command
        new HatCommand();
        // timer command
        new TimerCommand();
        // chat clear command
        new ChatClearCommand();
        // teleport command
        new TeleportCommand();
        // clear inventory command
        new ClearInventoryCommand();
        // help command
        new HelpCommand();
        // seed command
        new SeedCommand();
        // set world spawn command
        new SetWorldSpawnCommand();
        // spectate command
        new SpectateCommand();
        // summon command
        new SummonCommand();
        // tptop command
        new TeleportToTopCommand();
        // sign toggle command
        new SignToggleCommand();
        // world command
        new WorldCommand();
        // fill command
        new FillCommand();
        // set block command
        new SetBlockCommand();
        // ride commands
        new RideCommand();
        // damage command
        new DamageCommand();
    }

    public synchronized void unregister() {
        for (CommandNode<CommandSourceStack> registeredCommand : EssentialsUtil.getRegisteredCommands()) {
            EssentialsUtil.sendDebug("Unregistering command: " + registeredCommand.getName());
            EssentialsUtil.getRoot().removeCommand(registeredCommand.getName());
        }
    }
}
