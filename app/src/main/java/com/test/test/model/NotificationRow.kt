package com.test.test.model

import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import com.test.test.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.notification_row.view.*
import r.evgenymotorin.recipes.db.tables.NotificationData

class NotificationRow(val notification: NotificationData): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.notification_row
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.user_token_notification_row.text = notification.userKey
        viewHolder.itemView.message_notification_row.text = notification.message
        viewHolder.itemView.time_notification_row.text = SimpleDateFormat("HH:mm:ss").format(notification.timestamp)
    }
}