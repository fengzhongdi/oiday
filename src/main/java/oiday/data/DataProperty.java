package oiday.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataProperty {
	private String data_type;
	private String hysteresis;
	private String is_weighted;
	private String master_weight;

	public DataProperty() {

	}

	public DataProperty(String data_type, String hysteresis, String is_weighted, String master_weight) {
		this.data_type = data_type;
		this.hysteresis = hysteresis;
		this.is_weighted = is_weighted;
		this.master_weight = master_weight;
	}

	@XmlElement(nillable = false, required = false)
	public String getData_type() {
		return this.data_type;
	}

	@XmlElement(nillable = false, required = false)
	public String getHysteresis() {
		return this.hysteresis;
	}

	@XmlElement(nillable = false, required = false)
	public String getIs_weighted() {
		return this.is_weighted;
	}

	@XmlElement(nillable = false, required = false)
	public String getMaster_weight() {
		return this.master_weight;
	}
}
