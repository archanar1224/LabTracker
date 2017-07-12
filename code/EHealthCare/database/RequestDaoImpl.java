package org.nimhans.EHealthCare.database;

import java.sql.ResultSet;
import java.sql.Statement;

import org.nimhans.EHealthCare.Loader;

import org.nimhans.EHealthCare.model.Patient;
public class RequestDaoImpl implements RequestDao 
{
	
	/* Get patient id for a given request */
	public String getPatientID(String requestID)
	{
		String patientID = "";
		try
		{
			/*
			 * "request" is an External Table containing the info
			 * "labrequest" table is our mapping of external reqID to the base/root (Eg: npbase)
			 */
			System.out.println("reqid="+requestID);
			Statement st = Loader.getDbConnection().createStatement();
			String query = "select patient_uhid from request where reqid='" + requestID + "';";
			ResultSet rs = st.executeQuery(query);

			if(rs.next())
	    	{
				patientID = rs.getString("patient_uhid");  	
				System.out.println("dao patientID="+patientID);
	  	    }		
		}
		catch(Exception e)
		{
			System.out.println(e);
			
			
		}
		return patientID;
	}
	
	
	
	/* Get rootId for a given request */
	public String getNpBase(String reqId)
	{
		try
		{
		Statement st = Loader.getDbConnection().createStatement();
		String query = "select asset_id from asset where request_id='"+reqId+"'";
		ResultSet rs = st.executeQuery(query);
		if(rs.next())
		{
			String asset_id = rs.getString(1);
			/* To take only rootId */
			String npBase = asset_id.split(":")[0];
			return npBase;
		}
		return null;
		}
		catch(Exception e)
		{
			return null;
		}
		
	}
	
	
	/* To find the number of requests received in a given time period */
	public int getTotalRequestsReceived(String startDate,String endDate, String rootFlow)
	{
		String stations[] = rootFlow.split(":");
		
		/* Get start station id of root */
		int startStationId = Integer.parseInt(stations[0]);
		System.out.println("start"+startStationId);
		try
		{
		Statement st = Loader.getDbConnection().createStatement();
		
		/* Selects number of root assets that are entered receiving station (which in turn represents no of requests) */
		String query = "select count(*) from (select count(*) from action where stationid="+startStationId+" and DATE(timestamp)>='"+startDate+"' and DATE(timestamp)<='"+endDate+"' group by assetid) a";
		ResultSet rs = st.executeQuery(query);
		if(rs.next())
		{
			return rs.getInt(1);
		}
		else
			return 0;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 0;
		}
		
	}
	
	
	/* Number of requests completed in a given time period */
	public int getTotalRequestsCompleted(String startDate,String endDate,String rootFlow)
	{
		String stations[] = rootFlow.split(":");
		/* get last station of root */
		int lastStationId = Integer.parseInt(stations[stations.length-1]);
		System.out.println("last station id"+lastStationId);
		try
		{
		Statement st = Loader.getDbConnection().createStatement();
		
		/* Find number of roots that are exited the last station of their flow */
		String query = "select count(*) from (select count(*) from action where stationid="+lastStationId+" and DATE(timestamp)>='"+startDate+"' and DATE(timestamp)<='"+endDate+"' and actiontype='EXIT' group by assetid) a";
		ResultSet rs = st.executeQuery(query);
		if(rs.next())
		{
			return rs.getInt(1);
		}
		else
			return 0;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 0;
		}
		
	}
	
	
	
	

}
