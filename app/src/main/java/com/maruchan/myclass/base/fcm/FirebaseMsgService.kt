package com.maruchan.myclass.base.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.crocodic.core.helper.DateTimeHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.maruchan.myclass.R
import com.maruchan.myclass.data.constant.Const
import com.maruchan.myclass.ui.detail.DetailFriendsActivity
import com.maruchan.myclass.ui.home.HomeActivity
import timber.log.Timber

class FirebaseMsgService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("firebase-token", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val context: Context = applicationContext

        Log.d("fcmServis", "messageData:${message.data["title"]}")
        Log.d("fcmServis", "message:${message.notification}")

        /*if (message.notification!=null){
            showNotification(
                context,
                message.notification!!.title!!,
                message.notification!!.body!!,



            )
        }

    }

}*/

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


fun showNotification(context: Context, title: String, message: String, user_id: String) {
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

    val homeIntent = Intent(context, HomeActivity::class.java)


    val detailIntent = Intent(context, DetailFriendsActivity::class.java).apply {
        Log.d("cek id", "cek id : $user_id")
        putExtra(Const.ID, user_id.toInt())
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }


    var resultPendingIntent: PendingIntent? =
        PendingIntent.getActivity(
            context, 1, detailIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

    val stackBuilder = TaskStackBuilder.create(context)
    stackBuilder.addNextIntent(homeIntent)
    stackBuilder.addNextIntent(detailIntent)
    resultPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_CANCEL_CURRENT)


    val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
        .setSmallIcon(R.drawable.img_logo)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(resultPendingIntent)
        .setAutoCancel(true)

    val idNotification = DateTimeHelper().createAtLong().toInt()
    notificationManager.notify(idNotification, builder.build())
}

