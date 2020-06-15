package com.emika.app.presentation.adapter.calendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.network.pojo.subTask.Comment

class Comments : AppCompatActivity() {
    private var comments: List<Comment>? = null
    private var recycler: RecyclerView? = null
    private var adapter: CommentAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        initView()
    }

    private fun initView() {
//        comments = intent.getParcelableExtra("comments")
        adapter = CommentAdapter(comments!!)
        recycler = findViewById<RecyclerView>(R.id.comments_recycler).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = adapter
        }
    }
}
