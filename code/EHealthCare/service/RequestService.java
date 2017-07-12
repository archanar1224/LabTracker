package org.nimhans.EHealthCare.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.*;

import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.SpecialType;
import org.nimhans.EHealthCare.StatusType;
import org.nimhans.EHealthCare.model.AssetId;
import org.nimhans.EHealthCare.model.Patient;
import org.nimhans.EHealthCare.model.Request;
import org.nimhans.EHealthCare.model.derived.NimhansAsset;
import org.nimhans.EHealthCare.model.derived.NimhansRequest;

import io.swagger.annotations.*;


import org.json.JSONArray;
import org.json.JSONObject;


@Path("/requestservice")
@Api(value="Request Services")
/* set of services that are related to request */
public class RequestService {
	
	@GET
	@Path("/patientdetails")
	@ApiOperation(value="Gets the patients details for a given request ID", response=Patient.class)
	@ApiResponses(value = { 
		      @ApiResponse(code = 400, message = "Patient Data is unavailable"),
		      @ApiResponse(code = 404, message = "Request not found") })
	public Response getPatientDetails(@ApiParam(value = "Request ID") @QueryParam("samplerequestid") String requestId)
	{
		System.out.println("getting patient details");
		Patient patient = Loader.getLabFactory().getRequestObject(requestId).getPatientDetails();
		
		if(patient==null)
		{
			return  Response.status(Status.BAD_REQUEST)
					.entity("Patient Data is unavailable")
					.build();
		}
		
		return  Response.ok()
				.entity(patient)
				.build();
		
	}
	
	@GET
	@Path("/nextroot")
	@ApiOperation(value="Gives the next root", response=String.class)
	@ApiResponses(value = { 
		      @ApiResponse(code = 400, message = "Error in request number"),
		     })
	public Response getRootSuggestion(@ApiParam(value = "Request ID") @QueryParam("requestid") String reqId)
	{
		
		/*
		 * 
		 *If the request is already present then it returns the root assigned to that request else it returns new root 
		 *
		*/
		try
		{
		String root = Loader.getLabFactory().getRequestObject(reqId).getNpBase();
		JSONObject mainObj = new JSONObject();
		System.out.println("root"+root);
		if(root == null)
		{
		System.out.println("INSIDE");
		String nextRoot = Loader.getLabFactory().getIDGeneratorObject().generateNextRoot();
		System.out.println(nextRoot);
		mainObj.put("nextRoot",nextRoot);
		mainObj.put("isRequestPresent",false);
		}
		else
		{
			mainObj.put("nextRoot",root);
			mainObj.put("isRequestPresent",true);
		}
		
			return  Response.ok()
				.entity(mainObj.toString())
				.build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST)
					.entity("Error in request number")
					.build();
		}
		
	}
	
	@POST
	@ApiOperation(value="Saves the root", response=Request.class)
	@ApiResponses(value = { 
		      @ApiResponse(code = 400, message = "Requested NP Number cant be generated"),
		     })
	public Response saveRoot(@ApiParam(value = "Station ID") @QueryParam("stationId") int stationId, @ApiParam(value = "Request Object") Request request)
	{
		
		/*
		 * Takes in a request. 
		 * new Request entered in database with requestID.
		*/
	
		//Request reqObj = Loader.labFactory.getRequestObject();
		//System.out.println("RRR " + request.getNpBase());
		//System.out.println("RRR " + request.getPatientUHID());
		//System.out.println("In req service " + isNpBaseChanged);
		//System.out.println("inside saveRoot "  + request.getRootID() + request.getReqID());
		if(Loader.getLabFactory().getIDGeneratorObject().validateRoot(request))
		{
			System.out.println("valid Root ID");
			boolean result = Loader.getLabFactory().getIDGeneratorObject().saveRoot(request); // ??why is it returning request back CHECK!!!
			NimhansAsset asset = (NimhansAsset)Loader.getLabFactory().getAssetObject();
			asset.setAssetId(new AssetId(request.getRootID()));
			asset.setCurrentFlowIndex(0);
			asset.setParentId(new AssetId(request.getRootID()));
			asset.setAssetType("root");
			asset.setStatus(StatusType.ENTERED);
			asset.setReqId(request.getReqID());
			String remarks = "";
			String biopsy = "";
			String fixative = "";
			String specimen = "";
			asset.setRemarks(remarks);
			asset.setBiopsy(biopsy);
			asset.setFixative(fixative);
			asset.setSpecimen(specimen);
			asset.setSpecialType(SpecialType.NORMAL);
			String userId = "technician1";
			asset.addAsset(stationId,userId);
			// TODO  == check result flag and what to return 
			return  Response.ok()
					.entity(request)
					.build();
		}
		return  Response.status(Status.BAD_REQUEST)
				.entity("Requested NP Number cant be generated")
				.build();
	}
	@GET
	@Path("/getrequeststatus")
	@ApiOperation(value="Gets the Status of the request", response=String.class)
	@ApiResponses(value = { 
		      @ApiResponse(code = 400, message = "Request is not available"),
		     })
	/* returns an object that contains set of assets and their corresponding statuses under that request */
	public Response getRequestStatus(@ApiParam(value = "Request ID") @QueryParam("requestId") String requestId)
	{
		System.out.println("req id"+requestId);
		//String requestStatus = Loader.getLabFactory().getRequestObject(requestId).getRequestStatus();
		String requestStatus = new NimhansRequest(requestId).getRequestStatus();
		String root = new NimhansRequest(requestId).getNpBase();
		System.out.println("At" + requestStatus +" station");
		
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		try
		{
		json.put("assetId", root);
		json.put("status", requestStatus);
		System.out.println(json.getString("assetId")+json.getString("status"));
		ja.put(json);
		JSONObject mainObj = new JSONObject();
		mainObj.put("assetStatuses",ja);
		if(requestStatus != null)
			return  Response.ok()
				.entity(mainObj.toString())
				.build();
		else
			return Response.status(Status.BAD_REQUEST)
					.entity("Request is not available")
					.build();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		return Response.status(Status.BAD_REQUEST)
				.entity("JSON error")
				.build();
		}
	}
	@GET
	@Path("/noofrequestsreceived")
	@ApiOperation(value="Finds the number of the requests received", response=String.class)
	
	@Produces(MediaType.APPLICATION_JSON)
	public Response totalRequestsReceived(@ApiParam(value = "Start date") @QueryParam("startDate") String startDate, @ApiParam(value = "End date") @QueryParam("endDate") String endDate)   //no of requests came today
	{
		int noOfRequests = Loader.getLabFactory().getRequestObject().totalRequestsReceived(startDate,endDate);
		return  Response.ok()
				.entity(Integer.toString(noOfRequests))
				.build();
		
	}
	@GET
	@Path("/noofrequestscompleted")
	@ApiOperation(value="Finds the number of the requests completed", response=String.class)
	
	@Produces(MediaType.APPLICATION_JSON)
	public Response totalRequestsCompleted(@ApiParam(value = "Start date") @QueryParam("startDate") String startDate, @ApiParam(value = "End date") @QueryParam("endDate") String endDate)   //no of requests came today
	{
		System.out.println("start"+startDate+"end"+endDate);
		int noOfRequests = Loader.getLabFactory().getRequestObject().totalRequestsCompleted(startDate,endDate);
		
		return  Response.ok()
				.entity(Integer.toString(noOfRequests))
				.build();
		
	}
	

}
