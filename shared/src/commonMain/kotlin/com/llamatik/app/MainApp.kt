package com.llamatik.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.llamatik.app.localization.AvailableLanguages
import com.llamatik.app.localization.getCurrentLanguage
import com.llamatik.app.platform.SimpleScaleNavHost
import com.llamatik.app.platform.rememberNavigator
import com.llamatik.app.ui.screens.MainScreen

@Composable
fun MainApp() {

    val isRtl = getCurrentLanguage() == AvailableLanguages.FA

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
    ) {
        val navigator = rememberNavigator(MainScreen())
        SimpleScaleNavHost(navigator)
    }
}
