package oiday.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class RawPM25Obj {
	private String city, station, pm25, date, time;
	public RawPM25Obj(){
		
	}
	public RawPM25Obj(String city, String station, String pm25, String date, String time){
		this.city = city;
		this.station = station;
		this.pm25 = pm25;
		this.date = date;
		this.time = time;
	}
	@XmlElement(nillable = false, required = false)
	public String getCity(){
		return this.city;
	}
	@XmlElement(nillable = false, required = false)
	public String getStation(){
		return this.station;
	}
	@XmlElement(nillable = false, required = false)
	public String getPM25(){
		return this.pm25;
	}
	@XmlElement(nillable = false, required = false)
	public String getDate(){
		return this.date;
	}
	@XmlElement(nillable = false, required = false)
	public String getTime(){
		return this.time;
	}
}
