package org.nimhans.EHealthCare.factory;

import java.util.Date;

import org.nimhans.EHealthCare.ActionType;
import org.nimhans.EHealthCare.SpecialType;
import org.nimhans.EHealthCare.StatusType;
import org.nimhans.EHealthCare.database.*;
import org.nimhans.EHealthCare.model.Action;
import org.nimhans.EHealthCare.model.Asset;
import org.nimhans.EHealthCare.model.AssetId;
import org.nimhans.EHealthCare.model.Flow;
import org.nimhans.EHealthCare.model.Patient;
import org.nimhans.EHealthCare.model.IDGenerator;
import org.nimhans.EHealthCare.model.Request;
import org.nimhans.EHealthCare.model.Station;


public abstract class LabFactory {
	
	private static LabFactory appFactory = null;
	
	public abstract Asset getAssetObject();
	public abstract Asset getAssetObject(AssetId assetId, Flow flow, int currentFlowIndex, AssetId parent, StatusType status, String reqId, String remarks, String type, String biopsy, String fixative,String specimen,SpecialType specialtype);
	public abstract Action getActionObject(int stationID, ActionType actionType, Date timeStamp,String userId);
	public abstract Action getActionObject(AssetId assetID, int stationID, ActionType actionType, Date timeStamp,String userId);
	public abstract Flow getFlowObject();
	public abstract Action getActionObject();
	public abstract AssetId getAssetIdObject();
	public abstract AssetId getAssetIdObject(String assetId);
	public abstract String getAssetIdClassName();
	public abstract String getAssetClassName();
	public abstract IDGenerator getIDGeneratorObject();
	public abstract Flow getFlowObject(String flow);
	public abstract Patient getPatientObject();
	public abstract Patient getPatientObject(String uHID, String patientName, int age, String sex, String hospName, String neuro_no, String doctor, String deptName, String unitName, String mrdNo);
	public abstract Request getRequestObject();
	public abstract Request getRequestObject(String requestId);
	public abstract Station getStationObject(int stationId);
	public abstract Station getStationObject();
	public abstract Station getStationObject(int stationId,String name);
	public abstract AssetDaoImpl getAssetDao();
	public abstract ActionDaoImpl getActionDao();
	public abstract IDGeneratorDaoImpl getIdDao();
	public abstract RequestDaoImpl getRequestDao();
	public abstract StationDaoImpl getStationDao();
	public abstract PatientDaoImpl getPatientDao();
	

}
