package com.genuino.crm.quoting.common.dto;

public class WorkflowActionRequest {

    private String actor;
    private String actorRole;

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }
}