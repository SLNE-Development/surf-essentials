package dev.slne.surf.essentials.service

import dev.slne.surf.settings.api.common.dsl.settings
import dev.slne.surf.settings.api.common.surfSettingApi
import java.util.*

class SettingsService {
    suspend fun register() {
        settings {
            category {
                identifier = CATEGORY_ID
                displayName = "Essentials"
                description = "Essentials Settings"
            }

            setting {
                identifier = ID_SOUNDS
                withCategory(CATEGORY_ID)
                displayName = "Geräusche"
                description = "Aktiviert oder deaktiviert Geräusche für verschiedene Aktionen."
                defaultValue = "true"
            }
        }
    }

    suspend fun hasSoundsEnabled(uuid: UUID): Boolean {
        val setting = surfSettingApi.getSetting(ID_SOUNDS) ?: return false
        val value = surfSettingApi.getEntry(uuid, setting)

        return value?.value?.toBooleanStrictOrNull() ?: false
    }


    companion object {
        val INSTANCE = SettingsService()
        const val CATEGORY_ID = "surf_essentials"
        const val ID_SOUNDS = "sounds"
    }
}

val settingsService get() = SettingsService.INSTANCE