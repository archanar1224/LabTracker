package org.nimhans.EHealthCare.model;

import java.util.Date;
import java.util.List;

import org.nimhans.EHealthCare.ActionType;
import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.database.ActionDaoImpl;
import org.nimhans.EHealthCare.model.derived.NimhansAsset;
import org.nimhans.EHealthCare.model.derived.NimhansIDGenerator;

//add abstract later
public class Action {
	
	private AssetId assetID;
	private int stationID;
	private ActionType actionType; // 1:entry, 2:exit
	private Date timeStamp;
	private String userId; // who performed the action
	
	public Action()
	{
		
	}
	public Action(AssetId assetID, int stationID, ActionType actionType, Date timeStamp,String userId) {
		super();
		this.assetID = assetID;
		this.stationID = stationID;
		this.actionType = actionType;
		this.timeStamp = timeStamp;
		this.userId = userId;
	}
	public Action(int stationID, ActionType actionType, Date timeStamp,String userId) {
		super();
		this.stationID = stationID;
		this.actionType = actionType;
		this.timeStamp = timeStamp;
		this.userId=userId;
	}
	public AssetId getAssetID() {
		return assetID;
	}
	public void setAssetID(AssetId assetID) {
		this.assetID = assetID;
	}
	public int getStationID() {
		return stationID;
	}
	public void setStationID(int stationID) {
		this.stationID = stationID;
	}
	public ActionType getActionType() {
		return actionType;
	}
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	protected int actionTriggered()
	{
		return 1;
	}

	public String getUserId() {
		return userId;
	}
	public void setUser(String userId) {
		this.userId = userId;
	}
	
	
	/* Records an action in the database */
	public boolean triggerAction()
	{
		System.out.println("inside trigger action");
		ActionDaoImpl actionDao = Loader.getLabFactory().getActionDao();
		return actionDao.addAction(this.getAssetID(), this.getStationID(), this.getActionType(), this.getUserId(), this.getTimeStamp());
		
		
	}
	

}

