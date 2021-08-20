package com.convertlab;

public class Task {
    String taskId;
    String status;
    String tenantId;

    public Task(String taskId, String status, String tenantId, String type) {
        this.taskId = taskId;
        this.status = status;
        this.tenantId = tenantId;
        this.type = type;
    }

    public Task() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;
}
