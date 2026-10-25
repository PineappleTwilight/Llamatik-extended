package com.llamatik.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.llamatik.app.localization.getCurrentLocalization
import com.llamatik.app.platform.SimpleScaleNavHost
import com.llamatik.app.platform.Tab
import com.llamatik.app.platform.TabOptions
import com.llamatik.app.platform.rememberNavigator
import com.llamatik.app.ui.icon.LlamatikIcons
import com.llamatik.app.ui.screens.HomeTabScreen

internal object HomeTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(LlamatikIcons.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = getCurrentLocalization().home,
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        val navigator = rememberNavigator(HomeTabScreen())
        SimpleScaleNavHost(navigator)
    }
}
