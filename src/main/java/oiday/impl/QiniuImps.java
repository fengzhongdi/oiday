package oiday.impl;

import com.qiniu.util.Auth;

public class QiniuImps {
	private static final String AK = "7yNbTp0tCH0MbstARiiT8p8wxKYBzpx5Y1suQnU8";
	private static final String SK = "yuUcaWFtB9B_U-mRzW1i3pLAd0jGaAdIe2Jtova7";
	static Auth auth = Auth.create(AK,SK);

	public static String getUpToken0(){
	    return auth.uploadToken("oiday");
	}
	

}
