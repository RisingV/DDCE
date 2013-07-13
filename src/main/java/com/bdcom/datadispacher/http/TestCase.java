package com.bdcom.datadispacher.http;

import com.bdcom.datadispacher.ServerInfo;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2013-3-12 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class TestCase {

	private LocalHttpClient client;
	
	public TestCase() {
		client = new LocalHttpClient();
		init();
	}
	
	public void init() {
		client.setRequestURL("http://172.16.21.155:8080")
			  .appendReqURL("/test")
			  .addGetParams("test", "test");
	}
	
	public String getResutlt() {
		return client.doGetRequest();
	}
	
	static {
		System.out.println(
			"Server IP:"+ ServerInfo.getInetAddr().toString() 
		);
	}
	
	private static String fixJsonStr(String str) {
		if (str.startsWith("[") || str.endsWith("]")) {
			return str.substring(1,str.length() -1);
		}
		return str;
	}
	
	public static void main(String[] args) {
//		TestCase tc = new TestCase();
//		System.out.println(tc.getResutlt());
		System.out.println(
			new LocalHttpClient().setRequestURL("http://172.16.21.155:8080")
								 .appendReqURL("/test")
								 .addGetParams("test", "test")
								 .doGetRequest()
		);
		
		LocalHttpClient lhc = new LocalHttpClient();
		System.out.println(fixJsonStr(
			lhc.setRequestURL("http://172.16.21.155:8080")
								 .appendReqURL("/json")
								 .addGetParams("test", "test")
								 .doGetRequest()
		));
		lhc.clearGetParams();
		System.out.println(
			lhc.appendReqURL("/test")
			   .addGetParams("test", "test")
			   .doGetRequest()
		);
		
//		try {
//			JSONObject jo = new JSONObject(tc.getResutlt());
//			System.out.println(jo.toString());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
	}
}