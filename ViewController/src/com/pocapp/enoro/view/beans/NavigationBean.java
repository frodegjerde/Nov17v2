package com.pocapp.enoro.view.beans;

import java.io.Serializable;

import oracle.adf.controller.TaskFlowId;

public class NavigationBean implements Serializable {
    private String taskFlowId = "/WEB-INF/taskflows/customer_tf.xml#customer_tf";

    public NavigationBean() {
    }

    public TaskFlowId getDynamicTaskFlowId() {
        return TaskFlowId.parse(taskFlowId);
    }

    public void setDynamicTaskFlowId(String taskFlowId) {
        this.taskFlowId = taskFlowId;
    }

    public String customer_tf() {
        setDynamicTaskFlowId("/WEB-INF/taskflows/customer_tf.xml#customer_tf");
        return null;
    }

    public String customerWizard_tf() {
        setDynamicTaskFlowId("/WEB-INF/taskflows/customerWizard_tf.xml#customerWizard_tf");
        return null;
    }

    public String parameter_ts() {
        setDynamicTaskFlowId("/WEB-INF/taskflows/parameter_ts.xml#parameter_ts");
        return null;
    }
}
