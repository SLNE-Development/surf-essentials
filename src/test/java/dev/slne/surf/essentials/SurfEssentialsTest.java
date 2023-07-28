package dev.slne.surf.essentials;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SurfEssentialsTest {

    private ServerMock serverMock;
    private SurfEssentials surfEssentials;

    @BeforeEach
    void setUp() {
        serverMock = MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void onLoad() {
      // surfEssentials.onLoad();
    }

    @Test
    void onEnable() {
    }

    @Test
    void onDisable() {
    }

    @Test
    void getInstance() {
    }

    @Test
    void loadMessage() {
    }

    @Test
    void logger() {
    }

    @Test
    void getCoreProtectApi() {
    }
}