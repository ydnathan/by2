package com.example.helloworld.entities.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by vaidyanathan.s on 02/05/15.
 */
@Entity
@Table(name="CompanyOffice")
public class CompanyOffice {
    public CompanyOffice() {
        // Jackson deserialization
    }

    @Id
    @Column(name="id")
    @GenericGenerator(name="generator", strategy="increment")
    @GeneratedValue(generator="generator")
    private long id;

    @NotEmpty
    @Column(name="office_name")
    @JsonProperty("office_name")
    private String officeName;

    @NotEmpty
    @Column(name="address_text")
    @JsonProperty("address_text")
    private String addressText;

    @NotEmpty
    @Column(name="address_lat_lon")
    @JsonProperty("address_lat_lon")
    private String addressLatLon;

    @NotEmpty
    @Column(name="city")
    @JsonProperty("city")
    private String city;

    @NotEmpty
    @Column(name="state")
    @JsonProperty("state")
    private String state;

    @OneToMany(mappedBy = "companyOffice", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Column(nullable = true)
    @JsonBackReference
    private Set<User> users;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Company company;

    @JsonCreator
    public CompanyOffice(@JsonProperty("company_id") Company company,
                         @JsonProperty("office_name") String officeName,
                         @JsonProperty("address_text") String addressText,
                         @JsonProperty("address_lat_lon") String addressLatLon,
                         @JsonProperty("city") String city,
                         @JsonProperty("state") String state) {
        this.company = company;
        this.officeName = officeName;
        this.addressText = addressText;
        this.addressLatLon = addressLatLon;
        this.city = city;
        this.state = state;
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

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }

    public String getAddressLatLon() {
        return addressLatLon;
    }

    public void setAddressLatLon(String addressLatLon) {
        this.addressLatLon = addressLatLon;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

}
