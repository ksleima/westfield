package wasdev.sample.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import Decoder.BASE64Encoder;
/**
 * 
 * @author ibm
 *
 */
@WebServlet("/policyDetailsForVendorProd")
public class PolicyDetailsForVendorProd extends WestfieldServiceServlet {
	
	private static final long serialVersionUID = 1L;

	
	@Override
	protected String getHost(){
		String westfieldHost = System.getenv("WESTFIELD_HOST_PROD");
		return westfieldHost;
	}
	
	@Override
	public SOAPMessage createSoapRequestMessage(HttpServletRequest request) throws Exception {
		String SOAP_CREDENTIALS = System.getenv("SOAP_CREDENTIALS_PROD");
		String idValue = request.getParameter("id");
		String policyNumberVal = request.getParameter("policyNumber");
		String verificationDateVal = request.getParameter("verificationDate");
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();
		SOAPElement retrievePolicyDetailsForVendorRequest = soapBody.addChildElement("RetrievePolicyDetailsForVendorRequest", "",
				"http://www.westfieldgrp.com/enterprisemodel/wx");
		SOAPElement requestHeader = retrievePolicyDetailsForVendorRequest.addChildElement("requestHeader");
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
		SOAPElement policyNumber = retrievePolicyDetailsForVendorRequest.addChildElement("policyNumber");
		policyNumber.addTextNode(policyNumberVal);// "0001531939");
		
		SOAPElement verificationDate = retrievePolicyDetailsForVendorRequest.addChildElement("verificationDate");
		verificationDate.addTextNode(verificationDateVal);

		SOAPHeader soapHeader = envelope.getHeader();
		soapHeader.addNamespaceDeclaration("wsa", "http://www.w3.org/2005/08/addressing");

		MimeHeaders headers = soapMessage.getMimeHeaders();

		
		String authorization  =  new BASE64Encoder().encode(SOAP_CREDENTIALS.getBytes());
		headers.addHeader("Authorization", "Basic " + authorization);

		soapMessage.saveChanges();

		return soapMessage;
	}

	@Override
	public String getApiPath() {
		return "/PolicyInquiry/service/retrievePolicyDetailsForVendor/2.0";
	}
}
