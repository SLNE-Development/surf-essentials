package dev.slne.surf.essentials;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.bukkit.event.Listener;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class SurfEssentialsLoader implements PluginLoader, Listener {
    private final MavenLibraryResolver resolver;

    public SurfEssentialsLoader() {
        resolver = new MavenLibraryResolver();
    }

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        addDependency("net.kyori", "adventure-nbt", "4.14.0");
//        addDependency("com.github.retrooper.packetevents", "spigot", "2.3.0");
        addDependency("com.saicone.rtag", "rtag", "1.5.3");
        addDependency("com.saicone.rtag", "rtag-entity", "1.5.3");

        addRepository("papermc", "https://repo.papermc.io/repository/maven-public/");
        addRepository("jitpack.io", "https://jitpack.io");
        addRepository("codemc-releases", "https://repo.codemc.io/repository/maven-releases/");

        classpathBuilder.addLibrary(resolver);
    }

    private void addDependency(String groupId, String artifactId, String version) {
        resolver.addDependency(new Dependency(new DefaultArtifact("%s:%s:%s".formatted(groupId, artifactId, version)), null));
    }

    private void addRepository(String id, String url) {
        resolver.addRepository(new RemoteRepository.Builder(id, "default", url).build());
    }
}
