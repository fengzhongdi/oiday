package oiday.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

import com.google.gson.Gson;

import oiday.data.DataProperty;
import oiday.data.DataPropertyMap;
import oiday.data.PM25Obj;
import oiday.impl.GeneralServerImp;
import oiday.impl.QiniuImps;
import oiday.server.GeneralServer;


@XmlRootElement
@Path("/oiday/")
public class RestEndPoint {
	GeneralServer server = new GeneralServerImp();
	private Logger logger = Logger.getLogger("RestEndPoint");
	static String properties = null;
	static long timeStamp = -1l;
	Gson gson = new Gson();
	
	@GET
    @Path("/hello")
	@Produces("application/json")
    public String sayHello() throws Exception{
		logger.info("reach hello");
		return "hellos";
    }
	
	@GET
    @Path("/getDataProperties")
	@Produces("application/json")
    public String getDataProperties() throws Exception{
		if(properties == null || (System.currentTimeMillis() / 1000L) - timeStamp > 300){
			properties = gson.toJson(server.getDataProperties());
			timeStamp = System.currentTimeMillis() / 1000L;
		}
		return properties;
    }
	
	@GET
    @Path("/getQiniuToken")
	@Produces("application/json")
    public String getQiniuToken() throws Exception{
		return QiniuImps.getUpToken0();
    }
	
	@GET
    @Path("/getpm25info")
	@Produces("application/json")
    public String getPM25Info(@QueryParam("city") String city) throws Exception{
		return server.getPM25Info(city).getPM25Hash();
    }
	
}
