package dev.slne.surf.essentials.brigadier;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.brigadier.builder.general.*;
import me.lucko.commodore.Commodore;

public class GeneralTabComplete {
    SurfEssentials surf = SurfEssentials.getInstance();

    public void register(Commodore commodore){
        //alert command completions
        builder(commodore, "alert", new AlertBuilder().alertBuilder());
        //gamemode command completions
        builder(commodore, "gamemode", new GamemodeBuilder().gamemodeBuilder());
        //info command completions
        builder(commodore, "information", new InfoBuilder().infoBuilder());
        //time command completions
        builder(commodore, "time", new TimeBuilder().timeBuilder());
        //msg command completions
        builder(commodore, "msg", new MsgBuilder().msgBuilder());
        //rule command completions
        builder(commodore, "rule", new RuleBuilder().ruleBuilder());
        //weather command completions
        builder(commodore, "weather", new WeatherBuilder().weatherBuilder());
        //spawner command completions
        commodore.register(new SpawnerBuilder().spawnerBuilder());

    }



    private void builder(Commodore commodore, String command, LiteralCommandNode commandNode){
        commodore.register(surf.getCommand(command), commandNode);
    }
}
