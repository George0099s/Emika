package com.emika.app.presentation.adapter.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emika.app.R
import com.emika.app.data.network.pojo.subTask.Comment

class CommentAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImg = itemView.findViewById(R.id.comment_user_img) as ImageView
        val userName = itemView.findViewById(R.id.comment_user_name) as TextView
        val commText = itemView.findViewById(R.id.comment_text) as TextView
        val commDate = itemView.findViewById(R.id.comment_date) as TextView
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
    }
}