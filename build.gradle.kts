import dev.slne.surf.surfapi.gradle.util.registerSoft
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne.surf.essentials"
version = findProperty("version") as String

dependencies {
    compileOnly("dev.slne.surf.settings:surf-settings-api-common:1.21.10-1.0.1")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.essentials.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)

    serverDependencies {
        registerSoft(
            "surf-settings-paper",
            loadOrder = PaperPluginDescription.RelativeLoadOrder.BEFORE
        )
    }

    authors.addAll("twisti", "red")
}