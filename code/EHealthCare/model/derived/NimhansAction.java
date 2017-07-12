package org.nimhans.EHealthCare.model.derived;

import java.util.Date;

import org.nimhans.EHealthCare.ActionType;
import org.nimhans.EHealthCare.model.Action;
import org.nimhans.EHealthCare.model.AssetId;

public class NimhansAction extends Action{
	
	public NimhansAction()
	{
		
	}
	public NimhansAction(AssetId assetID, int stationID, ActionType actionType, Date timeStamp,String userId)
	{
		super(assetID, stationID, actionType, timeStamp, userId);
	}
	public NimhansAction(int stationID, ActionType actionType, Date timeStamp,String userId)
	{
		super(stationID, actionType, timeStamp, userId);
		
	}
	public boolean triggerAction()
	{
		return super.triggerAction();
	}

}
