package io.getunleash.plugin.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@State(name = "UnleashSettings", storages = [Storage("unleashSettings.xml")])
class UnleashSettings : PersistentStateComponent<UnleashSettings.State> {
    data class State(var apiUrl: String = "", var apiToken: String = "")

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        val instance: UnleashSettings
            get() = service<UnleashSettings>()
    }
}