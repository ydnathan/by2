package com.example.helloworld.resources;

import com.example.helloworld.dao.*;
import com.example.helloworld.entities.core.*;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.example.helloworld.entities.RequestRideResponse;
import com.example.helloworld.entities.SearchRidesResponse;
import com.example.helloworld.entities.core.PublishedRide.RideStatus;
import com.example.helloworld.entities.core.Request.RequestStatus;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Created by vaidyanathan.s on 02/05/15.
 */

@Path("/ride")
@Produces(MediaType.APPLICATION_JSON)
public class RideResource {

    private RideDAO rideDAO;
    private UserDAO userDAO;
    private RequestDAO requestDAO;
    private DestinationDAO destinationDAO;
    private CompanyOfficeDAO companyOfficeDAO;
    private RouteDAO routeDAO;
    private RouteDestinationMapDAO routeDestinationMapDAO;
    private PublishedRideDAO publishedRideDAO;

    final static Logger logger = LoggerFactory.getLogger(RideResource.class);

    public RideResource(RideDAO rideDAO, UserDAO userDAO, RequestDAO requestDAO, DestinationDAO destinationDAO, 
    		CompanyOfficeDAO companyOfficeDAO, RouteDAO routeDAO, RouteDestinationMapDAO routeDestinationMapDAO,PublishedRideDAO publishedRideDAO) {
        this.rideDAO = rideDAO;
        this.userDAO = userDAO;
        this.requestDAO = requestDAO;
        this.destinationDAO = destinationDAO;
        this.companyOfficeDAO = companyOfficeDAO;
        this.routeDAO = routeDAO;
        this.routeDestinationMapDAO = routeDestinationMapDAO;
        this.publishedRideDAO = publishedRideDAO;
    }

    @GET
    @Timed
    @UnitOfWork
    public Ride findRide(@QueryParam("id") LongParam id) {
        return rideDAO.findById(id.get());
    }

    @GET
    @Timed
    @Path("all")
    @UnitOfWork
    public List<Ride> allRides() {
        return rideDAO.findAll();
    }

    @GET
    @Timed
    @Path("search")
    @UnitOfWork
    public List<SearchRidesResponse> searchRides(@QueryParam("user_id") Optional<Long> userId, @QueryParam("destination_id") Optional<Long> destinationId) {
        // TODO : basic level of authentication..
        User user = userDAO.findById(userId.get());
        if(user == null) {
            return new ArrayList<SearchRidesResponse>();
        }

        List<Ride> rides = rideDAO.searchRides(destinationId.get());
        List<SearchRidesResponse> searchRidesResponses = new ArrayList<SearchRidesResponse>();
        for(Ride ride : rides) {
            searchRidesResponses.add(new SearchRidesResponse(ride));
        }
        return searchRidesResponses;
    }

    @POST
    @Timed
    @Path("approveRideSeekers")
    @UnitOfWork
    public List<Request> approveRideSeekers(@QueryParam("user_id") Optional<Long> userId, @QueryParam("requestIds") Optional<String> requestIdsStr) {
    	String[] requestIds = requestIdsStr.get().split("\\s*,\\s*");
        List<Long> requestIdList = new ArrayList<Long>();
        for(String requestId : requestIds) {
        	requestIdList.add(Long.parseLong(requestId));
        }
        List<Request> requestList = requestDAO.findById(requestIdList);
        List<Request> approvedList = new ArrayList<Request>();
        for(Request request : requestList){
        	if(request.getStatus() != RequestStatus.REJECTED_LEFT_ALREADY){
	        	request.setUpdatedAt(new Timestamp(Calendar.getInstance().getTime().getTime()));
	        	request.setStatus(RequestStatus.ACCEPTED);
	        	approvedList.add(requestDAO.save(request));
        	}
        }
        return approvedList;
    }
}
