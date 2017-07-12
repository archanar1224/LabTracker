package org.nimhans.EHealthCare.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.nimhans.EHealthCare.ActionType;
import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.model.AssetId;


/* DAO class that performs database actions related to action table  */

public class ActionDaoImpl implements ActionDao
{
	
	/* To add an action to action table  */
	public boolean addAction(AssetId assetId, int stationId, ActionType actionType, String userId, Date timestamp)
	{
		try
		{	
			Statement st = Loader.getDbConnection().createStatement();
			String assetIdValue = (String) assetId.getId();
			
			
			/* Only one entry and exit action for a combination of asset and station. If action is already there need to check and update accordingly  */
			
			/* Check if it has already entered. If yes no need to register it again */
			if(actionType.toString().equals("ENTER"))
			{
				String query = "select * from action where assetid='"+assetIdValue+"' and stationid="+stationId+" and actiontype='ENTER'";
				ResultSet rs = st.executeQuery(query);
				if(rs.next())
					return true;
			}
			
			/* Check if it has already exited. If yes delete previous action and insert current action */
			if(actionType.toString().equals("EXIT"))
			{
				String query = "delete from action where assetid='"+assetIdValue+"' and stationid="+stationId+" and actiontype='EXIT'";
				int res = st.executeUpdate(query);
				if(res<0)
					return false;
				
			}
			
			/* Insert action */
			Timestamp sqlTimestamp = new Timestamp(timestamp.getTime());
			String actionTypeValue = actionType.toString();
			String sql = "INSERT INTO action " +
	                     "VALUES (NULL, '"+ assetIdValue + "'," + stationId + ",'" + actionTypeValue +  "','"+ sqlTimestamp  + "', '" + userId + "');" ; 
			int result = st.executeUpdate(sql);
			if(result<=0)
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
			e.printStackTrace();
			return false;
		}
	}
}
