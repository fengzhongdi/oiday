package oiday.data;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.Gson;

@XmlRootElement
public class PM25Obj {
	private String city;
	private long time;
	private HashMap<String, String> pm25Hash = new HashMap<String, String>();

	public PM25Obj() {

	}

	public PM25Obj(String city, long time, HashMap<String, String> pm25Hash) {
		this.city = city;
		this.time = time;
		this.pm25Hash = pm25Hash;
	}
	
	public void addPM25Info(String station, String pm25){
		this.pm25Hash.put(station, pm25);
	}
	
	@XmlElement(nillable = false, required = false)
	public String getCity(){
		return this.city;
	}
	
	@XmlElement(nillable = false, required = false)
	public long getTime(){
		return this.time;
	}
	
	@XmlElement(nillable = false, required = false)
	public String getPM25Hash(){
		Gson gson  = new Gson();
		return gson.toJson(this.pm25Hash).replace("\\", "");
	}
	
}
