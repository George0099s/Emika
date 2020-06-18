package com.emika.app.presentation.adapter.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emika.app.R
import com.emika.app.data.EmikaApplication
import com.emika.app.data.db.entity.MemberEntity
import com.emika.app.data.network.pojo.subTask.Comment
import com.emika.app.data.network.pojo.task.PayloadTask
import com.emika.app.di.User
import com.emika.app.presentation.utils.DateHelper
import com.emika.app.presentation.viewmodel.calendar.CommentViewModel
import java.util.*
import javax.inject.Inject

class CommentAdapter(private val commentList: List<Comment>, private val context: Context, private val members: List<MemberEntity>, private val viewModel: CommentViewModel?) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    @Inject
    lateinit var user: User
    lateinit var comments: MutableList<Comment>

    init {
        EmikaApplication.instance.component?.inject(this)
        comments = commentList as MutableList<Comment>

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImg = itemView.findViewById(R.id.comment_user_img) as ImageView
        val userName = itemView.findViewById(R.id.comment_user_name) as TextView
        val commText = itemView.findViewById(R.id.comment_text) as TextView
        val commDate = itemView.findViewById(R.id.comment_date) as TextView
        val options = itemView.findViewById(R.id.comment_options) as ImageButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comm = comments[position]
        holder.commText.text = comm.text
        holder.commDate.text = DateHelper.getDate(comm.createdAt)
        holder.options.setOnClickListener { showPopupMenu(it, comm, position) }
        if (comm.createdBy == user.id && viewModel != null)
            holder.options.visibility = View.VISIBLE
        else
            holder.options.visibility = View.GONE

        for (member in members)
            if (comm.createdBy == member.id) {
                holder.userName.text = "${member.firstName} ${member.lastName}"
                Glide.with(context).load(member.pictureUrl).apply(RequestOptions.circleCropTransform()).into(holder.userImg)
            }

    }

    fun addComment(comment: Comment) {
        comments.add(comments.size, comment)
        notifyDataSetChanged()
    }

    private fun showPopupMenu(v: View, comment: Comment, position: Int) {
        val popupMenu = PopupMenu(context, v)
        popupMenu.inflate(R.menu.comment_menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.comm_delete -> {
                    viewModel?.deleteComment(comment.taskId, comment)
                    comments.remove(comment)
                    notifyItemRemoved(position)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener true

            }
        }
        popupMenu.show()
    }
}