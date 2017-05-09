package wasdev.sample.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
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

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;

public abstract class WestfieldServiceServlet  extends HttpServlet{

	private static final long serialVersionUID = 1L;

	private String soapRequestUrl;
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		try {

			String token = request.getParameter("token");
			if ("5531999940875".equals(token)) {	
				soapRequestUrl = getRequestUrl();
				trustAllCertificates();				
				//HttpURLConnection conn = new WestfieldProxyConnection(soapRequestUrl).getConnection() ;
				SOAPMessage message  = createSoapRequestMessage(request);
				String soapRequestBody = parseSOAPMessage(message);
				Socket socket = WestfieldProxySocket.getProxySocket(soapRequestUrl);
				if(socket == null){
					response.getWriter().print("Failed to create proxy socket");
					return;
				}
				try{
					socket = performSOAPRequest(socket,soapRequestBody);
					String payload = handleSoapResponse(socket);
					response.getWriter().print(payload);
				}finally{
					socket.close();
				}
				
				
			} else {
				response.getWriter().print("Wrong token");
			}
		} catch (Exception e) {
			e.printStackTrace(response.getWriter());
		}

	}

	public abstract SOAPMessage createSoapRequestMessage(HttpServletRequest request) throws Exception;

	public abstract String getRequestUrl();

	public HttpURLConnection performSOAPRequest(HttpURLConnection conn,String soapRequestBody) throws Exception{

		OutputStream reqStream = conn.getOutputStream();
		reqStream.write(soapRequestBody.getBytes());
		reqStream.flush();
		reqStream.close();

		return conn;
	}

	public Socket performSOAPRequest(Socket socket,String soapRequestBody) throws Exception{

		OutputStream reqStream = socket.getOutputStream();
		reqStream.write(soapRequestBody.getBytes());
		reqStream.flush();
		reqStream.close();

		return socket;
	}

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
		}};

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
	
	private String handleSoapResponse(Socket conn) throws IOException, UnsupportedEncodingException {
		InputStream is = conn.getInputStream();
		/*if(conn.getContentEncoding()!=null && conn.getContentEncoding().equalsIgnoreCase("gzip")){
			is = new GZIPInputStream(is);
		}
*/
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
