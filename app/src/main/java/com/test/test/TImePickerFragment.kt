package com.test.test

import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.app.TimePickerDialog
import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi


class TImePickerFragment: DialogFragment() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(activity, activity as TimePickerDialog.OnTimeSetListener?
            , hour, minute, DateFormat.is24HourFormat(activity))
    }
}