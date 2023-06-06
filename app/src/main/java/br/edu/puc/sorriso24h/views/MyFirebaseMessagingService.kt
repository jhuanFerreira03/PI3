package br.edu.puc.sorriso24h.views

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {
    private val channelId = "notification_channel"
    private val channelName = "br.edu.puc.sorriso24h"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val dados = remoteMessage.data

        if (dados == null) return

        SecurityPreferences(this).storeString(Constants.KEY_SHARED.ARRAY_NAME, dados["nome"].toString())
        SecurityPreferences(this).storeString(Constants.KEY_SHARED.ARRAY_TEL, dados["telefone"].toString())

        generateNotification(dados["title"].toString(), dados["nome"].toString() + " " + dados["telefone"].toString())
    }
    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews(channelName, R.layout.notification)
        remoteView.setTextViewText(R.id.text_noti, title)
        remoteView.setTextViewText(R.id.text_noti_text, message)

        return remoteView
    }
    private fun generateNotification(title: String, message: String){
        val intent = Intent(this, EmergencyDetailActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_arrow_back)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())
    }
}