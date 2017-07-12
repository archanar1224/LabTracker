package org.nimhans.EHealthCare.database;

import java.util.List;

import org.nimhans.EHealthCare.model.Asset;

public interface StationDao 
{
	public List<? extends Asset> getPendingTasks(int stationId);
}
