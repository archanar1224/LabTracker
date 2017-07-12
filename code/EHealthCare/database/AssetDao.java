package org.nimhans.EHealthCare.database;

import java.util.List;

import org.nimhans.EHealthCare.model.Asset;
import org.nimhans.EHealthCare.model.AssetId;

public interface AssetDao 
{
	//public Asset getAsset(AssetId assetId);
	public <T> List<T> getAssets(String root);
	public String getAssetStatus(AssetId assetId);
	public boolean updateAsset(String assetIDValue, String biopsy, String fixative);
	
}