package com.test.test

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_message.*
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import com.test.test.model.MessageRequest
import com.test.test.retrofit.NotificationApi
import com.test.test.retrofit.NotificationClient
import org.json.JSONObject
import r.evgenymotorin.recipes.database.NotificationsDataBase
import r.evgenymotorin.recipes.db.tables.NotificationData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue


class NewMessageActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {


    private lateinit var alarmManager: AlarmManager
    private lateinit var db: NotificationsDataBase
    private lateinit var toolbarMenu: Menu
    private lateinit var notificationApi: NotificationApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        title = getString(R.string.new_message)

        app_token_edittext.hint = "App Token: ${getString(R.string.apiToken)}"
        user_key_edittext.hint = "User Key: ${getString(R.string.userKey)}"

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        db = NotificationsDataBase.getInstance(this)!!
        notificationApi = NotificationClient.instance.create(NotificationApi::class.java)

        qr_code_button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    resources.getInteger(R.integer.camera_permission_request_code))
                return@setOnClickListener
            }

            startActivityForResult(Intent(this, QRCodeReaderActivity::class.java),
                resources.getInteger(R.integer.qr_code_activity_request))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == resources.getInteger(R.integer.camera_permission_request_code) && grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
            startActivityForResult(Intent(this, QRCodeReaderActivity::class.java),
                resources.getInteger(R.integer.qr_code_activity_request))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == resources.getInteger(R.integer.qr_code_activity_request)) {
            //получение текста с qr кода
            if (resultCode == Activity.RESULT_OK) {
                val contents = data!!.getStringExtra(getString(R.string.scanResult))
                message_edittext.setText(contents.toString())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_message_menu, menu)
        toolbarMenu = menu!!
        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }

            R.id.action_send -> {
                val notificationData = getNotificationBodyFromFields() ?: return false

                val messageRequest = MessageRequest()
                messageRequest.deviceName = notificationData.deviceName
                messageRequest.userKey = notificationData.userKey
                messageRequest.appToken = notificationData.appToken
                messageRequest.message = notificationData.message

                notificationApi.sendMessage(messageRequest).enqueue(object : Callback<JSONObject> {
                    override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                        Toast.makeText(this@NewMessageActivity, "$t", Toast.LENGTH_LONG).show()
                        setEnableStateToMenuItems(true)
                    }

                    override fun onResponse(call: Call<JSONObject>, response: Response<JSONObject>) {
                        if (response.isSuccessful) {
                            finish()
                        } else {
                            val jsonObject = JSONObject(response.errorBody()!!.string().toString())
                            Toast.makeText(this@NewMessageActivity, jsonObject.get("errors")?.toString(), Toast.LENGTH_LONG).show()
                            setEnableStateToMenuItems(true)
                        }
                    }
                })
            }

            R.id.action_delay -> TImePickerFragment().show(supportFragmentManager, null)

            else -> return false
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setEnableStateToMenuItems(isEnable: Boolean) {
        val delayItem = toolbarMenu.findItem(R.id.action_delay)
        val sendItem = toolbarMenu.findItem(R.id.action_send)

        delayItem.isEnabled = isEnable
        sendItem.isEnabled = isEnable
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getNotificationBodyFromFields(): NotificationData? {
        setEnableStateToMenuItems(false)

        var notificationData = NotificationData()

        if (message_edittext.text.isNullOrEmpty()) {
            Toast.makeText(this, "Message is Empty!", Toast.LENGTH_LONG).show()
            setEnableStateToMenuItems(true)
            return null
        } else {
            notificationData.message = message_edittext.text.toString()
        }

        if (app_token_edittext.text.isNullOrEmpty()) notificationData.appToken = getString(R.string.apiToken)
        else notificationData.appToken = app_token_edittext.text.toString()

        if (user_key_edittext.text.isNullOrEmpty()) notificationData.userKey = getString(R.string.userKey)
        else notificationData.userKey = user_key_edittext.text.toString()

        notificationData.deviceName = device_name_edittext.text.toString()

        notificationData.timestamp = System.currentTimeMillis() % TimeUnit.DAYS.toMillis(1)

        db.RecipeDataDao().insert(notificationData)
        notificationData = db.RecipeDataDao().getLastNotificationData()

        return notificationData
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val notificationData = getNotificationBodyFromFields() ?: return

        val c = Calendar.getInstance()
        val h = c.get(Calendar.HOUR_OF_DAY)
        val m = c.get(Calendar.MINUTE)
        val s = c.get(Calendar.SECOND)

        val pickedTime = TimeUnit.HOURS.toMillis(hourOfDay.toLong()) + TimeUnit.MINUTES.toMillis(minute.toLong())
        val currentTime = TimeUnit.HOURS.toMillis(h.toLong()) + TimeUnit.MINUTES.toMillis(m.toLong()) + TimeUnit.SECONDS.toMillis(s.toLong())

        var delay: Long

        delay = (pickedTime - currentTime).absoluteValue
        if (pickedTime < currentTime) delay = (60L * 60L * 24L * 1000L) - delay

        val i = Intent(this, AlarmBroadcastReceiver::class.java)
        i.putExtra(getString(R.string.notificationId), notificationData.id)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0)

        alarmManager.cancel(pendingIntent)
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent)
        finish()
    }
}