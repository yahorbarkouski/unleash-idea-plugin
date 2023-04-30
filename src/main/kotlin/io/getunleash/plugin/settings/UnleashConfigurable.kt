package io.getunleash.plugin.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

/**
 * A configurable component for managing Unleash settings in the IDE.
 *
 * Provides a user interface for configuring the Unleash API URL and API token
 * and handles applying and resetting the settings.
 */
class UnleashConfigurable : Configurable {

    private var settingsPanel: UnleashSettingsPanel? = null

    override fun createComponent(): JComponent {
        settingsPanel = UnleashSettingsPanel()
        return settingsPanel!!.getPanel()
    }

    override fun getDisplayName(): String = "Unleash"

    override fun apply() {
        val settings = UnleashSettings.instance

        settings.state.apiUrl = settingsPanel?.apiUrl ?: ""
        settings.state.apiToken = settingsPanel?.apiToken ?: ""
    }

    override fun isModified(): Boolean {
        val settings = UnleashSettings.instance

        return settingsPanel?.apiUrl != settings.state.apiUrl
                || settingsPanel?.apiToken != settings.state.apiToken
    }

    override fun reset() {
        val settings = UnleashSettings.instance

        settingsPanel?.apiUrl = settings.state.apiUrl
        settingsPanel?.apiToken = settings.state.apiToken
    }
}