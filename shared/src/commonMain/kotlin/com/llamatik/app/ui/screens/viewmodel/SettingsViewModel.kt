package com.llamatik.app.ui.screens.viewmodel

import com.llamatik.app.platform.ScreenModel
import com.llamatik.app.platform.SimpleNavigator

import com.llamatik.app.platform.RootNavigatorRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private var navigator: SimpleNavigator,
    private val rootNavigatorRepository: RootNavigatorRepository
) : ScreenModel {
    private val _state = MutableStateFlow(SettingsState("", "", ""))
    val state = _state.asStateFlow()
    private val _sideEffects = Channel<SettingsSideEffects>()
    val sideEffects: Flow<SettingsSideEffects> = _sideEffects.receiveAsFlow()

    fun onStarted(navigator: SimpleNavigator) {
        this.navigator = navigator
        screenModelScope.launch {
            _state.value = SettingsState(
                name = _state.value.name,
                description = _state.value.description,
                image = _state.value.image,
            )
        }
    }
}

data class SettingsState(
    val name: String,
    val description: String,
    val image: String,
)

sealed class SettingsSideEffects {
    data object Initial : SettingsSideEffects()
    data object OnSignedUp : SettingsSideEffects()
    data object OnSignUpError : SettingsSideEffects()
}
