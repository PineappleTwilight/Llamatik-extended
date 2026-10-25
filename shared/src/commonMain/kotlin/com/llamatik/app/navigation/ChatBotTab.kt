package com.llamatik.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.llamatik.app.feature.chatbot.ChatBotTabScreen
import com.llamatik.app.platform.SimpleScaleNavHost
import com.llamatik.app.platform.Tab
import com.llamatik.app.platform.TabOptions
import com.llamatik.app.platform.rememberNavigator
import com.llamatik.app.ui.icon.LlamatikIcons

internal object ChatBotTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(LlamatikIcons.ChatBot)

            return remember {
                TabOptions(
                    index = 2u,
                    title = "Llamatik AI",
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        val navigator = rememberNavigator(ChatBotTabScreen())
        SimpleScaleNavHost(navigator)
    }
}
