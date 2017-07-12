package org.nimhans.EHealthCare.database;

import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.SpecialType;
import org.nimhans.EHealthCare.StatusType;
import org.nimhans.EHealthCare.model.AssetId;
import org.nimhans.EHealthCare.model.Flow;
import org.nimhans.EHealthCare.model.derived.NimhansAsset;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class StationDaoImpl implements StationDao 
{
	
	
	/* Returns the assets whose next station is given stationId */
	public List<NimhansAsset> getPendingTasks(int stationId)
    {
		List<NimhansAsset> assetList = new ArrayList<>();
		try
   	 	{
			Statement st = Loader.getDbConnection().createStatement();
			
			
			String query = "select * from asset,flow where substring_index(substring_index(flow,':',current_flow_index+2),':',-1) ="+stationId+" and asset.flow_id = flow.flow_id and status = 'EXITED' and substring_index(substring_index(flow,':',current_flow_index+1),':',-1)!="+stationId;
		   	ResultSet rs = st.executeQuery(query);
		   	while(rs.next())
		   	{
		   		System.out.println("inside while");
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
                if(specialType!=null)
		        {
                	NimhansAsset a = (NimhansAsset) Loader.getLabFactory().getAssetObject(assetId,flow,currentFlowIndex,parentId,asset_status,reqId,remarks,type, biopsy, fixative,specimen,SpecialType.valueOf(specialType));
		        assetList.add(a);
		        }
		   	}
		   	System.out.println("size staiondao "+ assetList.size());
		   	return  assetList;
   	 	}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
    }
	
	
	/* Get assets who completed the work at a given station */
	public List<NimhansAsset> getCompletedTasks(int stationId)
    {
		List<NimhansAsset> assetList = new ArrayList<>();
		try
   	 	{
			Statement st = Loader.getDbConnection().createStatement();
			String stationIdString = Integer.toString(stationId);
			System.out.println("Id " + stationId + " "+ stationIdString);
			String query = "select * from asset,flow where substring_index(substring_index(flow,':',current_flow_index+1),':',-1) ='" + stationIdString +"' and asset.flow_id = flow.flow_id and status = 'EXITED'";
		   	ResultSet rs = st.executeQuery(query);
		   	while(rs.next())
		   	{
		   		System.out.println("inside while");
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
		        assetList.add(a);  
		   	}
		   	System.out.println("size staiondao "+ assetList.size());
		   	return  assetList;
   	 	}
		catch(Exception e)
		{
			System.out.println(e);
			return null;
		}
    }
	
	/* Returns the stationName for a given stationId */
	public String getStationName(int stationId)
	{
		try
		{
		Statement st = Loader.getDbConnection().createStatement();
		String query = "select stationname from station where stationid='"+stationId+"'";
		ResultSet rs = st.executeQuery(query);
		if(rs.next())
		{
			return rs.getString(1);
		}
		return null;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	
	/* Returns no of assets that entered given station but not exited */
	public Map<String,Integer> getAssetCount()
	{
		LinkedHashMap<String,Integer> stationAssetMap = new LinkedHashMap<String,Integer>(); 
		try
		{
			Statement st = Loader.getDbConnection().createStatement();
			
			/* substring_index(substring_index(flow.flow,':',asset.current_flow_index+1),':',-1) = station.stationid
			* Above part selects the assets who are at given station 
			*/
			
			
			String query = "select stationid,stationname,count(asset_id) from flow INNER JOIN asset ON asset.flow_id=flow.flow_id and status='ENTERED' RIGHT OUTER JOIN station ON substring_index(substring_index(flow.flow,':',asset.current_flow_index+1),':',-1) = station.stationid group by stationname,stationid order by station.stationid";
			ResultSet rs = st.executeQuery(query);
			/* Add all station details to a Map */
			while(rs.next())
			{
				String stationName = rs.getString(2);
				int count = rs.getInt(3);
				stationAssetMap.put(stationName,count);
				System.out.println(" "+stationName+" "+count);
				
			}
			return stationAssetMap;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/* Get all transactions in a given time period
	 * 
	 * transaction may have ENTRY, EXIT or both in given period
	 * 
	 */
	public JSONObject getTransactions(int stationId,String startDate, String endDate)
	{
		System.out.println("start"+startDate+"end"+endDate);
		try
		{
			Statement st = Loader.getDbConnection().createStatement();
			//a5,a6 - to find earliest entry time     a4,a3-to find latest exit time   a1,a2- to get entry and exit together  a7,result-for special type
			//to get the details of assets that entered and exited in given timestamp range
			
			
			
			/* 
			 * 
			 * a4 - action table
			 * a5 - action table
			 * 
			 * a1 - set of ENTER actions in a given time period
			 * 
			 * a2 - set of exit actions corresponding to that station
			 * 
			 * LEFT OUTER JOIN(a1,a2) so that assetId and stationId of them are equal (LEFT OUTER because it should give null if there is no EXIT action
			 * 
			 * a8 - action table
			 * a10 - action table
			 * 
			 * a9 - set of ENTER actions before the start date 
			 * 
			 * a11 - set of EXIT actions within given time period
			 * 
			 * INNER JOIN(a9,a11) so that assetId and stationId of them are equal
			 * 
			 * 
			 * UNION(LEFT OUTER JOIN(a1,a2) and INNER JOIN(a9,a11) - all transactions
			 * 
			 *
			 */
			String query = "select t.assetid,t.entry_time,t.exit_time,u.username,a.special_type from asset a,user u,"
					+"(select a1.assetid assetid,a1.timestamp entry_time,a2.timestamp exit_time,a1.userid userid from "
					+"(select * from action a5 where a5.actiontype='ENTER' and DATE(a5.timestamp)>='"+startDate+"' and DATE(a5.timestamp)<='"+endDate+"' and a5.stationid="+stationId+") a1 "
					+"LEFT OUTER JOIN (select * from action a4 where actiontype='EXIT' and a4.stationid="+stationId+") a2 "
					+"on a1.assetid=a2.assetid and a1.stationid=a2.stationid and a1.stationid="+stationId+" and a1.timestamp<=a2.timestamp "
					+"UNION "
					+"select a9.assetid assetid,a9.timestamp entry_time,a11.timestamp exit_time,a11.userid userid from "
					+"(select * from action a8 where a8.actiontype='ENTER' and a8.timestamp<'"+startDate+"' and a8.stationid="+stationId+") a9 "
					+"INNER JOIN (select * from action a10 where a10.actiontype='EXIT' and DATE(a10.timestamp)>='"+startDate+"' and DATE(a10.timestamp)<='"+endDate+"'and a10.stationid="+stationId+") a11 "
					+"on a9.assetid=a11.assetid and a11.stationid=a9.stationid and a9.stationid="+stationId+" and a9.timestamp<=a11.timestamp) t "
					+"where a.asset_id=t.assetid and t.userid=u.userid";
			
			
			ResultSet rs = st.executeQuery(query);
			JSONArray jarray = new JSONArray();
			while(rs.next())
			{
				
				JSONObject jObject = new JSONObject();
				jObject.put("assetId",rs.getString(1));
				jObject.put("specialType",rs.getString(5));
				jObject.put("technicianName",rs.getString(4));
				String enterTime = rs.getString(2);
				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(enterTime);
				String newEnterTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
				jObject.put("enterTime",newEnterTime);
				String exitTime = rs.getString(3);
				if(exitTime == null)
					jObject.put("exitTime","not exited");
				else
				{
					date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(exitTime);
					String newExitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
					jObject.put("exitTime",newExitTime);
					
				}
				jarray.put(jObject);
				
			}
			JSONObject result;
			
			if(jarray.length()>0)
			{
				result = new JSONObject();
				result.put("transactions",jarray);
				return result;
			}
			else
				return null;
			
			
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
