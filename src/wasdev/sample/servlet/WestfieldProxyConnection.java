package wasdev.sample.servlet;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;

//import Decoder.BASE64Encoder;

public class WestfieldProxyConnection {
	
	private HttpURLConnection conn;
	private static final String SOAP_CREDENTIALS = "SVC-INT-IBM-TEST:dcYxYU6n4@UGH!Rk";
	private String url;

	public WestfieldProxyConnection(String url) throws IOException {
		this.url = url;
		configureConnection();
	}
	
	private void configureConnection() throws IOException{
		StaticProxyService proxy = StaticProxyService.getInstance();
		URL oURL = new URL(url);
		String authorization  =  new sun.misc.BASE64Encoder().encode(SOAP_CREDENTIALS.getBytes());
		conn = (HttpURLConnection) oURL.openConnection();
		
		conn.setRequestProperty("Content-type", "text/xml; charset=utf-8");
		conn.setRequestProperty("SOAPAction","https://servicestest.westfieldgrp.com:44330/ClaimInquiry/service/retrieveClaimDetails/1.0");
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Basic " + authorization);
		conn.setRequestProperty("Proxy-Authorization", "Basic " + proxy.getEncodedAuth());
		conn.setRequestProperty("Accept-Encoding", "gzip");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		Authenticator.setDefault(proxy.getAuth());
	}
	
	public HttpURLConnection getConnection(){
		return conn;
	}
	
	

}
