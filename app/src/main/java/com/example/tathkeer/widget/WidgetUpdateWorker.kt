package com.example.tathkeer.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WidgetUpdateWorker {
    fun updateWidget(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            TathkeerWidget().updateAll(context)
        }
    }
}
