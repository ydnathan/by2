package com.example.helloworld.resources;

import com.codahale.metrics.annotation.Timed;
import com.example.helloworld.dao.CompanyDAO;
import com.example.helloworld.dao.CompanyOfficeDAO;
import com.example.helloworld.entities.core.Company;
import com.example.helloworld.entities.core.CompanyOffice;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by vaidyanathan.s on 10/05/15.
 */
@Path("/company_offices")
@Produces(MediaType.APPLICATION_JSON)
public class CompanyOfficeResource {
    private CompanyOfficeDAO companyOfficeDAO;
    private CompanyDAO companyDAO;

    public CompanyOfficeResource(CompanyOfficeDAO companyOfficeDAO, CompanyDAO companyDAO) {
        this.companyOfficeDAO = companyOfficeDAO;
        this.companyDAO = companyDAO;
    }

    @POST
    @Timed
    @Path("add")
    @UnitOfWork
    public Long addCompany(@FormParam("company_id") Optional<Long> companyId,
                           @FormParam("office_name") Optional<String> officeName,
                           @FormParam("address_text") Optional<String> addressText,
                           @FormParam("address_lat_lon") Optional<String> addressLatLon,
                           @FormParam("city") Optional<String> city,
                           @FormParam("state") Optional<String> state) {
        Company parentCompany = companyDAO.findById(companyId.get());
        return companyOfficeDAO.create(new CompanyOffice(parentCompany, officeName.get(), addressText.get(), addressLatLon.get(), city.get(), state.get()));
    }

    @GET
    @Timed
    @Path("find")
    @UnitOfWork
    public CompanyOffice findCompany(@QueryParam("id") Optional<Long> company_id) {
        return companyOfficeDAO.findById(company_id.get());
    }

    @GET
    @Timed
    @Path("all")
    @UnitOfWork
    public List<CompanyOffice> allCompanyOffices() {
        return companyOfficeDAO.findAll();
    }

    
    @GET
    @Timed
    @Path("search")
    @UnitOfWork
    public List<CompanyOffice> findCompanyByCityCode(@QueryParam("company") Long companyId) {
        Company parentCompany = companyDAO.findById(companyId);
        return companyOfficeDAO.findOfficesByCompany(parentCompany);
    }

}
