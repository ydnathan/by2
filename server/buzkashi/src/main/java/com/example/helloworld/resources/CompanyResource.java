package com.example.helloworld.resources;

import com.codahale.metrics.annotation.Timed;
import com.example.helloworld.entities.core.Company;
import com.example.helloworld.dao.CompanyDAO;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by vaidyanathan.s on 10/05/15.
 */
@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
public class CompanyResource {
    private CompanyDAO dao;

    public CompanyResource(CompanyDAO dao) {
        this.dao = dao;
    }

    @POST
    @Timed
    @Path("add")
    @UnitOfWork
    public Long addCompany(@FormParam("company_name") Optional<String> companyName,
                           @FormParam("email_domain") Optional<String> emailDomain) {
        return dao.create(new Company(companyName.get(), emailDomain.get()));
    }

    @GET
    @Timed
    @Path("find")
    @UnitOfWork
    public Company findCompany(@QueryParam("id") Optional<Long> company_id) {
        return dao.findById(company_id.get());
    }

    @GET
    @Timed
    @Path("all")
    @UnitOfWork
    public List<Company> allCompanies() {
        return dao.findAll();
    }

    
    @GET
    @Timed
    @Path("search")
    @UnitOfWork
    public List<Company> findCompanyByCityCode(@QueryParam("city") String city) {
        return dao.findCompaniesByCityCode(city);
    }

}
