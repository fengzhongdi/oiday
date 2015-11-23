package oiday.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

import snap.sono.demo.data.SnapList;
import snap.sono.demo.data.SnapString;
import snap.sono.demo.impl.GeneralImpl;
import snap.sono.demo.server.GeneralServer;

@XmlRootElement
@Path("/oiday/")
public class RestEndPoint {
	GeneralServer server = new GeneralImpl();
	private Logger logger = Logger.getLogger("RestEndPoint");
	
	@GET
    @Path("/hello")
	@Produces("application/json")
    public String sayHello() throws Exception{
		logger.info("reach hello");
		SnapString snapString = new SnapString("hello");
		logger.info("this is: "+snapString.getName());
		return snapString;
    }
	
}
