package org.nimhans.EHealthCare.model.derived;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.SpecialType;
import org.nimhans.EHealthCare.StatusType;
import org.nimhans.EHealthCare.database.*;
import org.nimhans.EHealthCare.model.*;

public class NimhansRequest extends Request{
	
	
	public NimhansRequest()
	{
		super();
	}
	
	public NimhansRequest(String reqId)
	{
		
		super(reqId);
	}
	
	/* Station where the given request is present (returns earliest station among all asset stations */
	public String getRequestStatus()
	{
		RequestDaoImpl requestDao = Loader.getLabFactory().getRequestDao();
		StationDaoImpl sdi = Loader.getLabFactory().getStationDao();
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		/* Get rootId for given request */
		String root = requestDao.getNpBase(this.getReqID());
		System.out.println("root id "+root);
		NimhansAssetId rootId = new NimhansAssetId(root);
		/* Get current station of root */
		int stationId = adi.getCurrentStation(rootId);
		String stationName = "";
		NimhansAsset asset = adi.getAsset(rootId);
		if(!asset.getSpecialType().equals(SpecialType.NORMAL))
		{
			stationName = "SPECIAL";
		}
		/* If root is in receiving station */
		if(stationId == 1)
		{
			String status = adi.getAssetStatus(rootId);
			System.out.println("root status "+status);
			/* If status of root is ENTERED return that station name */
			if(status.equals(StatusType.ENTERED.toString()))
				return sdi.getStationName(stationId);
			
			/* If status is EXITED find the earliest station depending on its children */
			else
			{
				stationId = this.getEarliestStationId(rootId);
				System.out.println("station id "+stationId);
				return stationName+" "+sdi.getStationName(stationId);
			}
		}
		if(stationId == 6)
		{
			return stationName+" "+ "STAINING";
		}
		/* If not receiving station then return the current station's name */
		else
		{
			
			return stationName+" "+sdi.getStationName(stationId);
			
		}
		
	}
	
	
	
	/* Get earliest stationId among its children */
	public int getEarliestStationId(AssetId assetId)
	{
		/* Assigning a maximum value initially */
		int earliestStationId = 100;
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		int station = adi.getCurrentStation(assetId);
		/* get the children assets */
		List<NimhansAsset> rootChildren = adi.getChildren(assetId);
		
		/* If no children return the asset's current station */
		if(rootChildren == null||rootChildren.size()==0)
		{
			return station;
		}
		/* Asset has children. So check its children's stations */
		/* Add children assets to a queue */
		Queue<Asset> assetQueue = new LinkedList<Asset>();
		assetQueue.addAll(rootChildren);
		while(!assetQueue.isEmpty())
		{
			/* Remove each asset from queue */
			Asset asset = assetQueue.remove();
			String status = adi.getAssetStatus(asset.getAssetId());
			
			/* If status is ENTERED then no need to check its children's status if it is less than earliest station id then change earliestStationId */
			if(status.equals(StatusType.ENTERED.toString()))
			{
				int currentStationId = adi.getCurrentStation(asset.getAssetId());
				System.out.println("asset_id"+asset.getAssetId().getId().toString()+"station "+currentStationId+"inrequest");
				if(earliestStationId > currentStationId)
					earliestStationId = currentStationId;
				
			}
			
			/* If status type is EXITED then check its children by adding them to the queue */
			else if(status.equals(StatusType.EXITED.toString()))
			{
				List<NimhansAsset> assetChildren = adi.getChildren(asset.getAssetId());
				if(assetChildren!=null&&assetChildren.size()>0)
				{
					assetQueue.addAll(assetChildren);
				}
				else //no children
				{
					int currentStationId = adi.getCurrentStation(asset.getAssetId());
					System.out.println("in else asset_id"+asset.getAssetId().getId().toString()+"station "+currentStationId+"inrequest");
					if(earliestStationId > currentStationId)
						earliestStationId = currentStationId;
				}
			}
			/* if status is finished then the entire tree for a given root completed staining so return stationId 5 (Staining) */
			else if(status.equals(StatusType.FINISHED.toString()))
			{
				earliestStationId = 5;
			}
		}
		return earliestStationId;
	}
	

}
