package com.maruchan.myclass.base.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.maruchan.myclass.R
import com.maruchan.myclass.data.session.Session
import timber.log.Timber
import javax.inject.Inject

class FirebaseMsgService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("firebase-token", token)
        /*session.setValue(Session.TOKEN_FCM, token)

        session.getString(Session.TOKEN_FCM)*/
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val context: Context = applicationContext

        Log.d("fcmServis", "messageData:${message.data}")
        Log.d("fcmServis", "message:${message.notification}")
        Timber.d("firebase_receive_message_title : ${message.data["user_id"]}")
        Timber.d("firebase_receive_message_title : ${message.data["title"]}")
        Timber.d("firebase_receive_message_message : ${message.data["message"]}")

        if (message.notification!=null){
            showNotification(
                context,
                message.notification!!.title!!,
                message.notification!!.body!!
            )
        }

    }

}

private fun sendRegistrationToServer(token: String?) {
    // TODO: Implement this method to send token to your app server.
    Log.d(TAG, "sendRegistrationTokenToServer($token)")
}

fun showNotification(context: Context,title: String, message: String) {
    //todo:Notification Manager
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    //todo: Notification for Oreo >
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

    // todo:Builder
    val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
        .setSmallIcon(R.drawable.img_logo)
//        .setContentInfo(user_id)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    // todo:Show Notification
    notificationManager.notify(1, builder.build())
}

/*FirebaseMessagingService() {
    override fun onNewToken(token : String) {
        super.onNewToken(token)
        Log.d("firebasetoken", token)
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.d("firebase_receive_message_title : ${message.data["title"]}")
        Timber.d("firebase_receive_message_message : ${message.data["message"]}")
    }

}

private fun sendRegistrationToServer(token: String?) {
    // TODO: Implement this method to send token to your app server.    Log.d(TAG, "sendRegistrationTokenToServer($token)")
}

*/

/*token fcm (device token)
dc0R5C3WQCCdtQPoGmNh2O:APA91bGGsR-iUCSTgbgjq_GQmgcC2W0lKpyQ0GdlAfH1z6fHZvw-i8j4WfzX3lyYhfKfrZIqrudYQGrzy1hk6gLi1TGLlnkQ2swxHVteeZxq2P0DnmbqnbRCfSu3PV_FK3a3aYY-1lGH*/

/*
sever Q
AAAAylmGvBQ:APA91bGw39t-VqLdjYcypirGqdvNnzQD_XRlNdSSVr6zEI8CGQPwB-vNN_yUE8dp3g81dv7zVMKMY3pGtz5Hp8J_Vb1Yg89RPMWIuCLjN-OG0vZaexQHSi162v72eg2Yg-yV-Suu8848
*/
