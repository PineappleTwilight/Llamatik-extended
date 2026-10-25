package com.llamatik.app.feature.webview.viewmodel

import com.llamatik.app.platform.ScreenModel
import com.llamatik.app.platform.Screen
import com.llamatik.app.platform.SimpleNavigator

import com.llamatik.app.platform.RootNavigatorRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class WebViewModel(
    private val userManualUrl: String,
    private val navigator: SimpleNavigator,
    private val rootNavigatorRepository: RootNavigatorRepository,
) : ScreenModel {
    private val _state = MutableStateFlow(WebViewScreenState(userManualUrl))
    val state = _state.asStateFlow()
    private val _sideEffects = Channel<WebViewScreenSideEffects>()
    val sideEffects: Flow<WebViewScreenSideEffects> = _sideEffects.receiveAsFlow()

    fun onStarted() {
        screenModelScope.launch {
            /*
            getPdfUseCase.invoke(userManualUrl)
                .onSuccess {
                    _state.value =
                        _state.value.copy(pdfData = it.first, encodedBase64Data = it.second)
                    _sideEffects.trySend(PdfScreenSideEffects.OnLoaded)
                }
                .onFailure {
                    _sideEffects.trySend(PdfScreenSideEffects.OnLoadError)
                    Logger.w(it) { "Error on downloading user manual" }
                }
*/
        }
    }
}

data class WebViewScreenState(val url: String? = null)

sealed class WebViewScreenSideEffects {
    data object Initial : WebViewScreenSideEffects()
    data object OnLoaded : WebViewScreenSideEffects()
    data object OnLoadError : WebViewScreenSideEffects()
}
