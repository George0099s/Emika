package com.emika.app.presentation.utils;

import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.network.pojo.task.PayloadTask;

import java.util.ArrayList;
import java.util.List;

public class Converter {
    private List<TaskEntity> taskEntities;
    private List<PayloadTask> payloadTaskList;
    private PayloadTask payloadTask;
    private TaskEntity taskEntity;

    public Converter() {
        taskEntities = new ArrayList<>();
        payloadTaskList = new ArrayList<>();
    }

    public List<TaskEntity> fromPayloadTaskToTaskEntityList(List<PayloadTask> taskList) {
        for (int i = 0; i < taskList.size(); i++) {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId(taskList.get(i).getId());
            taskEntity.setAssignee(taskList.get(i).getAssignee());
            taskEntity.setCompanyId(taskList.get(i).getCompanyId());
            taskEntity.setCreatedAt(taskList.get(i).getCreatedAt());
            taskEntity.setCreatedBy(taskList.get(i).getCreatedBy());
            taskEntity.setPriority(taskList.get(i).getPriority());
            taskEntity.setDeadlineDate(taskList.get(i).getDeadlineDate());
            taskEntity.setDeadlineEmika(taskList.get(i).getDeadlineEmika());
            taskEntity.setDeadlineTime(taskList.get(i).getDeadlineTime());
            taskEntity.setDeadlinePeriod(taskList.get(i).getDeadlinePeriod());
            taskEntity.setDescription(taskList.get(i).getDescription());
            taskEntity.setDuration(taskList.get(i).getDuration());
            taskEntity.setPlanDate(taskList.get(i).getPlanDate());
            taskEntity.setName(taskList.get(i).getName());
            taskEntities.add(taskEntity);
        }
        return taskEntities;
    }

    public List<PayloadTask> fromTaskEntityToPayloadTaskList(List<TaskEntity> taskList) {
        for (int i = 0; i < taskList.size(); i++) {
            PayloadTask payloadTask = new PayloadTask();
            payloadTask.setId(taskList.get(i).getId());
            payloadTask.setAssignee(taskList.get(i).getAssignee());
            payloadTask.setCompanyId(taskList.get(i).getCompanyId());
            payloadTask.setCreatedAt(taskList.get(i).getCreatedAt());
            payloadTask.setCreatedBy(taskList.get(i).getCreatedBy());
            payloadTask.setPriority(taskList.get(i).getPriority());
            payloadTask.setDeadlineDate(taskList.get(i).getDeadlineDate());
            payloadTask.setDeadlineEmika(taskList.get(i).getDeadlineEmika());
            payloadTask.setDeadlineTime(taskList.get(i).getDeadlineTime());
            payloadTask.setDeadlinePeriod(taskList.get(i).getDeadlinePeriod());
            payloadTask.setDescription(taskList.get(i).getDescription());
            payloadTask.setDuration(taskList.get(i).getDuration());
            payloadTask.setPlanDate(taskList.get(i).getPlanDate());
            payloadTask.setName(taskList.get(i).getName());
            payloadTaskList.add(payloadTask);
        }
        return payloadTaskList;
    }

    public PayloadTask fromTaskEntityToPayloadTask(TaskEntity task) {
        this.payloadTask = new PayloadTask();
        payloadTask.setId(task.getId());
        payloadTask.setAssignee(task.getAssignee());
        payloadTask.setCompanyId(task.getCompanyId());
        payloadTask.setCreatedAt(task.getCreatedAt());
        payloadTask.setCreatedBy(task.getCreatedBy());
        payloadTask.setDeadlineDate(task.getDeadlineDate());
        payloadTask.setDeadlineEmika(task.getDeadlineEmika());
        payloadTask.setDeadlineTime(task.getDeadlineTime());
        payloadTask.setDeadlinePeriod(task.getDeadlinePeriod());
        payloadTask.setDescription(task.getDescription());
        payloadTask.setDuration(task.getDuration());
        payloadTask.setPlanDate(task.getPlanDate());
        payloadTask.setName(task.getName());
        return payloadTask;
    }

    public TaskEntity fromPayloadTaskToTaskEntity(PayloadTask task) {
        this.taskEntity = new TaskEntity();
        taskEntity.setId(task.getId());
        taskEntity.setAssignee(task.getAssignee());
        taskEntity.setCompanyId(task.getCompanyId());
        taskEntity.setCreatedAt(task.getCreatedAt());
        taskEntity.setCreatedBy(task.getCreatedBy());
        taskEntity.setPriority(task.getPriority());
        taskEntity.setDeadlineDate(task.getDeadlineDate());
        taskEntity.setDeadlineEmika(task.getDeadlineEmika());
        taskEntity.setDeadlineTime(task.getDeadlineTime());
        taskEntity.setDeadlinePeriod(task.getDeadlinePeriod());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setDuration(task.getDuration());
        taskEntity.setPlanDate(task.getPlanDate());
        taskEntity.setName(task.getName());
        return taskEntity;
    }
}
