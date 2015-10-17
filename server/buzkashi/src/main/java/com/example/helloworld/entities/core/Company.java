package com.example.helloworld.entities.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by vaidyanathan.s on 02/05/15.
 */
@Entity
@Table(name="Company")
public class Company {
    public Company() {
        // Jackson deserialization
    }

    @Id
    @Column(name="id")
    @GenericGenerator(name="generator", strategy="increment")
    @GeneratedValue(generator="generator")
    private long id;

    @NotEmpty
    @Column(name="company_name")
    @JsonProperty("company_name")
    private String companyName;

    @NotEmpty
    @Column(name="email_domain")
    @JsonProperty("email_domain")
    private String emailDomain;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Column(nullable = true)
    @JsonBackReference
    private Set<CompanyOffice> companyOffices;

    @JsonCreator
    public Company(@JsonProperty("company_name") String companyName,
                   @JsonProperty("email_domain") String emailDomain) {
        this.companyName = companyName;
        this.emailDomain = emailDomain;
    }

    public long getId() {
        return id;
    }

    public String getEmailDomain() {
        return emailDomain;
    }

    public void setEmailDomain(String emailDomain) {
        this.emailDomain = emailDomain;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Set<CompanyOffice> getCompanyOffices() {
        return companyOffices;
    }

    public void setCompanyOffices(Set<CompanyOffice> companyOffices) {
        this.companyOffices = companyOffices;
    }
}
