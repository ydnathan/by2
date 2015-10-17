package com.example.helloworld.entities.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

/**
 * Created by vaidyanathan.s on 10/05/15.
 */

@Entity
@Table(name="Request")
public class Request {
    public Request() {
        // Jackson deserialization
    }

    public enum RequestStatus {
        REQUESTED, ACCEPTED, REJECTED_LEFT_ALREADY, REJECTED_RIDE_FULL
    }

    @Id
    @Column(name="id")
    @GenericGenerator(name="generator", strategy="increment")
    @GeneratedValue(generator="generator")
    private long id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private CompanyOffice source;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Destination destination;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;

    @Column(name="status")
    @JsonProperty("status")
    @Enumerated(EnumType.ORDINAL)
    private RequestStatus status;


//user_id, request_created_at, status (cancelled, confirmed, pending, expired, deleted), acked_giver, source_id, destination_ids(CSV)
    @JsonCreator
    public Request(@JsonProperty("user") User user,
                @JsonProperty("source") CompanyOffice companyOffice,
                @JsonProperty("destination") Destination destination
    ) {
        super();
        this.user = user;
        this.source = companyOffice;
        this.destination = destination;
        this.status = RequestStatus.REQUESTED;
        this.createdAt = new Timestamp(Calendar.getInstance().getTime().getTime());
        this.updatedAt = new Timestamp(Calendar.getInstance().getTime().getTime());
    }

    @Column(name="created_at")
    @JsonProperty("created_at")
    private Timestamp createdAt;

    @Column(name="updated_at")
    @JsonProperty("updated_at")
    private Timestamp updatedAt;


    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Destination getDestination() {
        return destination;
    }

    public Long getDestinationId() {
        return destination.getId();
    }

    public Long getDestination_id() {
        return destination.getId();
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public CompanyOffice getSource() { return source; }

    public void setSource(CompanyOffice source) { this.source = source; }

}
