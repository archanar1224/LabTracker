package org.nimhans.EHealthCare.model;

import java.util.*;
import org.nimhans.EHealthCare.ActionType;
import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.StatusType;
import org.nimhans.EHealthCare.database.ActionDaoImpl;
import org.nimhans.EHealthCare.database.AssetDaoImpl;

public class AssetId<T> {
	
	protected T assetId;
	
	public AssetId()
	{
		
	}
	
	public AssetId(T id) {
		super();
		this.assetId = id;
	}

	public T getId() {
		return assetId;
	}

	public void setId(T id) {
		this.assetId = id;
	}
	
	public Map<String,String> getChildrenStatus()
	{
		return null;
	}
	
	/* Get all assets under a given root */
	public List<? extends Asset> getAssets(String root)
	{
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		List<? extends Asset> assets = adi.getAssets(root);
		//System.out.println("Inside getassets of asset ID class " + assets.get(0).getRemarks());
		return assets;
	}
	
	
	/* get asset with given assetId */
	public Asset getAsset()
	{
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		
		Asset asset = adi.getAsset(this);
		//System.out.println("Inside getasset of asset ID class " + asset.getRemarks());
		return asset;
	}
	
	/* Updates assets currentFlowIndex */
	public boolean updateAssetFlowIndex(int newFlowIndex)
	{
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		return adi.updateAssetFlowIndex(this, newFlowIndex);
		
	}
	
	/* Updates asset's status. Performs status change only if status is EXITED or FINISHED
	 * 
	 * If an asset's status is changing to finished then we need to update the status of the parents recursively if all its children are finished  
	 */
	public boolean updateAssetStatus(StatusType status)
	{
		System.out.println("status in update"+status.toString());
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		boolean result = true;
		/* Enters only if the status is getting updated to FINISHED or EXITED */
		if(status.equals(StatusType.FINISHED)||status.equals(StatusType.EXITED))
		{
			System.out.println("inside finished");
			Asset asset = adi.getAsset(this);
			AssetId parentId = asset.getParentId();
			if(parentId.getId() == null)//root
			{
				
				System.out.println("root");
				/* If the statusType is finished and we reached the root */
				if(status.equals(StatusType.FINISHED))
				{
				
					Asset root = this.getAsset();
					/* Updates the asset's index 
					 * current station will be 6 (Dummy station that represents all its children are finished 
					 */
					
					if(root.getCurrentFlowIndex()==0)
					this.updateAssetFlowIndex(asset.getCurrentFlowIndex()+1);
				
				}
				/* If something EXITED but not FINISHED roots CFI should be 0 */
				else if(status.equals(StatusType.EXITED))
				{
					Asset root = this.getAsset();
					if(root.getCurrentFlowIndex()==1)
						this.updateAssetFlowIndex(asset.getCurrentFlowIndex()-1);
					result = adi.updateAssetStatus(this,status);
				}
				return true;
			}
			/* Updates current asset's status */
			result = adi.updateAssetStatus(this,status);
			/* Retrieves all children */
			List<? extends Asset> children = adi.getChildren(parentId);
			boolean flag = true;
			System.out.println("no of children"+children.size());
			for(int i=0;i<children.size();i++)
			{
				System.out.println("checking children");
				System.out.println("child"+children.get(i).getAssetId().getId());
				StatusType childStatus = children.get(i).getStatus();
				/* If status of children is not finished but the status to be updated is FINISHED then we have to stop the recursion */
				if(status.equals(StatusType.FINISHED)&&!childStatus.equals(StatusType.FINISHED))
				{
					System.out.println("inner child "+children.get(i).getAssetId().getId());
					flag = false;
					break;
				}
				
				/* If status of children is not FINISHED or EXITED but the status to be updated is EXITED then we have to stop the recursion */
				else if(status.equals(StatusType.EXITED)&&!(childStatus.equals(StatusType.EXITED)||childStatus.equals(StatusType.FINISHED)))
				{
					flag = false;
					break;
				}
			}
			/* If status is EXITED and all its children are EXITED or FINISHED
			 * 
			 * If status is FINISHED and all its children are also FINISHED   
			 * 
			 * then update parent's status
			 */
			if(flag)
				parentId.updateAssetStatus(status);
			
		}
		/* If status is ENTERED just update the asset's status */
		else
			result = adi.updateAssetStatus(this,status);
		return result;
	}

}
