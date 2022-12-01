package dev.slne.surf.essentials.brigadier;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.brigadier.builder.cheat.*;
import me.lucko.commodore.Commodore;

public class CheatTabComplete {
    SurfEssentials surf = SurfEssentials.getInstance();

    public void register(Commodore commodore){
        //Speed command completions
        builder(commodore, "speed", new SpeedBuilder().speedBuilder());
        //more command completions
        builder(commodore, "more", new FillStackBuilder().fillStackBuilder());
        //fly command completions
        builder(commodore, "fly", new FlyBuilder().flyBuilder());
        //food command completions
        builder(commodore, "feed", new FoodBuilder().foodBuilder());
        //godmode command completions
        builder(commodore, "godmode", new GodmodeBuilder().godmodeBuilder());
        //heal command completions
        builder(commodore, "heal", new HealBuilder().healBuilder());
        //repair command completions
        builder(commodore, "repair", new RepairBuilder().repairBuilder());
        //suicide command completions
        builder(commodore, "suicide", new SuicideBuilder().suicideBuilder());
    }



    private void builder(Commodore commodore, String command, LiteralCommandNode commandNode){
        commodore.register(surf.getCommand(command), commandNode);
    }
}
