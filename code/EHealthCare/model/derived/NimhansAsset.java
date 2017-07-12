package org.nimhans.EHealthCare.model.derived;

import java.util.Date;
import java.util.List;

import org.nimhans.EHealthCare.ActionType;
import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.SpecialType;
import org.nimhans.EHealthCare.StatusType;
import org.nimhans.EHealthCare.database.AssetDaoImpl;
import org.nimhans.EHealthCare.model.Asset;
import org.nimhans.EHealthCare.model.AssetId;
import org.nimhans.EHealthCare.model.Flow;
import org.json.JSONObject;

public class NimhansAsset extends Asset{
	
	private String assetType; // tissue, block,slide etc
	private String biopsy;
	private String fixative;
	private String specimen;
	private SpecialType specialType = SpecialType.NORMAL;
	

	public SpecialType getSpecialType() {
		return specialType;
	}

	public void setSpecialType(SpecialType specialType) {
		this.specialType = specialType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getAssetType() {
		return assetType;
	}

	

	public String getBiopsy() {
		return biopsy;
	}

	public void setBiopsy(String biopsy) {
		this.biopsy = biopsy;
	}

	public String getFixative() {
		return fixative;
	}

	public void setFixative(String fixative) {
		this.fixative = fixative;
	}
	public String getSpecimen() {
		return specimen;
	}

	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}
	public NimhansAsset()
	{
		
	}
	
	public NimhansAsset(AssetId assetId, Flow flowId, int currentFlowIndex, AssetId parentId, StatusType status, String reqId, String remarks, String assetType, String biopsy, String fixative, String specimen,SpecialType specialType) 
	{
		super(assetId,flowId,currentFlowIndex,parentId,status,reqId,remarks);
		this.assetType = assetType;
		this.biopsy = biopsy;
		this.fixative = fixative;
		this.specimen = specimen;
		this.specialType = specialType;
		
	}
	
	
	public NimhansAsset(AssetId assetId, Flow flowId, int currentFlowIndex, AssetId parentId, StatusType status, String reqId, String remarks) 
	{
		super(assetId,flowId,currentFlowIndex,parentId,status,reqId,remarks);
		
	}
	
	
	/* Add new asset */
	
	public boolean addAsset(int stationId,String userId)
    {
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		String flowId = adi.getFlowId(assetType);
		String id = null;
		
		if(!this.getAssetType().equals("root"))
		{
			/* get the maximum assetId assigned till now */
			id = AssetDaoImpl.getCurrentAssetId((String)this.getParentId().getId(),this.getAssetType());
			String rootIdValue = this.getParentId().getId().toString().split(":")[0]; 
			/* Get root asset */
			NimhansAsset rootAsset = adi.getAsset(new NimhansAssetId(rootIdValue));
			
			/* If root asset is special then child also will be special */
			if(!(rootAsset.getSpecialType().toString().equals("NORMAL")))
				this.setSpecialType(SpecialType.SPECIAL);
		}
		System.out.println("current id"+id);
		
		/* If it is a tissue */
		if(this.getAssetType().equals("tissue"))
	   	 {
	   		 System.out.println("type 1");
	   		 
	   		
	   			 if(id==null)
	   			 {
	   				 id="1";
	   			 }
	   			 else
	   			 {
		   			 int next = Integer.parseInt(id);
		   			 next++;
		   			 id = Integer.toString(next);
	   			 }
	   	 }
		
		/* If it is a block */
	   	 else if(this.getAssetType().equals("block"))
	   	 {
	   		 
	   		 
	   			 System.out.println("type 2 : "+ id);
	   			 if(id!=null)
	   			 {
		   			 char c = id.charAt(0);
		   			 c++;
		   			 id = "" + c + "";
		   		 }
	   			 else
	   			 {
	   				id= "A";
	   			 }
	   	 }
		
		/* If it is a slide */
	   	else if(this.getAssetType().equals("slide"))
	  	 {
	  		 
	  			 if(id!=null)
	  			 {
	  				 String sub = id.substring(1);
	  				 System.out.println("sub " + sub);
	  				 int next = Integer.parseInt(sub);
	  				 System.out.println(next + " next");
	  	  			 next++;
	  	  			 id= "S"+Integer.toString(next);
		   		
	  	  			
	  			 }
	  			 else
	  			 {
	  				 id= "S1";
	  			 }
	  			
	  	 }
	  		 
	  	 
		
		
		String asset_id = "";
		
		/*Receiving station can receive anything assign id's according to that */
		if(stationId == 1)
		{
	  		 if(assetType.equals("tissue"))
	  		 {
	  			 asset_id = this.getParentId().getId().toString() + ":" + id; 
	  		 }
	  		 else if(assetType.equals("block"))
	  		 {
	  			asset_id = this.getParentId().getId().toString() + ":##:" + id; 
	  		 }
	  		 else if(assetType.equals("slide"))
	  		 {
	  			asset_id = this.getParentId().getId().toString() + ":##:##:" + id; 
	  			
	  		 }
	  		 else if(assetType.equals("root"))
	  		 {
	  			 System.out.println("asset type is root");
	  			 asset_id = this.getParentId().getId().toString();
	  			 this.setParentId(new AssetId<String>(null));
	  		 }
		}
		else if(stationId == 2)
		{
			asset_id = (String)this.getParentId().getId()+":"+id;
		}
		else if(stationId == 4)
		{
			asset_id = (String)this.getParentId().getId()+":"+id;
		}
		System.out.println("asset_id"+asset_id);
		this.setAssetId(new AssetId(asset_id));
		boolean result = false;
  		result = adi.addAsset(this,flowId);
  		if(result)
  		{
  			
  			/* Trigger ENTRY and EXIT action on that asset when it gets created except it is a root */
  			/* If added properly, trigger entry action for the asset created */
  			Loader.getLabFactory().getActionObject(new AssetId(asset_id), stationId, ActionType.ENTER, new Date(), userId).triggerAction();
  			
  			/*Trigger exit action for the asset that created */
  			if(!assetType.equals("root"))
  			Loader.getLabFactory().getActionObject(this.getAssetId(), stationId, ActionType.EXIT, new Date(), userId).triggerAction();
  		}
   	    return result;
   	
    }
	
	
	/* Update the attributes of the asset */
	public <T> boolean  updateAsset(T assetIdValue, String biopsy, String fixative)
	{
		
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		return adi.updateAsset((String)assetIdValue, biopsy, fixative);
		
	}
	
	/* Delete a specific asset */
	public <T> boolean deleteAsset(T assetIdValue)
	{		
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		return adi.deleteAsset((String)assetIdValue);
	}
	
	/* Search for requests depending on specified parameters */
	public JSONObject search(String assetId,String requestId,String patientUhid,String patientName, String doctorName)
	{
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		return adi.search(assetId, requestId, patientUhid, patientName, doctorName);
	}


}