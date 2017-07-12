package org.nimhans.EHealthCare.model;

public abstract class Lab {
	private String labId;
	private String name;
	private String hospital;
	public Lab(){
		
	}
	public Lab(String labId, String name, String hospital) {
		super();
		this.labId = labId;
		this.name = name;
		this.hospital = hospital;
	}
	public String getLabId() {
		return labId;
	}
	public void setLabId(String labId) {
		this.labId = labId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHospital() {
		return hospital;
	}
	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
	

}
