package io.getunleash.plugin.settings

import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPasswordField
import javax.swing.JTextField

/**
 * Panel for managing Unleash settings in the IDE.
 *
 * Provides a user interface for configuring the Unleash API URL and API token
 * and is used by the [UnleashConfigurable] class.
 */
class UnleashSettingsPanel {
    private val apiUrlTextField: JTextField = JTextField().apply { columns = 20 }
    private val apiTokenTextField: JPasswordField = JPasswordField().apply { columns = 20 }

    var apiUrl: String
        get() = apiUrlTextField.text.trim()
        set(value) {
            apiUrlTextField.text = value
        }

    var apiToken: String
        get() = String(apiTokenTextField.password).trim()
        set(value) {
            apiTokenTextField.text = value
        }

    fun getPanel(): JPanel {
        val contentPanel = JPanel(GridBagLayout())

        with(GridBagConstraints()) {
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.WEST
            gridx = 0; gridy = 0
            weightx = 1.0
            insets = JBUI.insets(4)

            contentPanel.add(JLabel("Instance API URL"), this)

            gridy = 1; contentPanel.add(apiUrlTextField, this)
            gridy = 2; contentPanel.add(JLabel("API token:"), this)
            gridy = 3; contentPanel.add(apiTokenTextField, this)
        }

        return JPanel(BorderLayout()).apply {
            add(contentPanel, BorderLayout.NORTH)
        }
    }
}