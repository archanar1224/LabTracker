package org.nimhans.EHealthCare.database;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import org.nimhans.EHealthCare.Loader;

public class IDGeneratorDaoImpl implements IDGeneratorDao
{
	public String getCurrentRoot()
	{
		String currentRoot= "";
		try
		{
			Statement st = Loader.getDbConnection().createStatement();
			String query = "select current_npbase from npbase";
			ResultSet rs = st.executeQuery(query);
			
			/* If it is not first request then rs will contain some data */
			if(rs!= null&&rs.next())
	    	{
				currentRoot = rs.getString("current_npbase");
	    	}
			
			/*If first request rs willl be null */
			else
			{
				
				Calendar now=Calendar.getInstance();
				
				/* To get last 2 digits of current year */
				String currentYear = String.valueOf(now.get(Calendar.YEAR)).substring(2, 4);
				
				/* insert 0/current year and return the same */
				String insert_query = "insert into npbase values('0/"+currentYear+"')";
				
				int n = st.executeUpdate(insert_query);
				return "0/"+currentYear;
			}
		}
		catch(Exception e)
		{
			System.out.println(e);	
		}
		return currentRoot;
	}
	/*
	 * Checks if the given root is already used in the database
	 */
	
	public Boolean isInDatabase(String rootID)
	{
		Boolean flag = true;
		try
		{
			Statement st = Loader.getDbConnection().createStatement();
			String query = "select asset_id from asset where asset_id like '" + rootID + "%';";
			ResultSet rs = st.executeQuery(query);
			if (!rs.next()) 
	    	{
				//Empty :  NpBase Not in Database - False
				flag = false;
				System.out.println("not in db");
				//System.out.println("in dao" + rootID);
	    	}
			
		}
		catch(Exception e)
		{
			System.out.println(e);
			
			
		}
		return flag;
	}
	
	
	
	/* Updates npbase table with current root value */
	public void updateCurrentRoot(String root)
	{
		try
		{
			String currentRoot = getCurrentRoot();
			Statement st = Loader.getDbConnection().createStatement();
			String query = "update npbase set current_npbase='" + root + "' where current_npbase='" +  currentRoot +"';";
		    st.executeUpdate(query);
			
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	/*
	 * Adds the mapping of reqID and rootID to the database
	 */
	public void addLabRequest(String rootID, String reqId)
	{
		try
		{
			System.out.println(rootID+ "  " + reqId);
			Statement st = Loader.getDbConnection().createStatement();
			String query = "insert into labrequest values('" + reqId + "','" + rootID +  "');";
			
		    st.executeUpdate(query);
	
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

}
