package com.example.helloworld.resources;

import com.example.helloworld.dao.*;
import com.example.helloworld.entities.core.*;
import io.dropwizard.hibernate.UnitOfWork;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;

/**
 * Created by prashanth.yv on 6/5/15.
 */
@Path("/request")
@Produces(MediaType.APPLICATION_JSON)
public class RequestResource {
    private UserDAO userDAO;
    private CompanyOfficeDAO companyOfficeDAO;
    private DestinationDAO destinationDAO;
    private RouteDAO routeDAO;
    private RequestDAO requestDAO;

    final static Logger logger = LoggerFactory.getLogger(UserResource.class);

    public RequestResource(UserDAO userDAO, CompanyOfficeDAO companyOfficeDAO, DestinationDAO destinationDAO, RouteDAO routeDAO,RequestDAO requestDAO) {
        this.companyOfficeDAO = companyOfficeDAO;
        this.userDAO = userDAO;
        this.destinationDAO = destinationDAO;
        this.routeDAO = routeDAO;
        this.requestDAO = requestDAO;
    }

    @POST
    @Timed
    @Path("create")
    @UnitOfWork
    public Request createRequest(@FormParam("company_office_id") Optional<Long> companyOfficeId,
                                                 @FormParam("destination_id") Optional<Long> destinationId,
                                                 @FormParam("user_id") Optional<Long> userId
                                                 ) {
        CompanyOffice source = companyOfficeDAO.findById(companyOfficeId.get());
        User user = userDAO.findById(userId.get());
        Destination destination = destinationDAO.findById(destinationId.get());
        return requestDAO.create(new Request(user, source, destination));
        //return dao.create(new Destination(areaCode.get(), areaText.get(), city.get(), state.get()));

    }
    
    @GET
    @Timed
    @Path("get")
    @UnitOfWork
    public List<Request> findRequest(@QueryParam("destination_id") Optional<Long> destinationId) {
        Destination destination = destinationDAO.findById(destinationId.get());
    	return requestDAO.findRequestsByDestinationId(destination);
        //return dao.create(new Destination(areaCode.get(), areaText.get(), city.get(), state.get()));

    }
    
    @GET
    @Timed
    @Path("get2")
    @UnitOfWork
    public List<Request> findRequest(@QueryParam("destination_id") Optional<Long> destinationId,
    		@QueryParam("source_id") Optional<Long> sourceId) {
        Destination destination = destinationDAO.findById(destinationId.get());
        CompanyOffice source = companyOfficeDAO.findById(sourceId.get());
    	return requestDAO.findRequestsByDestinationIdAndSourceId(destination, source);

    }


}
