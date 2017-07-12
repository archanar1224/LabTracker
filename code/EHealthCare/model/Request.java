package org.nimhans.EHealthCare.model;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.QueryParam;

import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.database.AssetDaoImpl;
import org.nimhans.EHealthCare.database.PatientDaoImpl;
import org.nimhans.EHealthCare.database.RequestDaoImpl;

public class Request {
	private String reqID;
	private String rootID;
	private Date timestamp;
	public Request()
	{
		
	}
	public Request(String reqID) 
	{
		super();
		this.reqID = reqID;
		
	}
	public Request(String reqID, String rootID, Date timestamp)
	{
		super();
		this.reqID = reqID;
		this.rootID = rootID;
		this.timestamp=timestamp;
	}
	public String getReqID() {
		return reqID;
	}
	public void setReqID(String reqID) {
		this.reqID = reqID;
	}
	public String getRootID() {
		return rootID;
	}
	public void setRootID(String rootID) {
		this.rootID = rootID;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}	
	public int noOfAssets(String reqid){ //number of assets for a given request id
		// TODO
		return 1;
	}
	public int totalRequests()   //no of requests came today
	{
		//TODO
		return 1;
	}

	/* Get patient details for a given requestId */
	public Patient getPatientDetails()
	{
		RequestDaoImpl rdi = Loader.getLabFactory().getRequestDao();
		String patientID = rdi.getPatientID(this.reqID);
		System.out.println("patientId="+patientID);
		PatientDaoImpl pdi = Loader.getLabFactory().getPatientDao();
		Patient patient = pdi.getPatientDetails(patientID);
		return patient;
	}
	public String getNpBase()
	{
		RequestDaoImpl rdi = Loader.getLabFactory().getRequestDao();
		String npBase = rdi.getNpBase(this.reqID);
		
		return npBase;
	}
	public String getRequestStatus()
	{
		System.out.println("in parent get status method");
		return null;
	}
	
	/* Number of requests received in a given time period */
	public int totalRequestsReceived(String startDate,String endDate)
	{
		RequestDaoImpl rdi = Loader.getLabFactory().getRequestDao();
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		String flowId = adi.getFlowId("root");
		String rootFlow = adi.getFlow(new Flow(flowId));
		int noOfRequests = rdi.getTotalRequestsReceived(startDate,endDate,rootFlow);
		return noOfRequests;
	}
	
	/* Number of requests received in a given time period */
	public int totalRequestsCompleted(String startDate,String endDate)
	{
		RequestDaoImpl rdi = Loader.getLabFactory().getRequestDao();
		AssetDaoImpl adi = Loader.getLabFactory().getAssetDao();
		String flowId = adi.getFlowId("root");
		String rootFlow = adi.getFlow(new Flow(flowId));
		int noOfRequests = rdi.getTotalRequestsCompleted(startDate,endDate,rootFlow);
		return noOfRequests;
	}
	
	
}
