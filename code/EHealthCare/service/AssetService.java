package org.nimhans.EHealthCare.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.nimhans.EHealthCare.ActionType;
import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.SpecialType;
import org.nimhans.EHealthCare.StatusType;
import org.nimhans.EHealthCare.model.Asset;
import org.nimhans.EHealthCare.model.AssetId;
import org.nimhans.EHealthCare.model.Patient;
import org.nimhans.EHealthCare.model.derived.NimhansAsset;
import org.nimhans.EHealthCare.model.derived.NimhansAssetId;


import io.swagger.annotations.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Path("/assetservice")
@Api(value="Asset Services")

/* Rest services related to asset */
public class AssetService 
{	
		
	@POST
	@ApiOperation(value="Adds new asset", response=Boolean.class)
	
	/* Add new asset with given parameter values */
	public Response addNewAsset(@ApiParam(value = "Station ID") @QueryParam("stationId") String stationId, @ApiParam(value = "Root ID") @QueryParam("np") String npBase, @ApiParam(value = "Parent ID") @QueryParam("parent") String parentIdValue, @ApiParam(value = "Quantity ID") @QueryParam("quantity") int quantity, @ApiParam(value = "Asset Type ID") @QueryParam("assetType") String assetType, @ApiParam(value = "Asset") NimhansAsset asset)
	{
	    
	    System.out.println("Inside asset service post");
	    System.out.println("request id"+asset.getReqId());
	    System.out.println("asset type"+assetType);
	    boolean result=false;
	    StatusType status = StatusType.EXITED;// Asset exited Receiving after creation
	    asset.setStatus(status);
	    asset.setParentId(new AssetId<String>(parentIdValue));
	    asset.setAssetType(assetType);
	    System.out.println("status"+status);
	    asset.setCurrentFlowIndex(0);
	    String userId = "technician1";
	    System.out.println("quantity " +quantity);
	    boolean isStatusUpdated,isRootStatusUpdated;
	    int stationID = Integer.parseInt(stationId);
	    for(int i=1;i<=quantity;i++)
	    {
	    	 result = asset.addAsset(stationID,userId);
	    	  if(result)
	    	{
	    		/* Update the parent asset's status as EXITED */	
	    		isStatusUpdated = Loader.getLabFactory().getAssetIdObject(parentIdValue).updateAssetStatus(StatusType.EXITED);
			/* Update roots cfi(If it is at 6th station but one more asset got created then the cfi should change */
	    		isRootStatusUpdated = Loader.getLabFactory().getAssetIdObject(npBase).updateAssetFlowIndex(0);
	    		AssetId parentId = Loader.getLabFactory().getAssetIdObject(parentIdValue);
			/* Trigger EXIT action on parent */
	    		Loader.getLabFactory().getActionObject(parentId,stationID, ActionType.EXIT, new Date(), userId).triggerAction();
	    		
	    	}
	    }
	   	 
	    
	    return  Response.ok()
	   		 .entity(result)
	   		 .build();
	}
	
	
	
	@POST
	@Path("/addblock")
	@ApiOperation(value="Adds block for a given tissue", response=Boolean.class)
	/* adds block */
	public Response addBlock(@ApiParam(value = "Station ID") @QueryParam("stationId") String stationId, @ApiParam(value = "Asset") NimhansAsset asset)
	{
		System.out.println("Inside asset service post");
	    boolean result=false;
	    /*StatusType blockStatus = StatusType.ENTERED; // ongoing
	    int currentFlowIndex = 0;
	    AssetId parentAssetId = asset.getAssetId();
	    String parentIdValue = (String) parentAssetId.getId();
	    
	    String reqId = asset.getReqId();
	    String assetType = "block";
	    String biopsy = asset.getBiopsy();
	    String fixative = asset.getFixative();*/
	    
	    String userId = "technician1";
	    String remarks = ""; 
	    asset.setParentId(asset.getAssetId());
	    String root = ((String)asset.getParentId().getId()).split(":")[0];
	    asset.setCurrentFlowIndex(0);
	    asset.setStatus(StatusType.EXITED);
	    int stationID = Integer.parseInt(stationId);
	    asset.setRemarks(remarks);
	    asset.setAssetType("block");
	    String specimen = "";
	    asset.setSpecimen(specimen);
	    result = asset.addAsset(stationID,userId);
	    if(result)
    	{
		/* Update parent's status as EXITED */
    		boolean isStatusUpdated = Loader.getLabFactory().getAssetIdObject(asset.getParentId().getId().toString()).updateAssetStatus(StatusType.EXITED);
		/* Update root's cfi */
    		boolean isRootStatusUpdated = Loader.getLabFactory().getAssetIdObject(root).updateAssetFlowIndex(0);
    		
		/* Trigger exit action on parent asset(Tissue)*/
    		Loader.getLabFactory().getActionObject(asset.getParentId(), stationID, ActionType.EXIT, new Date(), userId).triggerAction();
    	}
		return  Response.ok()
		   		 .entity(result)
		   		 .build();
	}
	@POST
	@Path("/addslide")
	@ApiOperation(value="Adds slides for a given block", response=Boolean.class)
	/* Method to add slide */
	public Response addSlide(@ApiParam(value = "Station ID") @QueryParam("stationId") String stationId, @ApiParam(value = "Quantity") @QueryParam("quantity") String quantity, @ApiParam(value = "Asset") NimhansAsset asset)
	{
		System.out.println("Inside asset service post add slide");
		System.out.println("quantity "+quantity);
	    boolean result=false;
	    /*StatusType blockStatus = StatusType.ENTERED; // ongoing
	    int currentFlowIndex = 0;
	    AssetId parentAssetId = asset.getAssetId();
	    String parentIdValue = (String) parentAssetId.getId();
	    String root = ((String)parentAssetId.getId()).split(":")[0];
	    String reqId = asset.getReqId();
	    String assetType = "block";
	    String biopsy = asset.getBiopsy();
	    String fixative = asset.getFixative();*/
	    String userId = "technician1";
	   // String remarks = ""; 
	    asset.setParentId(asset.getAssetId());
	    String root = ((String)asset.getParentId().getId()).split(":")[0];
	    System.out.println("parent id "+asset.getParentId().getId().toString());
	    asset.setCurrentFlowIndex(0);
	    asset.setStatus(StatusType.EXITED);
	    int stationID = Integer.parseInt(stationId);
	    asset.setAssetType("slide");
	    int quantityValue = Integer.parseInt(quantity);
	    for(int i=0;i<quantityValue;i++)
	    result = asset.addAsset(stationID,userId);
	    if(result)
    	{
		/* Update parent's asset status as EXITED */
    		boolean isStatusUpdated = Loader.getLabFactory().getAssetIdObject(asset.getParentId().getId().toString()).updateAssetStatus(StatusType.EXITED);
		/* Update root's cfi */
    		boolean isRootStatusUpdated = Loader.getLabFactory().getAssetIdObject(root).updateAssetFlowIndex(0);
		/* Trigger exit action on parent asset */
    		Loader.getLabFactory().getActionObject(asset.getParentId(), stationID, ActionType.EXIT, new Date(), userId).triggerAction();
    	}
		return  Response.ok()
		   		 .entity(result)
		   		 .build();
	}
	
	@POST
	@Path("/completeslide")
	@ApiOperation(value="To update the status on the given slide", response=Boolean.class)
	/* when a slide finishes staining */
	public Response completeSlide(@ApiParam(value = "Asset ID") @QueryParam("assetId") String assetIdValue)
	{
		System.out.println("in complete assetId = "+assetIdValue);
		StatusType status = StatusType.FINISHED;
		String userId = "technician1";
		int stationId = 5;
		AssetId assetId = Loader.getLabFactory().getAssetIdObject(assetIdValue);
		assetId.updateAssetFlowIndex(1);
		/* Update asset status as FINISHED */
		boolean result = assetId.updateAssetStatus(status);
		Loader.getLabFactory().getActionObject(assetId, stationId ,ActionType.ENTER, new Date(), userId).triggerAction();
		Loader.getLabFactory().getActionObject(assetId, stationId ,ActionType.EXIT, new Date(), userId).triggerAction();
		
		return  Response.ok()
		   		 .entity(result)
		   		 .build();
	}
	@POST
	@Path("/completeblock")
	@ApiOperation(value="To update the status of a block", response=Boolean.class)
	public Response completeBlock(@ApiParam(value = "Asset ID") @QueryParam("assetId") String assetIdValue)
	{
		System.out.println("in complete assetId = "+assetIdValue);
		StatusType status = StatusType.EXITED;
		String userId = "technician1";
		int stationId = 3;
		AssetId assetId = Loader.getLabFactory().getAssetIdObject(assetIdValue);
		assetId.updateAssetFlowIndex(1);
		/* Update asset status to EXITED */
		boolean result = assetId.updateAssetStatus(status);
		
		Loader.getLabFactory().getActionObject(assetId, stationId ,ActionType.ENTER, new Date(), userId).triggerAction();
		Loader.getLabFactory().getActionObject(assetId, stationId ,ActionType.EXIT, new Date(), userId).triggerAction();
		
		return  Response.ok()
		   		 .entity(result)
		   		 .build();
	}


	@GET
	@Path("/getassets")
	@ApiOperation(value="Returns all assets of a given Root ID", response=List.class)
	@Produces(MediaType.APPLICATION_JSON)
	public List<NimhansAsset> getAssets(@ApiParam(value = "Root ID") @QueryParam("npBase") String root)
	{
		
		List<NimhansAsset> assetList = Loader.getLabFactory().getAssetIdObject().getAssets(root);
		
		return assetList;
		
	}
	
	@PUT
	@Path("/updateasset")
	@ApiOperation(value="Updates biopsy and fixative details for a given asset", response = Boolean.class)
	public Response updateAsset(@ApiParam(value = "Asset ID") @QueryParam("assetId") String assetIdValue, @ApiParam(value = "Biopsy value") @QueryParam("biopsy") String biopsy, @ApiParam(value = "Fixative value") @QueryParam("fixative") String fixative)
	{
		//Update in receiving Station so keeping # extra in function of generating NP Number
		
		System.out.println("inside update asset "+ assetIdValue + " ,, "+ biopsy);
		boolean result = Loader.getLabFactory().getAssetObject().updateAsset(assetIdValue, biopsy, fixative);
		
		//assetService.updateAsset(asset);
		
		return  Response.ok()
				.entity("success")
				.build();
	}
	
	
	
	@DELETE
	@Path("/deleteasset")
	@ApiOperation(value="Deletes a given asset", response = Boolean.class)
	public Response deleteAsset(@ApiParam(value = "Asset ID") @QueryParam("assetId") String assetIdValue)
	{
		System.out.println("inside delete asset "+ assetIdValue);
		boolean result = Loader.getLabFactory().getAssetObject().deleteAsset(assetIdValue);
		
		//assetService.updateAsset(asset);
		
		return  Response.ok()
				.entity("success")
				.build();
	}
	
	
	
	@POST
    @Path("/updatespecial")
	@ApiOperation(value="Updates the asset to include special status tag", response = Boolean.class)
    public Response updateSpecial(@ApiParam(value = "Root ID") @QueryParam("rootID") String assetIdValue, @ApiParam(value = "Special Type value") @QueryParam("specialType") String specialTypeValue)
    {
        
        System.out.println("in update special = " + assetIdValue);
        SpecialType specialType = SpecialType.NORMAL;
	/* If it is a special request the request should point to receiving station */
        int newRootFlowIndex = 0;   
        if(specialTypeValue.equalsIgnoreCase("Special Grossing"))
        {
            specialType = SpecialType.SPECIAL_GROSSING;
            //newRootFlowIndex = 1;
        }
        else if(specialTypeValue.equalsIgnoreCase("Special Sectioning"))
        {
            specialType = SpecialType.SPECIAL_SECTIONING;
            //newRootFlowIndex = 3;
        }
        else if(specialTypeValue.equalsIgnoreCase("Special Staining"))
        {
            specialType = SpecialType.SPECIAL_STAINING;
            //newRootFlowIndex = 4;
        }
        NimhansAssetId assetId = (NimhansAssetId)Loader.getLabFactory().getAssetIdObject(assetIdValue);
        boolean result =assetId.updateAssetSpecialType(specialType, newRootFlowIndex);
        return  Response.ok()
                    .entity(result)
                    .build();
    }
	
	
	
	@GET
	@Path("/getchildrenstatus")
	@ApiOperation(value="Returns the childern's status for a given asset", response = String.class)
	public Response getChildrenStatus(@ApiParam(value = "Asset ID") @QueryParam("assetId") String assetId)
	{
		System.out.println("parentId"+assetId+"in service");
		Map<String,String> statusMap = Loader.getLabFactory().getAssetIdObject(assetId).getChildrenStatus();
		Set<String> idSet = statusMap.keySet();
		JSONArray ja = new JSONArray();
		try
		{
		for(String id:idSet)
		{
			JSONObject jo = new JSONObject();
			jo.put("assetId",id);
			jo.put("status", statusMap.get(id));
			System.out.println(jo.getString("assetId")+jo.getString("status")+"inservice");
			ja.put(jo);
			
		}

		JSONObject mainObj = new JSONObject();
		mainObj.put("assetStatuses",ja);
		return  Response.ok()
                .entity(mainObj.toString())
                .build();
		}
		catch(Exception e)
		{
			return Response.status(Status.BAD_REQUEST)
			.entity("Service is not available")
			.build();
		}
	    
	}
	
	
	
	@GET
	@Path("/search")
	@ApiOperation(value="Search based on filters", response = String.class)
	@ApiResponses(value = { 
		      @ApiResponse(code = 400, message = "No assets available on given filters")
		       })
	public Response search(@ApiParam(value = "Root ID") @QueryParam("rootId") String assetId, @ApiParam(value = "Request ID") @QueryParam("requestId") String requestId, @ApiParam(value = "Patient's UHID") @QueryParam("patientUHID") String patientUhid, @ApiParam(value = "Patient's Name") @QueryParam("patientName") String patientName, @ApiParam(value = "Doctor's Name") @QueryParam("doctorName") String doctorName) throws JSONException
	{
		System.out.println("service assetid="+assetId+" reqid="+requestId+" pid="+patientUhid+" dname="+doctorName+" pname="+patientName);
		JSONObject result = Loader.getLabFactory().getAssetObject().search(assetId, requestId, patientUhid, patientName, doctorName);
		if(result != null)
		{
			System.out.println(result.getJSONArray("search").getJSONObject(0).getString("doctorName"));
			return Response.ok()
	                .entity(result.toString())
	                .build();
		}
		else
			return Response.status(Status.BAD_REQUEST)
            .entity("No assets available on given filters")
            .build();
	
	
	
	}
		
	
	

}
