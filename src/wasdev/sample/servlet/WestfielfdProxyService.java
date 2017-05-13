package wasdev.sample.servlet;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
/**
 * 
 * @author angsdey2@in.ibm.com
 *
 */
public class WestfielfdProxyService {

	/**
	 * Send soap Request through proxy
	 * @param message
	 * @param westfieldApiUrl
	 * @return
	 * @throws SOAPException
	 * @throws MalformedURLException
	 */
	public static SOAPMessage sendRequestThroughProxy(SOAPMessage message, final String westfieldApiUrl) throws SOAPException, MalformedURLException {
		SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
		SOAPConnection connection = factory.createConnection();
		final Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("sl-ams-01-guido.statica.io", 9293));

		URL endpoint = new URL(null, westfieldApiUrl, new URLStreamHandler() {
			protected URLConnection openConnection(URL url) throws IOException {
				// The url is the parent of this stream handler, so must
				// create clone
				URL clone = new URL(url.toString());
				URLConnection connection = null;
				if (p.address().toString().equals("0.0.0.0/0.0.0.0:80")) {
					connection = clone.openConnection();
				} else{
					connection = clone.openConnection(p);
				}
				return connection;
			}
		});
/*		
		System.setProperty("https.proxyHost", "sl-ams-01-guido.statica.io");
		System.setProperty("https.proxyPort", "9293");
		System.setProperty("https.proxyUser", "statica3924");
		System.setProperty("https.proxyPassword", "731871029c0c6382");
*/

		Authenticator authenticator = new Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				return (new PasswordAuthentication("statica3924",
						"731871029c0c6382".toCharArray()));
			}
		};
		Authenticator.setDefault(authenticator);

		try {
			SOAPMessage response = connection.call(message, endpoint);
			connection.close();
			return response;
		} catch (Exception e) {
			// Re-try if the connection failed
			e.printStackTrace();
			SOAPMessage response = connection.call(message, endpoint);
			connection.close();
			return response;
		}
	}
}
