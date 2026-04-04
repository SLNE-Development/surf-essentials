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

    authors.addAll("twisti", "red", "mikey")
}