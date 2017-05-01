package wasdev.sample.servlet;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

//import Decoder.BASE64Encoder;

public class StaticProxyService {

	private static StaticProxyService staticProxyService;

	private static String user;

	private static String password;

	private static String host;

	private static int port;

	private ProxyAuthenticator auth;

	private StaticProxyService(){
		setProxyEnvironment();
	}

	public static StaticProxyService getInstance(){
		if(staticProxyService == null){
			staticProxyService = new StaticProxyService();
			
		}
		return staticProxyService;
	}

	private static void setProxyEnvironment(){
		//String proxyUrlEnv = System.getenv("STATICA_URL");
		String proxyUrlEnv = "http://statica3924:731871029c0c6382@sl-ams-01-guido.statica.io:9293";
		
		if(proxyUrlEnv!=null){
			try {
				URL proxyUrl = new URL(proxyUrlEnv);
				String authString = proxyUrl.getUserInfo();
				user = authString.split(":")[0];
				password = authString.split(":")[1];
				host = proxyUrl.getHost();
				port = proxyUrl.getPort();    
				setProxy();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}     
		}
		else{
			System.err.println("You need to set the environment variable STATICA_URL!");
		}
	}

	private static void setProxy(){
/*		System.setProperty("http.proxyHost", host);
		System.setProperty("http.proxyPort", String.valueOf(port));
		System.setProperty("https.proxyHost",host);
		System.setProperty("https.proxyPort", String.valueOf(port));
		System.setProperty("http.proxyUser", user);
		System.setProperty("http.proxyPassword", password);
*/	}

	public String getEncodedAuth(){
		//If not using Java8 you will have to use another Base64 encoded, e.g. apache commons codec.
		String encoded = new sun.misc.BASE64Encoder().encode((user + ":" + password).getBytes());
		return encoded;
	}

	public ProxyAuthenticator getAuth(){
		if(auth == null){
			auth = new ProxyAuthenticator(user,password);
		}
		return auth;
	}

	private class ProxyAuthenticator extends Authenticator {

		private String user, password;

		public ProxyAuthenticator(String user, String password) {
			this.user = user;
			this.password = password;
		}
		 @Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, password.toCharArray());
		}
	}
}
