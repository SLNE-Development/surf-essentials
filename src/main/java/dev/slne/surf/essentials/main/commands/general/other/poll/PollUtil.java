package dev.slne.surf.essentials.main.commands.general.other.poll;

public abstract class PollUtil {
    private static boolean poll;

    public static void setPoll(Boolean bool){
        poll = bool;
    }

    public static Boolean isPoll(){
        return poll;
    }
}
