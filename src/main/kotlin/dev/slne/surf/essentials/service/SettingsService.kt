package dev.slne.surf.essentials.service

import dev.slne.surf.settings.api.common.dsl.settings
import dev.slne.surf.settings.api.common.surfSettingApi
import java.util.*

class SettingsService {
    suspend fun register() {
        settings {
            setting {
                identifier = SPECIAL_ITEM_SOUNDS
                displayName = "Special Item Geräusche"
                description =
                    "Aktiviert oder deaktiviert Geräusche, wenn ein Spieler ein besonderen Gegenstand erhält."
                category = CATEGORY_ID
                defaultValue = "true"
            }
        }
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