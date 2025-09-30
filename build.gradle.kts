plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.essentials.PaperMain")
    bootstrapper("dev.slne.surf.cloud.bukkit.BukkitBootstrap")
    authors.addAll("twisti", "red")

    runServer {
        jvmArgs("-Dsurf.cloud.serverName=test-server01")
    }
}