package org.nimhans.EHealthCare.service;

import java.util.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.model.Asset;
import org.nimhans.EHealthCare.model.AssetId;
import org.nimhans.EHealthCare.model.Patient;
import org.nimhans.EHealthCare.model.derived.NimhansAsset;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Path("/stationservice")
@Api(value="Station Services")
public class StationService {
	
	@GET
	@Path("/stationstatistics")
	@ApiOperation(value="Gets all stations statistics", response=String.class)	
	public Response stationStatistics()
	{
		Map<String,Integer> stationAssetMap = Loader.getLabFactory().getStationObject().getAssetCount();
		Set<String> nameSet = new HashSet<String>();
		if(stationAssetMap != null)
		{
			nameSet = stationAssetMap.keySet();
		}
		JSONArray ja = new JSONArray();
		try
		{
		for(String name:nameSet)
		{
			JSONObject jo = new JSONObject();
			jo.put("stationName",name);
			jo.put("assetCount", stationAssetMap.get(name));
			
			ja.put(jo);
			
		}

		JSONObject mainObj = new JSONObject();
		mainObj.put("stations",ja);
		return  Response.ok()
                .entity(mainObj.toString())
                .build();
		}
		catch(Exception e)
		{
			return Response.status(Status.BAD_REQUEST)
			.entity("Request is not available")
			.build();
		}
		
	}

	
	@GET
	@Path("/scan")
	@ApiOperation(value="Scans the asset at a given station ID and validates", response=Asset.class)
	@ApiResponses(value = { 
		      @ApiResponse(code = 400, message = "Wrong station")
		      })
	@Produces(MediaType.APPLICATION_JSON)
	public Response scanAsset(@ApiParam(value = "Station ID") @QueryParam("stationId") int stationId, @ApiParam(value = "Asset ID") @QueryParam("assetId") String assetIdValue)
	{
		System.out.println("assetIdvalue " + assetIdValue+"stationid"+stationId);
		AssetId assetId = Loader.getLabFactory().getAssetIdObject(assetIdValue);
		boolean result = Loader.getLabFactory().getStationObject(stationId).scanAsset(assetId);
		//scanAsset(stationId, assetId)
		System.out.println(result);
		if(result)
		{
			NimhansAsset asset = (NimhansAsset) assetId.getAsset();
			System.out.println("In stn service scan: " + asset.getCurrentFlowIndex());
			// Send asset back
			return  Response.ok()
			.entity(asset)
			.build();
		}
		else
		{
			System.out.println("scan Not okay");
			// Wrong station, send error message
			return  Response.status(Status.BAD_REQUEST)
					.entity("Wrong station")
					.build();
		}
		

	}

	
	@GET
    @Path("/pendingassets")
	@ApiOperation(value="Returns the pending assets at a given station", response=List.class)
    public List<NimhansAsset> getPendingTasks(@ApiParam(value = "Station ID") @QueryParam("stationId") String stationIdString)
    {
		int stationId = Integer.parseInt(stationIdString);
		System.out.println("inside pending tasks "+ stationId);
		List<NimhansAsset> assetList = (List<NimhansAsset>) Loader.getLabFactory().getStationObject(stationId).getPendingTasks(stationId);
		//System.out.println("after pendingtasks " + assetList.size());
		return assetList;
    }
	@GET
    @Path("/transactions")
	@ApiOperation(value="Returns the transactions occured at that station during the interval", response=String.class)
	@ApiResponses(value = { 
		      @ApiResponse(code = 400, message = "No transactions")
		      })
	public Response getTransactions(@ApiParam(value = "Station ID") @QueryParam("stationId") String stationIdString, @ApiParam(value = "Start date of interval") @QueryParam("startDate") String startDate, @ApiParam(value = "End date of interval") @QueryParam("endDate") String endDate) throws JSONException
	{
		int stationId = Integer.parseInt(stationIdString);
		System.out.println("stationid"+stationId+"service");
		JSONObject result = Loader.getLabFactory().getStationObject(stationId).getTransactions(startDate, endDate);
		
		if(result!=null)
		{
			
			System.out.println(result.getJSONArray("transactions").getJSONObject(0).getString("assetId"));
			return  Response.ok()
					.entity(result.toString())
				.build();
		}
		else
			return  Response.status(Status.BAD_REQUEST)
					.entity("No transactions")
					.build();
		
	}
	
	
	
	@GET
    @Path("/completed")
	@ApiOperation(value="Returns the completed taks at a given station", response=List.class)
    public List<NimhansAsset> getCompletedTasks(@ApiParam(value = "Station ID") @QueryParam("stationId") int stationId)
    {
		System.out.println("inside completed tasks "+ stationId);
		List<NimhansAsset> assetList = (List<NimhansAsset>) Loader.getLabFactory().getStationObject(stationId).getCompletedTasks(stationId);
		System.out.println("after completed " + assetList.size());
		return assetList;
    }
	
}
