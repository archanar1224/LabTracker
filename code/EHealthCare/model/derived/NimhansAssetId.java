package org.nimhans.EHealthCare.model.derived;

import java.util.*;

import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.SpecialType;
import org.nimhans.EHealthCare.database.AssetDaoImpl;
import org.nimhans.EHealthCare.database.StationDaoImpl;
import org.nimhans.EHealthCare.model.Asset;
import org.nimhans.EHealthCare.model.AssetId;
import org.nimhans.EHealthCare.model.Station;

public class NimhansAssetId extends AssetId{
	
	public NimhansAssetId()
	{
		
	}
	public NimhansAssetId(String assetId)
	{
		super(assetId);
	}

	public String getAssetId() {
		return (String)assetId;
	}

	
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public List<NimhansAsset> getAssets(String root)
	{
		return super.getAssets(root);
		//AssetDaoImpl adi = new AssetDaoImpl(); 
		//List<NimhansAsset> assets = adi.getAssets(root);
		//System.out.println("Inside getassets of asset ID class " + assets.get(0).getRemarks());
		//return assets;
	}
	
	public NimhansAsset getAsset()
	{
		return (NimhansAsset) super.getAsset();
	}
	
	/* Update specialType for the assetId */ 
	public boolean updateAssetSpecialType(SpecialType specialType, int flowIndex)
    {
        AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
        adi.updateAssetFlowIndex(this, flowIndex);
        return adi.updateAssetSpecialType(this, specialType);
        
    }
	
	/* Update current flow index of asset */
	public boolean updateAssetFlowIndex(int newFlowIndex)
	{
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		if(!((String)this.getId()).contains(":"))//root
		{
			int currentFlowIndex = adi.getCurrentFlowIndex(this);
			if(currentFlowIndex > newFlowIndex)
				return adi.updateAssetFlowIndex(this, newFlowIndex);
			return true;//already in correct position. No updation needed
		}
		return adi.updateAssetFlowIndex(this, newFlowIndex);
		
	}
	
	/* Get current stations of each child asset */
	public Map<String,String> getChildrenStatus()
	{
		Map<String,String> statusMap = new HashMap<String,String>();
		String assetId = this.getAssetId();
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		/* Get list of asset's children */
		List<NimhansAsset> children = adi.getChildren(new AssetId(assetId));
		NimhansRequest request = new NimhansRequest();
		StationDaoImpl sdi = Loader.getLabFactory().getStationDao();
		String stationName;
		
		/* iterate over the children */
		for(int i=0;i<children.size();i++)
		{
			int stationId = request.getEarliestStationId(children.get(i).getAssetId());
			stationName = sdi.getStationName(stationId);
			
			/* if special add special before the station name */
			if(children.get(i).getSpecialType().equals(SpecialType.SPECIAL))
				stationName = "SPECIAL " + stationName;
			statusMap.put(children.get(i).getAssetId().getId().toString(),stationName);
		
		}
		return statusMap;
	}


}
