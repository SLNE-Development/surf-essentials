import dev.slne.surf.api.gradle.util.registerRequired
import dev.slne.surf.api.gradle.util.withSurfApiBukkit

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

    runServer {
        withSurfApiBukkit()
    }
}

runPaper {
    folia.registerTask()
}

dependencies {
    compileOnly("net.luckperms:api:5.4")
}