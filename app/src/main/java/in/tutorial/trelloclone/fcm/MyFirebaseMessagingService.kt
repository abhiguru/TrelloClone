package `in`.tutorial.trelloclone.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.activities.MainActivity
import `in`.tutorial.trelloclone.activities.SignInActivity
import `in`.tutorial.trelloclone.firebase.FirestoreClass
import `in`.tutorial.trelloclone.utils.Constants

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG,"Message data ${remoteMessage.data}")
            val title = remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message = remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!
            sendNotification(title, message )
        }
        remoteMessage.notification?.let {
            Log.d(TAG,"Message Notification Body: ${it.body}")
        }
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG,"Refresed token $token")
        sendRegistrationToServer(token)
    }
    private fun sendRegistrationToServer(token: String){
    }
    private fun sendNotification(title:String, messageBody:String){
        val intent = if(FirestoreClass().getCurrentUserId().isNotEmpty()){
            Intent(this, MainActivity::class.java)
        }else{
            Intent(this, SignInActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_MUTABLE
            )
        }else{
            PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT
            )
        }
        val channelId = this.resources.getString(R.string.default_notification)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this, channelId
        ).setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManger = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,
            "Channel Projemang title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManger.createNotificationChannel(channel)
        }
        notificationManger.notify(0 /* ID of notification */, notificationBuilder.build())
    }
    companion object{
        private const val TAG = "MyFirebaseMsgService"
    }
}