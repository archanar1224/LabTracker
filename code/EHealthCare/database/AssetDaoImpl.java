package org.nimhans.EHealthCare.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.SpecialType;
import org.nimhans.EHealthCare.StatusType;
import org.nimhans.EHealthCare.model.Asset;
import org.nimhans.EHealthCare.model.AssetId;
import org.nimhans.EHealthCare.model.Flow;
import org.nimhans.EHealthCare.model.derived.NimhansAsset;
import org.nimhans.EHealthCare.model.derived.NimhansFlow;
import org.json.JSONArray;
import org.json.JSONObject;

/* DAO class that performs database actions related to an asset  */

public class AssetDaoImpl implements AssetDao
{
	
	
	/* Getting asset object for a given id */
	public NimhansAsset getAsset(AssetId id)
	{
		// go to DB to retrieve the asset details
		try
		{
		
	    	Statement st = Loader.getDbConnection().createStatement();
	    	String assetIdValue = (String) id.getId();
	    	ResultSet rs=st.executeQuery("select * from asset where asset_id='"+assetIdValue+"'");
	    	if(rs.next())
	    	{
	    		String asset_id = rs.getString("asset_id");
	    		AssetId assetId = new AssetId(asset_id);
	    		String flowId = rs.getString("flow_id");
	    		Flow flow = Loader.getLabFactory().getFlowObject(flowId);
	    		int currentFlowIndex = rs.getInt("current_flow_index");
	    		String parent = rs.getString("parent_id");
	    		AssetId parentId = new AssetId(parent);
	    		String status = rs.getString("status");
	    		StatusType asset_status = StatusType.valueOf(status);
	    		String reqId = rs.getString("request_id");
	    		String remarks = rs.getString("remarks");
	    		String type = rs.getString("asset_type");
	    		String biopsy = rs.getString("biopsy");
	    		String fixative = rs.getString("fixative");
	    		String specimen = rs.getString("specimen");
                String specialType = rs.getString("special_type");
	    		NimhansAsset a = (NimhansAsset) Loader.getLabFactory().getAssetObject(assetId,flow,currentFlowIndex,parentId,asset_status,reqId,remarks,type, biopsy, fixative, specimen, SpecialType.valueOf(specialType));
	    		return a;
	    	}
	    	return null;
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null;
		}
    	
		
	}
	
	
	
	/* Get all assets related to a particular root */
	public List<NimhansAsset> getAssets(String root)
	{
		List<NimhansAsset> assetList = new ArrayList<>();
		try
		{
	    	Statement st = Loader.getDbConnection().createStatement(); 	
	    	
	    	/*select the assets that has root's id in its id */
	    	ResultSet rs = st.executeQuery("select * from asset where asset_id LIKE '"+root+"%'");
	    	while(rs.next())
	    	{
	    		 String asset_id = rs.getString("asset_id");
	    		 AssetId assetId = new AssetId(asset_id);
	    		 String flow = rs.getString("flow_id");
	    		 Flow f = Loader.getLabFactory().getFlowObject(flow);
	    		 int currentFlowIndex = rs.getInt("current_flow_index");
	    		 String parent = rs.getString("parent_id");
	    		 AssetId parentId = new AssetId(parent);
	    		 String status = rs.getString("status");
	    		 StatusType asset_status = StatusType.valueOf(status);
	    		 String reqId = rs.getString("request_id");
	    		 String remarks = rs.getString("remarks");
	    		 String type = rs.getString("asset_type");
	    		 String biopsy = rs.getString("biopsy");
	    		 String fixative = rs.getString("fixative");
	    		 String specimen = rs.getString("specimen");
                 String specialType = rs.getString("special_type");
	    		 NimhansAsset a = (NimhansAsset) Loader.getLabFactory().getAssetObject(assetId,f,currentFlowIndex,parentId,asset_status,reqId,remarks,type, biopsy, fixative,specimen,SpecialType.valueOf(specialType));
	    		 assetList.add(a);
	    	}
	    	return  assetList;
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null;
		}		
	}
	
	
	/* To get status of an asset (ENTERED,EXITED or FINISHED) */
	public String getAssetStatus(AssetId assetId)
	{
		String status = null;
		try
		{
			Statement st = Loader.getDbConnection().createStatement();
			String assetIdString = assetId.getId().toString();
			String query = "select status from asset where asset_id='"+assetIdString+"'";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
			status = rs.getString(1);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return status;
			
		}
		return status;
	}
	
	
	/* Update asset status to specified type */
	public boolean updateAssetStatus(AssetId assetId, StatusType status)
	{
		try
		{
			Statement st = Loader.getDbConnection().createStatement();
			String assetStatus = status.toString();
			
			String asset_id = (String)assetId.getId();
			System.out.println("status"+assetStatus+"assetId = "+asset_id);
			String query = "update asset set status ='"+assetStatus+"' where asset_id ='"+asset_id+"'";
			int n = st.executeUpdate(query);
			if(n>0)
				return true;
			return false;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	
	
	/* Get flow id for a given asset type */
	public String getFlowId(String assetType)
	{
		String flowId = "";
		Statement st;
		try
		{
			st = Loader.getDbConnection().createStatement();
			String query = "select flow_id from flow where asset_type = '" + assetType + "';";
			ResultSet rs = st.executeQuery(query);
	  		if(rs.next())
	  		{
	  			flowId = rs.getString("flow_id");
	  		}
			return flowId;
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/* Get flow for a given flow id */
	public String getFlow(Flow flowId)
	{
		String flowValue = "";
		Statement st;
		try
		{
			st = Loader.getDbConnection().createStatement();
			System.out.println("In getflow dao" + flowId.getFlowId());
			String query = "select flow from flow where flow_id = '" + flowId.getFlowId() + "';";
			ResultSet rs = st.executeQuery(query);
	  		 if(rs.next())
	  		 {
	  			 flowValue = rs.getString("flow");
	  			 System.out.println("flow val : "+ flowValue);
	  		 }
			return flowValue;
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	
	
	/* Update the asset having the id provided */
	public boolean updateAsset(String assetIdValue, String biopsy, String fixative)
	{
		try 
		{
			Statement st = Loader.getDbConnection().createStatement();
			String query = "update asset set fixative = '"+ fixative+"', biopsy='"+biopsy+"' where asset_id ='"+assetIdValue+"';";
			int res = st.executeUpdate(query);
			if(res<=0)
			{
				return false;
			}
			else
			{
				return true;
			}

		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	
	/* method to delete an asset */
	public boolean deleteAsset(String assetIdValue)
	{
		try 
		{
			Statement st = Loader.getDbConnection().createStatement();
			String query = "delete from asset where asset_id = '"+assetIdValue+"';";
			int res = st.executeUpdate(query);
			if(res<=0)
			{
				return false;
			}
			else
			{
				return true;
			}

		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	
	/* get current flow index of an asset with given assetId */
	public int getCurrentFlowIndex(AssetId assetId)
	{
		int currentFlowIndex = -1;
		Statement st;
		try
		{
			st = Loader.getDbConnection().createStatement();
			System.out.println("In getcfi"+assetId.getId());
			String query = "select current_flow_index from asset where asset_id = '" + assetId.getId().toString() + "';";
			ResultSet rs = st.executeQuery(query);
	  		 if(rs.next())
	  		 {
	  			currentFlowIndex = rs.getInt("current_flow_index");
	  			 System.out.println("cfi val : "+ currentFlowIndex);
	  		 }
			return currentFlowIndex;
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
	}
	
	/* Update asset's current flow index */
	public boolean updateAssetFlowIndex(AssetId assetId, int newFlowIndex)
	{
		String assetIdValue = (String) assetId.getId();
		try 
		{
			System.out.println("Inside cfi dao : "+ assetIdValue);
			Statement st = Loader.getDbConnection().createStatement();
			String query = "update asset set current_flow_index = "+ newFlowIndex + " where asset_id ='"+assetIdValue+"';";
			int res = st.executeUpdate(query);
			if(res<=0)
			{
				return false;
			}
			else
			{
				return true;
			}

		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/* Get maximum assigned assetId till now for a given type. 
	 * 
	 * Tissue - returns 1,2,3..etc
	 * 
	 * Block - returns A,B,C..etc
	 * 
	 * Slide - return S1,S2,S3..etc
	 *
	 */
	public static String getCurrentAssetId(String root, String assetType)
    {
   	 try
   	 {
   		 System.out.println("Type " + assetType);
	   	 Statement st = Loader.getDbConnection().createStatement();
	   	 
	   	 /*substring_index(asset_id,:,-1) splits asset_id based on ':' and returns last split
	   	  * 
	   	  *  query retrieves the maximum values among the assigned values till now for a given root
	   	  *  
	   	  *  checking the length of the splitted string to deal with 11,S11..etc
	   	  */
	   	 
	   	 String query = "select max(substring_index(asset_id,':',-1)) from asset where asset_id like '"+root+"%' and asset_type='"+assetType+"' and length(substring_index(asset_id,':',-1))=(select max(length(substring_index(asset_id,':',-1))) from asset where asset_type='"+assetType+"' and asset_id like '"+root+"%');";
	   	ResultSet rs = st.executeQuery(query);
	   	if(rs.next())
	   	{
	   		return rs.getString(1);
	   	}
	   	else
	   	{
	   		return null;
	   	}
	   	 
	   	 
	   	 
	 }
   	 catch(Exception e)
	 {
   		 System.out.println(e);
	  	 return null;
	 }
  }
	
	
	/* To add a new asset */
	 public boolean addAsset(NimhansAsset asset,String flowId)
	 {
	   	 try
	   	 {
	   		 
		   	 PreparedStatement pst = Loader.getDbConnection().prepareStatement("insert into asset values(?,?,?,?,?,?,?,?,?,?,?,?)");
		   	 pst.setString(1, (String)asset.getAssetId().getId());
		   	 pst.setString(2,flowId);
		   	 pst.setInt(3, asset.getCurrentFlowIndex());
		   	 pst.setString(4,(String)asset.getParentId().getId());
		   	 pst.setString(5, asset.getStatus().toString());
		   	 pst.setString(6, asset.getReqId());
		   	 pst.setString(7, asset.getRemarks());
		   	 pst.setString(8, asset.getAssetType());
		   	 pst.setString(9, asset.getBiopsy());
		   	 pst.setString(10, asset.getFixative());
		   	 pst.setString(11, asset.getSpecimen());
		   	 pst.setString(12, asset.getSpecialType().toString());
		   	 int n = pst.executeUpdate();
		   	 System.out.println("last step");
		   	 if(n>0)
		   		 return true;
		   	 else
		   		 return false;
		   	 }
	   	 catch(Exception e)
	   	 {
	   		 System.out.println(e);
	   		 return false;
	   	 }
	    }
	 
	 
	 
	 
	 /* To get children of given parent asset */
	 public List<NimhansAsset> getChildren(AssetId parentId)
	    {
	        List<NimhansAsset> assetList = new ArrayList<>();
	        try
	        {
	        	System.out.println("parentId"+parentId.getId().toString()+"indao");
	            Statement st = Loader.getDbConnection().createStatement();     
	            String parentIdValue = (String) parentId.getId();
	            
	            /*selects all assets whose parentid is equal to given parent_id */
	            ResultSet rs = st.executeQuery("select * from asset where parent_id = '"+parentIdValue+"'");
	            while(rs.next())
	            {
	                 String asset_id = rs.getString("asset_id");
	                 AssetId assetId = new AssetId(asset_id);
	                 String flow = rs.getString("flow_id");
	                 Flow f = Loader.getLabFactory().getFlowObject(flow);
	                 int currentFlowIndex = rs.getInt("current_flow_index");
	                 String parent = rs.getString("parent_id");
	                 AssetId assetParentId = new AssetId(parent);
	                 String statusValue = rs.getString("status");
	                 StatusType status = StatusType.valueOf(statusValue);
	                 String reqId = rs.getString("request_id");
	                 String remarks = rs.getString("remarks");
	                 String type = rs.getString("asset_type");
	                 String biopsy = rs.getString("biopsy");
	                 String fixative = rs.getString("fixative");
	                 String specimen = rs.getString("specimen");
	                 String specialType = rs.getString("special_type");
	                 NimhansAsset a = (NimhansAsset) Loader.getLabFactory().getAssetObject(assetId,f,currentFlowIndex,assetParentId,status,reqId,remarks,type, biopsy, fixative,specimen,SpecialType.valueOf(specialType));
	                 System.out.println("asset_id"+asset_id+"indao");
	                 /* iteratively add child assets */
	                 assetList.add(a);
	            }
	            return  assetList;
	        }
	        catch(Exception e)
	        {
	            System.out.println(e);
	            return null;
	        }        
	    }
	 
	 
	 /* Updates specialType of an asset */
	 public boolean updateAssetSpecialType(AssetId assetId, SpecialType specialType)
	    {
	        String assetIdValue = (String) assetId.getId();
	        try
	        {
	            System.out.println("Inside specialtype dao : "+ assetIdValue);
	            Statement st = Loader.getDbConnection().createStatement();
	            System.out.println(specialType.toString());
	            String query = "update asset set special_type ='"+ specialType.toString() + "' where asset_id ='"+assetIdValue+"';";
	            int res = st.executeUpdate(query);
	            if(res<=0)
	            {
	                return false;
	            }
	            else
	            {
	                return true;
	            }
	            
	        }
	        catch(Exception e)
	        {
	        	System.out.println(e);
	        	return false;
	        }
	    }
	 
	 
	 
	 /*retrives the current station of an asset */
	 public int getCurrentStation(AssetId assetId)
	 {
		 try
		 {
			 Statement st = Loader.getDbConnection().createStatement();
			 
			 /* Retrieves flow and cfi */
			 String query = "select a.current_flow_index,flow from asset a,flow f where a.asset_id = '"+assetId.getId().toString()+"' and a.flow_id=f.flow_id";
			 ResultSet rs = st.executeQuery(query);
			 if(rs.next())
			 {
				 
			 /* split the flow and take corresponding station id depending on cfi */
			 int stationId =Integer.parseInt(rs.getString(2).split(":")[rs.getInt(1)]);
			 return stationId;
			 }
			 else
				 return -1;
		 }
		 catch(Exception e)
	        {
	        	System.out.println(e);
	        	return -1;
	        }
		 
	 }
	 
	 
	 /* Search based on assetId,requestId,patient Uhid,patient name, doctor name
	  * 
	  * case insensitive search, will return those requests which has the substrings of the search values 
	  */
	 public JSONObject search(String assetId,String requestId,String patientUhid,String patientName, String doctorName)
	 {
		 
		 String param[] = new String[5];
		 
		 /*intializing all parameters to %%. So while retrieving it retrives all requests */
		 for(int i=0;i<5;i++)
			 param[i]="%%";
		 
		 /*Assigning the values if user provides any */
		 if(!assetId.equals(""))
		 {
			 param[0]=assetId;
		 }
		 if(!requestId.equals(""))
		 {
			 param[1]=requestId;
		 }
		 if(!patientUhid.equals(""))
		 {
			 param[2]=patientUhid;
		 }
		 if(!patientName.equals(""))
		 {
			 param[3]="%"+patientName+"%";
		 }
		 if(!doctorName.equals(""))
		 {
			 param[4]="%"+doctorName+"%";
		 }
		 for(int i=0;i<5;i++)
			 System.out.println(param[i]+" ");
		 
		 
		 /* Retrieving the request details depending on search parameters */
		 String query = "select asset_id,request_id,uhid,name,doctor from asset,request,patient where asset.request_id=request.reqid and request.patient_uhid=patient.uhid and asset_id like '"+param[0]+"' and request_id like '"+param[1]+"' and uhid like '"+param[2]+"' and name like '"+param[3]+"' and doctor like '"+param[4]+"' and asset_type='root'";
		 System.out.println("query "+query);
		 try
		 {
			 Statement st = Loader.getDbConnection().createStatement();
			 ResultSet rs = st.executeQuery(query);
			 
			 /* Creation of a JSON array */
			 JSONArray jArray = new JSONArray();
			 
			 /* Insert all request details as JSON objects in the JSON array */
			 while(rs.next())
			 {
				 System.out.println("inside while");
				 JSONObject jObject = new JSONObject();
				 jObject.put("assetId",rs.getString(1));
				 jObject.put("requestId",rs.getString(2));
				 jObject.put("patientUHID",rs.getString(3));
				 jObject.put("patientName",rs.getString(4));
				 jObject.put("doctorName",rs.getString(5));
				 jArray.put(jObject);
			 }
			 System.out.println("len "+jArray.length());
			 if(jArray.length()>0)
			 {
				 JSONObject result = new JSONObject();
				 result.put("search", jArray);
				 return result;
			 }
			 else
			 {
				 return null;
			 }
		 }
		 catch(Exception e)
		 {
			 return null;
		 }
	 
	 
	 
	 }


	
}