package dev.slne.surf.essentials.utils.arguments;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LookAnchorArgumentTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void validateAnchor() {
        for (LookAnchorArgument.Anchor value : LookAnchorArgument.Anchor.values()) {
            assertEquals(value.getId(), LookAnchorArgument.Anchor.getByName(value.getId()).get().getId());
        }
    }


}