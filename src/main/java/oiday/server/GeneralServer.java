package oiday.server;

import java.util.Map;

import oiday.data.DataProperty;
import oiday.data.DataPropertyMap;
import oiday.data.PM25Obj;

public interface GeneralServer {
	DataPropertyMap getDataProperties() throws Exception;
	
	PM25Obj getPM25Info(String city) throws Exception;
}
