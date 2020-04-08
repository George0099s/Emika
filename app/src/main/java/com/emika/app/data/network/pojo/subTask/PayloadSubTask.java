package com.emika.app.data.network.pojo.subTask;

import androidx.room.Entity;

import com.emika.app.data.network.pojo.task.PayloadTask;
import com.google.android.gms.tasks.Task;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity
public class PayloadSubTask  {
    @SerializedName("task")
    @Expose
    private PayloadTask task;
    @SerializedName("sub_tasks")
    @Expose
    private List<SubTask> subTasks = null;
    @SerializedName("comments")
    @Expose
    private List<Object> comments = null;

    public PayloadTask getTask() {
        return task;
    }

    public void setTask(PayloadTask task) {
        this.task = task;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public List<Object> getComments() {
        return comments;
    }

    public void setComments(List<Object> comments) {
        this.comments = comments;
    }

}
