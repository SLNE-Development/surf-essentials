import dev.slne.surf.api.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

group = "dev.slne.surf.essentials"
version = findProperty("version") as String

surfPaperPluginApi {
    mainClass("dev.slne.surf.essentials.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)

    useCanvasMc()

    serverDependencies {
        registerRequired("LuckPerms")
    }

    authors.addAll("twisti", "red", "mikey")
}

dependencies {
    compileOnly("net.luckperms:api:5.4")
}