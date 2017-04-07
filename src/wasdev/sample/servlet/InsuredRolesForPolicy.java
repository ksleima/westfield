package wasdev.sample.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;

//import Decoder.BASE64Encoder;

/**
 * 
 * @author ibm
 *
 */
@WebServlet("/InsuredRolesForPolicy")
public class InsuredRolesForPolicy extends HttpServlet{
	
	private static final String SOAP_CREDENTIALS = "SVC-INT-IBM-TEST:dcYxYU6n4@UGH!Rk";
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		try{
			String id = request.getParameter("id");
			String policyNumber = request.getParameter("policyNumber");
			String verificationDate = request.getParameter("verificationDate");
			String token = request.getParameter("token");
			
			//TODO parameters may change according to the WSDL
			
			if ("5531999940875".equals(token)) {

				String payload = performSOAPRequest(id, policyNumber, verificationDate);

				response.getWriter().print(payload);

			} else {
				response.getWriter().print("Wrong token");
			}
			
			
		}catch(Exception e){
			e.printStackTrace(response.getWriter());
		}
	}
	
	public String performSOAPRequest(String id, String policyNumber, String verificationDate) throws Exception{
		trustAllCertificates();

		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory.createConnection();

//		String url = "https://servicestest.westfieldgrp.com/InsuranceAgreementManager/service/retrieveInsuredRolesForPolicy/2.0"; 
		String url = "https://servicestest.westfieldgrp.com:44330/InsuranceAgreementManager/service/retrieveInsuredRolesForPolicy/2.0";
		SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(id, policyNumber, verificationDate), url);

		// Process the SOAP Response
		String response = printSOAPResponse(soapResponse);

		soapConnection.close();

		return response;
	}

/*	
	private void trustAllCertificates() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			
			public boolean isServerTrusted(X509Certificate[] arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			public boolean isClientTrusted(X509Certificate[] arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			public X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stub
				return null;
			}
		}};

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/	

		private void trustAllCertificates() {
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


	private SOAPMessage createSOAPRequest(String idValue, String policyNumberVal, String verificationDateVal) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();
		SOAPElement retrieveInsuredRolesForPolicyRequest = soapBody.addChildElement("RetrieveInsuredRolesForPolicyRequest", "",
				"http://www.westfieldgrp.com/enterprisemodel/wx"); //TODO local name needs to be modified according the WSDL
		SOAPElement requestHeader = retrieveInsuredRolesForPolicyRequest.addChildElement("requestHeader");
		SOAPElement id = requestHeader.addChildElement("id");
		id.addTextNode(idValue);// "c54d408f-0def-4c00-bb5d-0a2c516cc9c5");
		SOAPElement cmdType = requestHeader.addChildElement("cmdType");
		cmdType.addTextNode("response");
		SOAPElement cmdMode = requestHeader.addChildElement("cmdMode");
		cmdMode.addTextNode("alwaysRespond");
		SOAPElement echoBack = requestHeader.addChildElement("echoBack");
		echoBack.addTextNode("false");
		SOAPElement refreshCache = requestHeader.addChildElement("refreshCache");
		refreshCache.addTextNode("false");
		
		SOAPElement queryCriteria = retrieveInsuredRolesForPolicyRequest.addChildElement("queryCriteria");
		
		SOAPElement policyNumber = queryCriteria.addChildElement("policyNumber");
		policyNumber.addTextNode(policyNumberVal);// "0001531939");
		
		SOAPElement verificationDate = queryCriteria.addChildElement("verificationDate");
		verificationDate.addTextNode(verificationDateVal);

		SOAPHeader soapHeader = envelope.getHeader();
		soapHeader.addNamespaceDeclaration("wsa", "http://www.w3.org/2005/08/addressing");

		MimeHeaders headers = soapMessage.getMimeHeaders();

		String authorization = new sun.misc.BASE64Encoder().encode(SOAP_CREDENTIALS.getBytes());
		
//		String authorization  =  new BASE64Encoder().encode(SOAP_CREDENTIALS.getBytes());
		headers.addHeader("Authorization", "Basic " + authorization);

		soapMessage.saveChanges();

		// /* Print the request message */
		// System.out.print("Request SOAP Message = ");
		// soapMessage.writeTo(System.out);
		// System.out.println();

		return soapMessage;
	}
	
	
	/**
	 * Method used to print the SOAP Response
	 */
	private String printSOAPResponse(SOAPMessage soapResponse) throws Exception {

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		Source sourceContent = soapResponse.getSOAPPart().getContent();
		System.out.print("\nResponse SOAP Message = ");
		StreamResult result = new StreamResult(os);
		transformer.transform(sourceContent, result);

		os.flush();
		os.close();

		return new String(os.toByteArray(), "utf8");
	}

}
