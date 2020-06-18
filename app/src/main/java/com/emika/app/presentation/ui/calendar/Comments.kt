package com.emika.app.presentation.ui.calendar

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.network.pojo.comment.ModelComment
import com.emika.app.data.network.pojo.subTask.Comment
import com.emika.app.presentation.adapter.calendar.CommentAdapter
import com.emika.app.presentation.utils.viewModelFactory.calendar.TokenViewModelFactory
import com.emika.app.presentation.viewmodel.calendar.CommentViewModel
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.list_item.*

class Comments : AppCompatActivity() {
    private var comments: List<Comment>? = null
    private var recycler: RecyclerView? = null
    private var adapter: CommentAdapter? = null
    private lateinit var viewModel: TaskInfoViewModel
    private lateinit var viewModelComments: CommentViewModel
    private lateinit var createComment: ImageButton
    private lateinit var textComment: TextView
    private var taskId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        val toolbar = findViewById<Toolbar>(R.id.comments_toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        toolbar.overflowIcon!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val upArrow = resources.getDrawable(R.drawable.ic_arrow_back_white)
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        supportActionBar!!.title = "Comments"
        toolbar.setNavigationOnClickListener {
            finish()
        }
        initView()
    }

    private fun initView() {
        val token = EmikaApplication.instance.sharedPreferences?.getString("token", null)
        taskId = intent.getStringExtra("taskId")!!
        viewModel = ViewModelProviders.of(this, TokenViewModelFactory(token)).get(TaskInfoViewModel::class.java)
        viewModelComments = ViewModelProviders.of(this).get(CommentViewModel::class.java)
        viewModelComments.commentsMutableLiveData.observe(this, getComment)
        viewModel.getMembers()
        viewModel.getCommentsMutableLiveData(taskId).observe(this, getComments)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recycler = findViewById<RecyclerView>(R.id.comments_recycler).apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
        }
        createComment = findViewById<ImageButton>(R.id.comments_add_img)
        textComment = findViewById<TextView>(R.id.comments_text)
            createComment.setOnClickListener {
                if (comments_text.text.isNotEmpty()) {
                    viewModelComments.createComment(textComment.text.toString(), taskId!!)
                    textComment.text = ""
                }
            }
    }

    private val getComments = Observer<List<Comment>> { comments ->
        adapter = CommentAdapter(comments, this, viewModel.memberEntities, viewModelComments)
        recycler!!.adapter = adapter
    }

    private val getComment = Observer<ModelComment> { model ->
        val comment = model.payload
        adapter!!.addComment(comment)
        viewModelComments.insertComm(comment)
        recycler!!.scrollToPosition(adapter!!.itemCount)
    }

}
