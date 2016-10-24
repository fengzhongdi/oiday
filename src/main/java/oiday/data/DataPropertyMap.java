package oiday.data;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataPropertyMap {
	private Map<String, DataProperty> dataProperyMap;
	
	private Map<String, String> extraSettings;

	public DataPropertyMap(){
		
	}
	
	public DataPropertyMap(Map<String, DataProperty> dataProperyMap, Map<String, String> extraMap) {
		this.dataProperyMap = dataProperyMap;
		this.extraSettings = extraMap;
	}

	@XmlElement(nillable = false, required = false)
	public Map<String, DataProperty> getDataProperyDictionary() {
		return this.dataProperyMap;
	}
	
	@XmlElement(nillable = false, required = false)
	public Map<String, String> getExtraSettings() {
		return this.extraSettings;
	}
}
