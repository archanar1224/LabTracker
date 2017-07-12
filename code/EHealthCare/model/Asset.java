package org.nimhans.EHealthCare.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.StatusType;
import org.nimhans.EHealthCare.database.AssetDaoImpl;
import org.json.JSONObject;

public abstract class Asset {
	
	private AssetId assetId;
	private Flow flowId;
	private int currentFlowIndex;
	private AssetId parentId;
	private List<AssetId> childrenId;
	private StatusType status; // not started, ongoing or completed
	private String reqId;
	private String remarks;
	//int noOfChildrenCompleted;
	//do we need to store type????(ex:block,tissue..etc)
	public Asset()
	{
		childrenId = new ArrayList<AssetId>();
	}
	
	public Asset(AssetId assetId, Flow flowId, int currentFlowIndex, AssetId parentId, StatusType status, String reqId, String remarks) {
		super();
		this.assetId = assetId;
		this.flowId = flowId;
		this.currentFlowIndex = currentFlowIndex;
		this.parentId = parentId;
		this.childrenId = new ArrayList<AssetId>();
		this.status = status;
		this.reqId = reqId;
		this.remarks = remarks;
	}
	
	public AssetId getAssetId() {
		return assetId;
	}

	public void setAssetId(AssetId assetId) {
		this.assetId = assetId;
	}

	public Flow getFlowId() {
		return flowId;
	}

	public void setFlowId(Flow flowId) {
		this.flowId = flowId;
	}

	public int getCurrentFlowIndex() {
		return currentFlowIndex;
	}

	public void setCurrentFlowIndex(int currentFlowIndex) {
		this.currentFlowIndex = currentFlowIndex;
	}

	public AssetId getParentId() {
		return parentId;
	}

	public void setParentId(AssetId parentId) {
		this.parentId = parentId;
	}

	public List<AssetId> getChildrenIds() {
		return childrenId;
	}

	public void setChildren(List<AssetId> childrenId) {
		this.childrenId = childrenId;
	}

	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}
	
	
	

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int updateAssetStatus()
	{
		//check the status of children if all are completed then check whether its flow got completed
		//if both completed then update the status of asset as completed and call update status of parent
		return 1;
	}
	
	
	public String getAssetStatus(String assetId){
		return null;
	}
	public String getAssetHistory(String id){
		return null;
	}
	
	
	public abstract boolean addAsset(int stationId,String userId);
	public abstract JSONObject search(String assetId,String requestId,String patientUhid,String patientName, String doctorName);

	public abstract <T> boolean updateAsset(T assetId, String biopsy, String fixative);
	public abstract <T> boolean deleteAsset(T assetIdValue);

}