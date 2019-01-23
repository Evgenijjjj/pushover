package com.test.test

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.test.test.model.MessageRequest
import com.test.test.retrofit.NotificationApi
import com.test.test.retrofit.NotificationClient
import org.json.JSONObject
import r.evgenymotorin.recipes.database.NotificationsDataBase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class AlarmBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        //Получение сообщения из БД
        val notificationId = intent?.getIntExtra(context?. getString(R.string.notificationId), -1)

        if (notificationId == null || notificationId == -1) return

        val dbDao = NotificationsDataBase.getInstance(context!!)
            ?.RecipeDataDao()

        val notificationData = dbDao?.getNotificationDataWithId(notificationId) ?: return

        val messageRequest = MessageRequest()
        messageRequest.deviceName = notificationData.deviceName
        messageRequest.userKey = notificationData.userKey
        messageRequest.appToken = notificationData.appToken
        messageRequest.message = notificationData.message

        //Отправка сообщения
        NotificationClient.instance
            .create(NotificationApi::class.java)
            .sendMessage(messageRequest)
            .enqueue(object : Callback<JSONObject> {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                sendNotification("An error occurred: \"$t\" while sending message: \"${notificationData.message}\".", context)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<JSONObject>, response: Response<JSONObject>) {
                if (response.isSuccessful) {
                    sendNotification("message: \"${notificationData.message}\" sent successfully", context)
                }
                else {
                    val jsonObject = JSONObject(response.errorBody()!!.string().toString())
                    sendNotification("An error occurred: \"${jsonObject.get("errors")}\" while sending message: \"${notificationData.message}\".", context)
                    dbDao.deleteNotificationDataWithId(notificationId)
                }
            }
        })

    }

    /**
     * Уведомление пользователя о статусе отправки сообщения
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(content: String, ctx: Context) {
        val builder = Notification.Builder(ctx)
        builder.setContentTitle(ctx.getString(R.string.notificationStatus))
        builder.setContentText(content)
        builder.setChannelId(ctx.getString(R.string.channelId))
        builder.setSmallIcon(android.R.drawable.ic_menu_share)

        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(NotificationChannel(ctx.getString(R.string.channelId), ctx.getString(R.string.notificationStatus), NotificationManager.IMPORTANCE_HIGH))
        nm.notify(0, builder.build())
    }
}