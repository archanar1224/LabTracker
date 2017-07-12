package org.nimhans.EHealthCare.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContextEvent;

import org.nimhans.EHealthCare.ActionType;
import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.SpecialType;
import org.nimhans.EHealthCare.StatusType;
import org.nimhans.EHealthCare.database.ActionDaoImpl;
import org.nimhans.EHealthCare.database.AssetDaoImpl;
import org.nimhans.EHealthCare.database.IDGeneratorDaoImpl;
import org.nimhans.EHealthCare.database.PatientDaoImpl;
import org.nimhans.EHealthCare.database.RequestDaoImpl;
import org.nimhans.EHealthCare.database.StationDaoImpl;
import org.nimhans.EHealthCare.model.Action;
import org.nimhans.EHealthCare.model.Patient;
import org.nimhans.EHealthCare.model.Asset;
import org.nimhans.EHealthCare.model.AssetId;
import org.nimhans.EHealthCare.model.Flow;
import org.nimhans.EHealthCare.model.Request;
import org.nimhans.EHealthCare.model.Station;
import org.nimhans.EHealthCare.model.IDGenerator;
import org.nimhans.EHealthCare.model.derived.NimhansAction;
import org.nimhans.EHealthCare.model.derived.NimhansAsset;
import org.nimhans.EHealthCare.model.derived.NimhansAssetId;
import org.nimhans.EHealthCare.model.derived.NimhansFlow;
import org.nimhans.EHealthCare.model.derived.NimhansIDGenerator;
import org.nimhans.EHealthCare.model.derived.NimhansRequest;

public class NimhansFactory extends LabFactory{
	
	
	
	private Asset asset = null;
	private Flow flow = null;
	private Action action = null;
	private Patient patient = null;
	private Request request = null;
	private List<Station> stationList = new ArrayList<Station>();
private static AssetDaoImpl assetDao;
	

	private static ActionDaoImpl actionDao;
	private static IDGeneratorDaoImpl idDao;
	private static PatientDaoImpl patientDao;
	private static RequestDaoImpl requestDao;
	private static StationDaoImpl stationDao;
	
	/* initialize all DAO objects in the constructor */
	public NimhansFactory()
	{
	assetDao = new AssetDaoImpl();
	actionDao = new ActionDaoImpl();
	idDao = new IDGeneratorDaoImpl();
	patientDao = new PatientDaoImpl();
	requestDao = new RequestDaoImpl();
	stationDao = new StationDaoImpl();
	}
	public AssetDaoImpl getAssetDao() {
		return assetDao;
	}

	

	public ActionDaoImpl getActionDao() {
		return actionDao;
	}

	

	public IDGeneratorDaoImpl getIdDao() {
		return idDao;
	}

	

	public PatientDaoImpl getPatientDao() {
		return patientDao;
	}

	

	public RequestDaoImpl getRequestDao() {
		return requestDao;
	}

	
	public StationDaoImpl getStationDao() {
		return stationDao;
	}

	
	
	
	public Asset getAssetObject()
	{
		if(asset == null)
			asset = new NimhansAsset();
		return asset;
	}
	public String getAssetClassName()
	{
		return "org.nimhans.EHealthCare.model.derived.NimhansAsset";
	}
	public Asset getAssetObject(AssetId assetId, Flow flowId, int currentFlowIndex, AssetId parent, StatusType status, String reqId, String remarks, String type, String biopsy, String fixative,String specimen,SpecialType specialType)
	{
		return new NimhansAsset(assetId, flowId, currentFlowIndex, parent, status, reqId, remarks, type, biopsy, fixative, specimen, specialType);
	}
	public Station getStationObject(int stationId, String name)
    {
		return new Station(stationId,name);
    }

	public Action getActionObject()
	{
		if(action == null)
		 action = new NimhansAction();
		return action;
	}
	public Action getActionObject(int stationID, ActionType actionType, Date timeStamp,String userId)
	{
		Action a = new NimhansAction(stationID, actionType, timeStamp, userId);
		return a;
	}
	public Action getActionObject(AssetId assetID, int stationID, ActionType actionType, Date timeStamp,String userId)
	{
		Action a = new NimhansAction(assetID, stationID, actionType, timeStamp, userId);
		return a;
	}
	public Station getStationObject()
	{

		return new Station();
	}
	
	
	/* Maintain the list of station objects
	 *  
	 * instead of creating new object every time check if that is there and return the corresponding object
	 * 
	 * else create and add to list
	 *
	 */
	public Station getStationObject(int stationId)
	{
		int noOfStations = 0;
		if(stationList != null)
			noOfStations= stationList.size();
		for(int i=0;i<noOfStations;i++)
		{
			if(stationList.get(i).getStationId() == stationId)
				return stationList.get(i);
		}
		stationList.add(new Station(stationId));
		return stationList.get(noOfStations);
	}
	public Flow getFlowObject()
	{
		if(flow == null)
			flow = new NimhansFlow();
		return flow;
	}
	public Flow getFlowObject(String flow)
	{
		Flow f = new NimhansFlow(flow);
		return f;
	}
	public AssetId getAssetIdObject()
	{
		AssetId id = new NimhansAssetId();
		return id;
	}
	public AssetId getAssetIdObject(String assetId)
	{
		AssetId id = new NimhansAssetId(assetId);
		return id;
	}
	public String getAssetIdClassName()
	{
		return "org.nimhans.EHealthCare.model.derived.NimhansAssetId";
	}
	public IDGenerator getIDGeneratorObject()
	{
		IDGenerator idgen = new NimhansIDGenerator();
		return idgen;
	}
	
	public Request getRequestObject()
	{
		if(request == null)
			request = new Request();
		return request;
	}
	public Request getRequestObject(String requestID)
	{
		return new NimhansRequest(requestID);
	}
	public Patient getPatientObject()
	{
		if(patient == null)
			patient = new Patient();
		return patient;
	}
	public Patient getPatientObject(String uHID, String patientName, int age, String sex, String hospName, String neuro_no, String doctor, String deptName, String unitName, String mrdNo)
	{
		return new Patient(uHID, patientName, age, sex, hospName, neuro_no, doctor, deptName, unitName, mrdNo);
	}
}
