package org.nimhans.EHealthCare.model;

import java.util.*;


import javax.ws.rs.QueryParam;

import org.nimhans.EHealthCare.ActionType;
import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.StatusType;
import org.nimhans.EHealthCare.database.AssetDaoImpl;
import org.nimhans.EHealthCare.database.StationDaoImpl;
import org.nimhans.EHealthCare.model.derived.NimhansAsset;
import org.json.JSONObject;

public class Station {
	
	private int stationId;
	private String stationName;
	public Station()
	{
		
	}
	public Station(int stationId)
	{
		this.stationId = stationId;
	}
	public Station(int stationId, String stationName) {
		super();
		this.stationId = stationId;
		this.stationName = stationName;
	}
	public int getStationId() {
		return stationId;
	}
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	
	
	/* validates whether the asset scanned in correct station or not */
	public boolean validate(AssetId assetId)
	{
		
		boolean result = false;
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		NimhansAsset asset = adi.getAsset(assetId);
		String rootId = assetId.getId().toString().split(":")[0];
		System.out.println("rootid"+rootId);
		NimhansAsset rootAsset = adi.getAsset(new AssetId(rootId));
		
		/* If root is special */
		if(!(rootAsset.getSpecialType().toString().equals("NORMAL")))
		{
			/* Assign ids according to the special status of the root */
			int specialId = 2;
			if(rootAsset.getSpecialType().toString().equals("SPECIAL_SECTIONING"))
				specialId = 4;
			if(rootAsset.getSpecialType().toString().equals("SPECIAL_STAINING"))
				specialId = 5;
			/* The staionId less than the SpecialStationId is not allowed */
			if(this.getStationId()<specialId)
			{
				System.out.println("this station not allowed");
				return false;
				
			}
			
			
		}
		
		
		
		int currentFlowIndex = asset.getCurrentFlowIndex();
		Flow flowId = asset.getFlowId();
		
		String flow = adi.getFlow(flowId);
		flowId.setFlow(flow);
		System.out.println("in validate cfi:"+ currentFlowIndex + " : flow: "+ flow);
		
		String stations[] = flow.split(":");
		String stationIdString = Integer.toString(this.getStationId());
		System.out.println("new"+stations[0]);
		if(stations[0].equals(stationIdString))//asset should get created there.should not be scanned
		{
			System.out.println("should get created should not be scanned");
			return false;
		}
		/* add below snippet if there is a condition that 
		 * 
		 * an asset can enter this station only if the nextstation in its flow is the current station 
		 */
		/*if(flowLength > currentFlowIndex+1)
		{
			int nextStation = Integer.parseInt(stations[currentFlowIndex + 1]);
		
			if(this.stationId == nextStation)
			{
				//then correct
				result = true;
			}
		}*/
		int stationIndex=-1;
		
		/* Get series of stations in asset's flow */
		for(int i=0;i<stations.length;i++)
		{
			if(stations[i].equals(stationIdString))
			{
				stationIndex = i;
				break;
			}
		}
		System.out.println("stationindex"+stationIndex);
		if(stationIndex==-1)//station is not present in asset's flow
			return false;
		if(currentFlowIndex >= stationIndex-1)
			result =true;
		
		return result;
	}
	
	/* returns number of assets that entered the station */
	public Map<String,Integer> getAssetCount()
	{
		StationDaoImpl sdi = Loader.getLabFactory().getStationDao();
		Map<String,Integer> stationAssetMap = sdi.getAssetCount();
		return stationAssetMap;
	}
	
	
	/* scans the asset and does validation and updates the flow of the scan asset */
	public boolean scanAsset(AssetId assetId)
	{
		//First validate
		System.out.println("stationid"+this.getStationId());
		boolean isValid = validate(assetId); // validate and update asset if valid
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		if(isValid)
		{
			System.out.println("Valid asset");
			//update asset's currentFlowIndex
			Asset asset = adi.getAsset(assetId);
			String stationIdString = Integer.toString(this.getStationId());
			//int current_station_id = adi.getCurrentStation(assetId);
			int currentFlowIndex = asset.getCurrentFlowIndex();
			Flow flowId = asset.getFlowId();
			String flow = adi.getFlow(flowId);
			String stations[] = flow.split(":");
			System.out.println("cfi:"+currentFlowIndex);
			
			/* If next station in the flow of asset is same as the station where it scanned then update the assets cfi and status */
			if(currentFlowIndex+1<stations.length&&stations[currentFlowIndex+1].equals(stationIdString))
			{
				assetId.updateAssetFlowIndex(currentFlowIndex + 1);
				assetId.updateAssetStatus(StatusType.ENTERED);
				
			}
						
			
			
			
			// Add a entry transaction 
			Date currentTime = new Date();
			Action action = Loader.getLabFactory().getActionObject(assetId, this.getStationId(), ActionType.ENTER, currentTime, "technician1");;
			boolean result = action.triggerAction();
			
			
			return true;
		}
		else
		{
			System.out.println("not a valid asset");
			return false;
		}
				
		
	}
	
	/* Get transactions related to this station in given time period */
	public JSONObject getTransactions(String startDate,String endDate)
	{
		System.out.println("in model" +" station id = "+this.getStationId());
		StationDaoImpl sdi = Loader.getLabFactory().getStationDao();
		JSONObject result = sdi.getTransactions(this.getStationId(),startDate,endDate);
		return result;
	}
	
	
	/* Get pending tasks for a station */
	public List<? extends Asset> getPendingTasks(int stationId)
    {
   	 	StationDaoImpl stationDao = Loader.getLabFactory().getStationDao();
   	 	List<? extends Asset> assetList = stationDao.getPendingTasks(stationId);
   	 	return assetList;
    }
	/* Get completed tasks for a station */
	public List<? extends Asset> getCompletedTasks(int stationId)
    {
   	 	StationDaoImpl stationDao = Loader.getLabFactory().getStationDao();
   	 	List<? extends Asset> assetList = stationDao.getCompletedTasks(stationId);
   	 	return assetList;
    }


}
