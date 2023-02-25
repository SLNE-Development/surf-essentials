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

        resolver.addDependency(new Dependency(new DefaultArtifact("com.github.stefvanschie.inventoryframework:IF:0.10.8"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.comphenix.protocol:ProtocolLib:4.8.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("net.kyori:adventure-nbt:4.12.0"), null));

        resolver.addRepository(new RemoteRepository.Builder("dmulloy2-repo", "default", "https://repo.dmulloy2.net/repository/public/").build());
        resolver.addRepository(new RemoteRepository.Builder("jitpack.io", "default", "https://jitpack.io").build());
        resolver.addRepository(new RemoteRepository.Builder("papermc", "default", "https://repo.papermc.io/repository/maven-public/").build());
        resolver.addRepository(new RemoteRepository.Builder("slne-repository-snapshots", "default", "https://repo.slne.dev:2053/snapshots").build());


        classpathBuilder.addLibrary(resolver);
    }
}
