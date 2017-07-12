package org.nimhans.EHealthCare.database;

import java.util.Date;

import org.nimhans.EHealthCare.ActionType;
import org.nimhans.EHealthCare.model.AssetId;

public interface ActionDao 
{
	public boolean addAction(AssetId assetId, int stationId, ActionType actionType, String userId, Date timestamp);
}
