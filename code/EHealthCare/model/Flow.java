package org.nimhans.EHealthCare.model;

public class Flow 
{
	private String flowId;
	private String flow;
	public Flow()
	{
		
	}
	public Flow(String flowId) {
		super();
		this.flowId = flowId;
	}
	public String getFlowId() {
		return flowId;
	}
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	public String getFlow() {
		return flow;
	}
	public void setFlow(String flow) {
		this.flow = flow;
	}
	/*protected Flow getAssetFlow(String type)
	{
		return this;
	}*/

}
