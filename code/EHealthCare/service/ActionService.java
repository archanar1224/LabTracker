package org.nimhans.EHealthCare.service;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


@Path("/actionservice")
public class ActionService {
	
	@GET
	@Path("/aaa")
	@Produces(MediaType.APPLICATION_JSON)
	public String newAction()
	{
		return null;
	}
	


}
