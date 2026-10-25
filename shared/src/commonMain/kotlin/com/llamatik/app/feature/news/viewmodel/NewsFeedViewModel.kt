package com.llamatik.app.feature.news.viewmodel

import com.llamatik.app.platform.ScreenModel
import com.llamatik.app.platform.Screen
import com.llamatik.app.platform.SimpleNavigator

import co.touchlab.kermit.Logger
import com.llamatik.app.feature.news.NewsFeedDetailScreen
import com.llamatik.app.feature.news.repositories.FeedItem
import com.llamatik.app.feature.news.usecases.GetAllNewsUseCase
import com.llamatik.app.platform.RootNavigatorRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NewsFeedViewModel(
    private var navigator: SimpleNavigator,
    private val rootNavigatorRepository: RootNavigatorRepository,
    private val getAllNewsUseCase: GetAllNewsUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(NewsScreenState())
    val state = _state.asStateFlow()
    private val _sideEffects = Channel<NewsSideEffects>()
    val sideEffects: Flow<NewsSideEffects> = _sideEffects.receiveAsFlow()

    fun onStarted(navigator: SimpleNavigator) {
        this.navigator = navigator
        screenModelScope.launch {
            getAllNewsUseCase.invoke()
                .onSuccess {
                    _state.value = _state.value.copy(news = it)
                }
                .onFailure { error ->
                    error.message?.let { message ->
                        Logger.e(message)
                    }
                }
        }
    }

    fun onOpenFeedItemDetail(link: String) {
        rootNavigatorRepository.navigator.push(NewsFeedDetailScreen(link))
    }
}

data class NewsScreenState(
    val news: List<FeedItem> = emptyList()
)

sealed class NewsSideEffects {
    data object Initial : NewsSideEffects()
    data object OnSignedUp : NewsSideEffects()
    data object OnSignUpError : NewsSideEffects()
}
