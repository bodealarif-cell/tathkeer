package com.example.tathkeer.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.tathkeer.MainActivity
import com.example.tathkeer.R
import com.example.tathkeer.data.AdhkarProvider
import com.example.tathkeer.data.Preferences
import kotlinx.coroutines.runBlocking

class TathkeerWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MyContent(context)
        }
    }

    @Composable
    private fun MyContent(context: Context) {
        // قراءة الحالة المحفوظة
        val prefs = Preferences(context)
        val section = runBlocking { prefs.sectionFlow.first() } // بدلاً من replayLatest
        val index = runBlocking { prefs.indexFlow.first() }
        val morningDone = runBlocking { prefs.morningDoneFlow().first() }
        val eveningDone = runBlocking { prefs.eveningDoneFlow().first() }

        val list = if (section == "morning") AdhkarProvider.morningAdhkar else AdhkarProvider.eveningAdhkar
        val doneList = if (section == "morning") morningDone else eveningDone

        val currentZikr = if (index in list.indices) {
            val z = list[index]
            val done = if (index in doneList.indices) doneList[index] else 0
            z.copy(done = done)
        } else null

        val bgColor = ColorProvider(R.color.green_light)
        val textColor = ColorProvider(R.color.green_dark)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(bgColor)
                .padding(16.dp)
                .clickable( onClickAction = createIncrementAction(context) ),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentZikr != null) {
                Text(
                    text = currentZikr.title,
                    style = TextStyle(
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(bottom = 4.dp)
                )
                Text(
                    text = currentZikr.text.take(30) + "...",  // نختصر النص
                    style = TextStyle(
                        color = textColor,
                        fontSize = 12
                    ),
                    modifier = GlanceModifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${currentZikr.done} / ${currentZikr.count}",
                        style = TextStyle(
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            } else {
                Text("لا يوجد ذكر")
            }
        }
    }

    private fun createIncrementAction(context: Context): Action {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = "INCREMENT_FROM_WIDGET"
            data = Uri.parse("tathkeer://widget/increment")
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return actionStartActivity(pendingIntent)
    }
}

class TathkeerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TathkeerWidget()
}
