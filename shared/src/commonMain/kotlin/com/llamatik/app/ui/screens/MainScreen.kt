package com.llamatik.app.ui.screens

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.llamatik.app.navigation.ChatBotTab
import com.llamatik.app.platform.LocalSimpleNavigator
import com.llamatik.app.platform.LocalSimpleTabNavigator
import com.llamatik.app.platform.RootNavigatorRepository
import com.llamatik.app.platform.RootSnackbarHostStateRepository
import com.llamatik.app.platform.SimpleScaleNavHost
import com.llamatik.app.platform.SimpleTabNavigator
import com.llamatik.app.platform.Tab
import com.llamatik.app.platform.rememberNavigator
import com.llamatik.app.platform.rememberTabNavigator
import com.llamatik.app.ui.components.LlamatikNavigationBarItem
import com.llamatik.app.ui.theme.LlamatikTheme
import com.llamatik.app.ui.theme.Typography
import org.koin.core.parameter.ParametersHolder
import org.koin.mp.KoinPlatform

class MainScreen : com.llamatik.app.platform.Screen {
    @Composable
    override fun Content() {
        LlamatikTheme {
            MainScreenView()
        }
    }
}

@Composable
fun MainScreenView() {
    val snackbarHostState = remember { SnackbarHostState() }
    val rootNavigator = rememberNavigator(MainScreen())
    val tabNavigator = rememberTabNavigator(ChatBotTab)
    val rootNavigatorRepository = setupRootNavigator(rootNavigator, tabNavigator)
    val rootSnackbarHostStateRepository = setupRootSnackbarHostState(snackbarHostState)

    androidx.compose.runtime.CompositionLocalProvider(
        LocalSimpleNavigator provides rootNavigator,
        LocalSimpleTabNavigator provides tabNavigator,
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(rootSnackbarHostStateRepository.snackbarHostState) },
        ) {
            SimpleScaleNavHost(tabNavigator.navigatorFor(tabNavigator.current))
        }
    }
}

fun setupRootNavigator(
    rootNavigator: com.llamatik.app.platform.SimpleNavigator,
    tabNavigator: SimpleTabNavigator,
): RootNavigatorRepository {
    val koin = KoinPlatform.getKoin()
    return koin.get(
        null,
        parameters = {
            ParametersHolder(
                listOf(rootNavigator, tabNavigator).toMutableList(),
                false,
            )
        },
    )
}

fun setupRootSnackbarHostState(snackbarHostState: SnackbarHostState): RootSnackbarHostStateRepository {
    val koin = KoinPlatform.getKoin()
    return koin.get(
        null,
        parameters = { ParametersHolder(listOf(snackbarHostState).toMutableList(), false) },
    )
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab, rootNavigator: RootNavigatorRepository) {
    val tabNavigator = LocalSimpleTabNavigator.current
    val currentDestination = tabNavigator.current == tab

    LlamatikNavigationBarItem(
        selected = currentDestination,
        onClick = {
            tabNavigator.select(tab)
        },
        icon = {
            tab.options.icon?.let {
                Icon(
                    painter = it,
                    contentDescription = tab.options.title,
                )
            }
        },
        label = {
            Text(
                text = tab.options.title,
                style = Typography.get().labelSmall,
            )
        },
    )
}
