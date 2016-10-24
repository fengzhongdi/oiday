package oiday.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import com.google.gson.Gson;

import oiday.data.OIDayConstants;
import oiday.data.PM25Obj;
import oiday.data.RawPM25Obj;
import oiday.data.DataProperty;
import oiday.data.DataPropertyMap;
import oiday.server.GeneralServer;

public class GeneralServerImp implements GeneralServer {

	private Logger logger = Logger.getLogger("GeneralImpl");
	private static HashMap<String, PM25Obj> pm25Hash = new HashMap<String, PM25Obj>();
	static long pm25TimeStamp = -1l;

	@Override
	public DataPropertyMap getDataProperties() throws Exception {
		Map<String, DataProperty> dataProperyHash = new HashMap<String, DataProperty>();
		Map<String, String> extraHash = new HashMap<String, String>();
		String line = "";
		Scanner scan = null;
		try {
			logger.info(">>> Enter getDataProperties.");
			scan = new Scanner(new File(OIDayConstants.CONFIG_PATH));
			while (scan.hasNext()) {
				line = scan.nextLine();
				if (line.length() > 0) {
					String[] lines = line.split("=");
					String[] atts = lines[1].split(";");
					dataProperyHash.put(lines[0], new DataProperty(atts[0], atts[1], atts[2], atts[3]));
				}
			}
			scan.close();
			scan = new Scanner(new File(OIDayConstants.EXTRA_CONFIG_PATH));
			while (scan.hasNext()) {
				line = scan.nextLine();
				if (line.length() > 0) {
					String[] lines = line.split("=");
					extraHash.put(lines[0], lines[1]);
				}
			}
			logger.info("<<< Exit sellerSeeAllBuyers.");
		} catch (Exception e) {
			logger.severe("Get exception, line = " + line);
			throw e;
		} finally {
			if (scan != null) {
				scan.close();
			}
		}
		return new DataPropertyMap(dataProperyHash, extraHash);
	}

	@Override
	public PM25Obj getPM25Info(String city) throws Exception {
		logger.info(">>> Enter getPM25Info.");
		PM25Obj pm25Obj = null;
		long sysTime = System.currentTimeMillis() / 1000L;
		if (!pm25Hash.containsKey(city) || sysTime - pm25Hash.get(city).getTime() > 3000) {
			pm25Obj = getPM25InfoFrom3rdParty(city, sysTime);
			pm25Hash.put(city, pm25Obj);
		}
		logger.info("<<< Exit getPM25Info.");
		return pm25Hash.get(city);
	}

	private PM25Obj getPM25InfoFrom3rdParty(String city, long sysTime) throws Exception {
		logger.info(">>> Enter getPM25InfoFrom3rdParty.");
		String urlStr = URLEncoder.encode(city, "UTF-8");
		PM25Obj pm25Obj = new PM25Obj(city, sysTime, new HashMap<String, String>());
		try {
			String requestStr = String.format(
					"http://www.nofm.cn/pm25api/oidayquery.json?city=%s&token=%s",
					urlStr, OIDayConstants.PM25TOKEN);
			URL url = new URL(requestStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			String jsonStr = "";
			logger.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				jsonStr += output;
			}
			ShallowJSONParser parser = new ShallowJSONParser();
			ArrayList<String> jsonList = parser.parseJSONToArray(jsonStr);
			for(String json : jsonList){
				HashMap<String, String> hash = parser.parseJSONToMap(json);
				if(hash.containsKey("station")){
					String station = "";
					if(hash.get("station").startsWith("\\u")){
						byte[] bytes = hash.get("station").getBytes();
						station = new String(bytes, Charset.forName("UTF-8"));
					}else{
						station = hash.get("station");
					}
					pm25Obj.addPM25Info(station, hash.get("pm25"));
				}
			}
			conn.disconnect();
			logger.info("<<< Exit getPM25InfoFrom3rdParty.");
		} catch (MalformedURLException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		return pm25Obj;
	}

}
