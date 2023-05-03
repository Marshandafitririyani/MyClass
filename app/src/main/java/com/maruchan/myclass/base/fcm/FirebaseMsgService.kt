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

        //TODO: untuk mengecek
        Log.d("fcmServis", "messageData:${message.data}")
        Log.d("fcmServis", "message:${message.notification}")
        Timber.d("firebase_receive_message_title : ${message.data["user_id"]}")
        Timber.d("firebase_receive_message_title : ${message.data["title"]}")
        Timber.d("firebase_receive_message_message : ${message.data["body"]}")


        showNotification(
            context,
            message.data["title"] ?: return,
            message.data["body"] ?: return

            //TODO:title mengambil titlenya, body itu messagenya
        )


    }

}

private fun sendRegistrationToServer(token: String?) {
    // TODO: Implement this method to send token to your app server.
    //TODO: untuk mengirim device tokennya, tapi home sudah ngambil dan mengirim...ini hanya untuk mengeceknya
    Log.d(TAG, "sendRegistrationTokenToServer($token)")
}

//TODO: untuk edit notifikasinya, notifasi manager sudah ada di android
fun showNotification(context: Context, title: String, message: String) {
    //todo:Notification Manager
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    //TODO: Notification for Oreo
    // untuk android 13 meminta notifikasi untuk notifikasi wajib untuk android 13 keatas
    // untuk menyeting notifikasinya
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "CHANNEL_ID", //TODO:untuk android 13
            "My Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Mhm"
        channel.enableLights(true)
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }

    //TODO: untuk berpindah ke activity detail saat membuka notifikasinya
    val resultIntent = Intent(context, DetailFriendsActivity::class.java)

    //TODO: untuk berpindah ke activity detail saat membuka notifikasinya
    var resultPendingIntent: PendingIntent? =
        PendingIntent.getActivity(context, 1, resultIntent, PendingIntent.FLAG_IMMUTABLE)

    //TODO: untuk edit titile, masage, logo
    // TODO:Builder
    val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
        .setSmallIcon(R.drawable.img_logo)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(resultPendingIntent)
        .setAutoCancel(true)

    // TODO:Show Notification
    notificationManager.notify(1, builder.build())
}

