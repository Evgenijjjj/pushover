package com.test.test

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.test.test.model.NotificationRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_start_page.*
import kotlinx.android.synthetic.main.myfab.*
import r.evgenymotorin.recipes.database.NotificationsDataBase

class StartPageActivity : AppCompatActivity() {

    private lateinit var db: NotificationsDataBase
    private lateinit var adapter: GroupAdapter<ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_page)
        title = getString(R.string.latest_messages)

        db = NotificationsDataBase.getInstance(this)!!
        adapter = GroupAdapter()
        recyclerview.adapter = adapter

        myfab_main_btn.setOnClickListener {
            it.isClickable = false
            startActivity(Intent(this, NewMessageActivity::class.java))
            it.isClickable = true
        }
    }

    override fun onResume() {
        super.onResume()

        adapter.clear()
        fillRecyclerView()
    }

    private fun fillRecyclerView() {
        for (e in db.RecipeDataDao().getAll())
            adapter.add(NotificationRow(e))
    }
}