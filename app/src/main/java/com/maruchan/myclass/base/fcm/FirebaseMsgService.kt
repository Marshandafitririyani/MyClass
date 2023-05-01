package com.maruchan.myclass.base.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.maruchan.myclass.R
import com.maruchan.myclass.data.constant.Const
import com.maruchan.myclass.ui.detail.DetailFriendsActivity
import timber.log.Timber

class FirebaseMsgService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("firebase-token", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val context: Context = applicationContext

        showNotification(
            context,
            message.data["title"] ?: return,
            message.data["body"] ?: return,
            message.data["user_id"] ?: return,
        )


    }

}

private fun sendRegistrationToServer(token: String?) {
    Log.d(TAG, "sendRegistrationTokenToServer($token)")
}


fun showNotification(context: Context, title: String, message: String, userId: String) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "CHANNEL_ID",
            "My Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Mhm"
        channel.enableLights(true)
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }

    val resultIntent = Intent(context, DetailFriendsActivity::class.java).apply {
        putExtra(Const.ID, userId)
    }

    var resultPendingIntent: PendingIntent? =
        PendingIntent.getActivity(context, 1, resultIntent, PendingIntent.FLAG_IMMUTABLE)

    val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
        .setSmallIcon(R.drawable.img_logo)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(resultPendingIntent)
        .setAutoCancel(true)

    notificationManager.notify(1, builder.build())
}

