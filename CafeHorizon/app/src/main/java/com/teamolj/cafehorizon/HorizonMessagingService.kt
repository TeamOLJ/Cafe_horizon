package com.teamolj.cafehorizon

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class HorizonMessagingService : FirebaseMessagingService() {

    private var auth = Firebase.auth
    private var db = Firebase.firestore

    // Called if the FCM registration token is updated: 이미 초기생성이 된 상태일 때 작동
    override fun onNewToken(token: String) {
        if (auth.currentUser != null)
            db.collection("UserInformation").document(auth.currentUser!!.uid).update("deviceToken", token)
    }

    // 메시지가 Data 형식으로만 수신된다고 가정하여 구현한 코드 (Notification payload X)
    // 사용자의 로그인 상태를 먼저 확인하고 알림을 띄우기 위함
    // 메시지 수신 동의 여부는 클라이언트에서 확인하지 않음 -> 해당 부분은 서버에서 확인하고 발송 처리
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (auth.currentUser != null) {
            remoteMessage.data.let {
                sendNotification(it["Title"], it["Body"])
            }
        }
    }

    // 작업표시줄에 알림 띄우는 함수
    private fun sendNotification(messageTitle: String?, messageBody: String?) {
        val intent = Intent(this, OrderedListActivity::class.java)   // 알림 클릭 시 주문내역 창으로 이동
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ch_notification_icon)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0(API 수준 26)부터는 모든 알림을 채널에 할당해야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "푸시 알림", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}