package dev.slne.surf.essentials;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class SurfEssentialsLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        addDependency(resolver, "com.github.stefvanschie.inventoryframework", "IF", "0.10.8");
        addDependency(resolver, "net.kyori", "adventure-nbt", "4.12.0");
        addDependency(resolver, "io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT");

        addRepository(resolver, "jitpack.io", "https://jitpack.io");
        addRepository(resolver, "papermc", "https://repo.papermc.io/repository/maven-public/");

        classpathBuilder.addLibrary(resolver);
    }

    private void addDependency(MavenLibraryResolver resolver, String groupId, String artifactId, String version) {
        resolver.addDependency(new Dependency(new DefaultArtifact("%s:%s:%s".formatted(groupId, artifactId, version)), null));
    }

    private void addRepository(MavenLibraryResolver resolver, String id, String url) {
        resolver.addRepository(new RemoteRepository.Builder(id, "default", url).build());
    }
}
