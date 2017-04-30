package wasdev.sample.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public abstract class WestfieldServiceServlet  extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	private String soapRequestUrl;
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		//String testUrl = "http://ip.jsontest.com/";
		//String testUrl = "http://servicestest.westfieldgrp.com:44330/ClaimInquiry/service/retrieveClaimDetails/1.0";
		//HTTPProxyDemo demo = new HTTPProxyDemo();
		//String s = demo.getResponse(StaticProxyService.getInstance(), testUrl);
		//System.out.println(s);
		//response.getWriter().print(s);
		try {
		
			String token = request.getParameter("token");
			if ("5531999940875".equals(token)) {	
				soapRequestUrl = getRequestUrl();
				trustAllCertificates();				
				HttpURLConnection conn = new WestfieldProxyConnection(soapRequestUrl).getConnection() ;
				SOAPMessage message  = createSoapRequestMessage(request);
				String soapRequestBody = parseSOAPMessage(message);
				conn = performSOAPRequest(conn,soapRequestBody);
				String payload = handleSoapResponse(conn);
				response.getWriter().print(payload);
			} else {
				response.getWriter().print("Wrong token");
			}
		} catch (Exception e) {
			e.printStackTrace(response.getWriter());
		}
	}
	
	public abstract SOAPMessage createSoapRequestMessage(HttpServletRequest request) throws Exception;

	public HttpURLConnection performSOAPRequest(HttpURLConnection conn,String soapRequestBody) throws Exception{
		
		OutputStream reqStream = conn.getOutputStream();
		reqStream.write(soapRequestBody.getBytes());
		reqStream.flush();
		reqStream.close();
		
		return conn;
	}
	
	public abstract String getRequestUrl();
	
	public  void trustAllCertificates() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method used to print the SOAP Response
	 */
	public String parseSOAPMessage(SOAPMessage soapMessage) throws Exception {

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		Source sourceContent = soapMessage.getSOAPPart().getContent();
		System.out.print("\nResponse SOAP Message = ");
		StreamResult result = new StreamResult(os);
		transformer.transform(sourceContent, result);

		os.flush();
		os.close();

		System.out.println(os.toByteArray());
		return new String(os.toByteArray(), "utf8");
	}
	
	private String handleSoapResponse(HttpURLConnection conn) throws IOException, UnsupportedEncodingException {
		InputStream is = conn.getInputStream();
		if(conn.getContentEncoding()!=null && conn.getContentEncoding().equalsIgnoreCase("gzip")){
			is = new GZIPInputStream(is);
		}

		byte[] buffer = new byte[1024];
		int len;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while (-1 != (len = is.read(buffer))) {
			bos.write(buffer, 0, len);
		}           
		String response = new String(bos.toByteArray(), "utf8");
		return response;
	}
		
	
}
