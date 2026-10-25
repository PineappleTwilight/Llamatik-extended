package com.llamatik.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.jvm.Volatile

/**
 * The screen contract of the demo app. Replaces the retired Voyager `Screen`, which
 * Houri fork dropped in favor of Jetpack Navigation 3 (see the main app's `mihon.core.navigation`).
 * This sample app is not part of Houri's build graph — it keeps this minimal, dependency-free
 * equivalent so the demo still runs unchanged on all KMP targets.
 */
interface Screen {
    @Composable
    fun Content()
}

/**
 * Replaces the retired Voyager `ScreenModel`: a scope holder whose scope lives as long as
 * the model is retained by its screen.
 */
interface ScreenModel {
    val screenModelScope: CoroutineScope
}

/**
 * A minimal back stack of [Screen]s, exposing the tiny slice of Voyager's `Navigator` API the
 * demo uses (push / pop / replace / peek). State is a [SnapshotStateList], so rendering it
 * recomposes exactly like the old navigator did.
 */
class SimpleNavigator(initial: Screen) {

    val items: SnapshotStateList<Screen> = mutableStateListOf(initial)

    val lastItem: Screen get() = items.last()

    val size: Int get() = items.size

    val canPop: Boolean get() = items.size > 1

    fun push(screen: Screen) {
        items += screen
    }

    fun pop(): Boolean {
        if (!canPop) return false
        items.removeAt(items.lastIndex)
        return true
    }

    fun replace(screen: Screen) {
        items[items.lastIndex] = screen
    }

    fun popUntilRoot() {
        while (canPop) pop()
    }
}

/** Remembers a [SimpleNavigator] rooted at [initial], surviving recomposition. */
@Composable
fun rememberNavigator(initial: Screen): SimpleNavigator {
    return androidx.compose.runtime.remember(initial) { SimpleNavigator(initial) }
}

/** The navigator the demo's screens read, replacing the retired Voyager `LocalNavigator`. */
val LocalSimpleNavigator = androidx.compose.runtime.staticCompositionLocalOf<SimpleNavigator> {
    error("LocalSimpleNavigator not provided")
}

/** The tab navigator the demo's screens read, replacing the retired Voyager `LocalTabNavigator`. */
val LocalSimpleTabNavigator = androidx.compose.runtime.staticCompositionLocalOf<SimpleTabNavigator> {
    error("LocalSimpleTabNavigator not provided")
}

/**
 * Replaces the retired Voyager `Tab`: a top-level destination with title/icon options and
 * composable content.
 */
interface Tab {
    val options: TabOptions

    @Composable
    fun Content()
}

data class TabOptions(
    val index: UInt,
    val title: String,
    val icon: androidx.compose.ui.graphics.painter.Painter? = null,
)

/** Replaces the retired Voyager `TabNavigator`: current-tab state plus per-tab [SimpleNavigator]s. */
class SimpleTabNavigator(initial: Tab) {
    var current: Tab by androidx.compose.runtime.mutableStateOf(initial)
        private set

    private val tabNavigators = mutableMapOf<Tab, SimpleNavigator>()

    fun navigatorFor(tab: Tab): SimpleNavigator =
        tabNavigators.getOrPut(tab) { SimpleNavigator(tab) }

    fun select(tab: Tab) {
        current = tab
    }
}

/** Remembers a [SimpleTabNavigator] rooted at [initial]. */
@Composable
fun rememberTabNavigator(initial: Tab): SimpleTabNavigator {
    return androidx.compose.runtime.remember(initial) { SimpleTabNavigator(initial) }
}

/**
 * Renders the top of a [SimpleNavigator] with a scale transition, replacing Voyager's
 * Voyager scale transition.
 */
@Composable
fun SimpleScaleNavHost(navigator: SimpleNavigator) {
    androidx.compose.animation.AnimatedContent(
        targetState = navigator.lastItem,
        transitionSpec = {
            (
                androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(220)) +
                    androidx.compose.animation.scaleIn(
                        initialScale = 0.92f,
                        animationSpec = androidx.compose.animation.core.tween(220),
                    )
                ).togetherWith(
                androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(90)) +
                    androidx.compose.animation.scaleOut(
                        targetScale = 1.05f,
                        animationSpec = androidx.compose.animation.core.tween(90),
                    ),
            )
        },
        label = "simple-nav",
    ) { screen ->
        screen.Content()
    }
}

/**
 * Renders the top of a [SimpleNavigator] with a slide transition, replacing Voyager's
 * Voyager slide transition.
 */
@Composable
fun SimpleSlideNavHost(navigator: SimpleNavigator) {
    androidx.compose.animation.AnimatedContent(
        targetState = navigator.lastItem,
        transitionSpec = {
            androidx.compose.animation.slideInHorizontally(
                animationSpec = androidx.compose.animation.core.tween(220),
                initialOffsetX = { it / 4 },
            ).togetherWith(
                androidx.compose.animation.slideOutHorizontally(
                    animationSpec = androidx.compose.animation.core.tween(90),
                    targetOffsetX = { -it / 4 },
                ),
            )
        },
        label = "simple-nav-slide",
    ) { screen ->
        screen.Content()
    }
}

/**
 * Replaces the retired Voyager-Koin `koinScreenModel`: resolves a Koin factory-scoped model for the
 * current composition.
 */
@Composable
inline fun <reified T> koinScreenModel(
    scope: org.koin.core.scope.Scope = org.koin.mp.KoinPlatform.getKoin(),
    noinline parameters: org.koin.core.parameter.ParametersDefinition = { org.koin.core.parameter.EMPTY_PARAMETERS },
): T = androidx.compose.runtime.remember(parameters) { scope.get<T>(parameters = parameters) }

/**
 * Screen-model base with a scope cancelled on [onDispose]; mirrors the old
 * `ScreenModel` scope lifecycle for models created directly by screens.
 */
abstract class BaseScreenModel : ScreenModel {
    override val screenModelScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Volatile
    private var disposed = false

    fun onDispose() {
        if (!disposed) {
            disposed = true
            screenModelScope.cancel()
        }
    }
}
