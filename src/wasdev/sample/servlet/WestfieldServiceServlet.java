package wasdev.sample.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.X509TrustManager;
/**
 * 
 * @author angsdey2@in.ibm.com
 *
 */
public abstract class WestfieldServiceServlet  extends HttpServlet{

	private static final long serialVersionUID = 1L;

	private String soapRequestUrl;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
		try {

			String token = request.getParameter("token");
			String westfieldToken  = System.getenv("WESTFIELD_TOKEN");
			if (westfieldToken.equals(token)) {	
				soapRequestUrl = getHost() + getApiPath();
				trustAllCertificates();				
				SOAPMessage message  = createSoapRequestMessage(request);
				SOAPMessage soapResponse = WestfielfdProxyService.sendRequestThroughProxy(message, soapRequestUrl);
				// Process the SOAP Response
				String strResponse = printSOAPResponse(soapResponse);
				response.getWriter().print(strResponse);
			} else {
				response.getWriter().print("Wrong token");
			}
		} catch (Exception e) {
			e.printStackTrace(response.getWriter());
		}

	}

	public abstract SOAPMessage createSoapRequestMessage(HttpServletRequest request) throws Exception;

	public abstract String getApiPath();
	
	protected abstract String getHost();

	public  void trustAllCertificates() {
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
			}
		};

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Parse Soap Response
	 * @param soapResponse
	 * @return
	 * @throws Exception
	 */
	private String printSOAPResponse(SOAPMessage soapResponse) throws Exception {

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		Source sourceContent = soapResponse.getSOAPPart().getContent();
		StreamResult result = new StreamResult(os);
		transformer.transform(sourceContent, result);

		os.flush();
		os.close();

		return new String(os.toByteArray(), "utf8");
	}


}
