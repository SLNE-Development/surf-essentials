package dev.slne.surf.essentials.brigadier;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.brigadier.builder.general.*;
import me.lucko.commodore.Commodore;

public class GeneralTabComplete {
    SurfEssentials surf = SurfEssentials.getInstance();

    public void register(Commodore commodore){
        //worldChange command completions
        builder(commodore, "world", new WorldBuilder().worldChangeBuilder());

    }



    private void builder(Commodore commodore, String command, LiteralCommandNode commandNode){
        commodore.register(surf.getCommand(command), commandNode);
    }
}
