package com.example.helloworld.entities.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by vaidyanathan.s on 17/10/15.
 */
public class BaseEntity {
    public BaseEntity() {
        this.requestCreatedAt = new Timestamp(Calendar.getInstance().getTime().getTime());
        this.requestUpdatedAt = new Timestamp(Calendar.getInstance().getTime().getTime());
    }

    @Column(name="request_created_at")
    @JsonProperty("request_created_at")
    private Timestamp requestCreatedAt;

    @Column(name="request_updated_at")
    @JsonProperty("request_updated_at")
    private Timestamp requestUpdatedAt;


    public Timestamp getRequestCreatedAt() {
        return requestCreatedAt;
    }

    public void setRequestCreatedAt(Timestamp requestCreatedAt) {
        this.requestCreatedAt = requestCreatedAt;
    }

    public Timestamp getRequestUpdatedAt() {
        return requestUpdatedAt;
    }

    public void setRequestUpdatedAt(Timestamp requestUpdatedAt) {
        this.requestUpdatedAt = requestUpdatedAt;
    }
}
