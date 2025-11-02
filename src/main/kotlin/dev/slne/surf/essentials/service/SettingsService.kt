package dev.slne.surf.essentials.service

import dev.slne.surf.settings.api.common.dsl.settings
import dev.slne.surf.settings.api.common.surfSettingApi
import dev.slne.surf.surfapi.core.api.util.logger
import java.util.*

class SettingsService {
    suspend fun register() {
        logger().atInfo().log("Hooking into surf-settings...")
        settings {
            setting {
                identifier = SPECIAL_ITEM_SOUNDS
                displayName = "Special Item Geräusche"
                description =
                    "Aktiviert oder deaktiviert Geräusche, wenn ein Spieler einen besonderen Gegenstand erhält."
                category = CATEGORY_ID
                defaultValue = "true"
            }
        }
        logger().atInfo().log("Successfully hooked into surf-settings.")
    }

    suspend fun hasSoundsEnabled(uuid: UUID): Boolean {
        val setting = surfSettingApi.getSetting(SPECIAL_ITEM_SOUNDS) ?: return false
        val value = surfSettingApi.getEntry(uuid, setting)

        return value?.value?.toBooleanStrictOrNull() ?: false
    }


    companion object {
        val INSTANCE = SettingsService()
        const val CATEGORY_ID = "essentials"
        const val SPECIAL_ITEM_SOUNDS = "essentials-special_item_sounds"
    }
}

val settingsService get() = SettingsService.INSTANCE