package wasdev.sample.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.ProxyClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

public class WestfieldProxySocket {

	public static Socket getProxySocket(String url) throws HttpException, IOException{
		ProxyClient proxyclient = new ProxyClient();
		 proxyclient.getHostConfiguration().setHost("servicestest.westfieldgrp.com");
	        // set the proxy host and port
	        proxyclient.getHostConfiguration().setProxy("sl-ams-01-guido.statica.io", 9293);
	        // set the proxy credentials, only necessary for authenticating proxies
	        proxyclient.getState().setProxyCredentials(
	            new AuthScope("sl-ams-01-guido.statica.io", 9293, null),
	            new UsernamePasswordCredentials("statica3924", "731871029c0c6382"));
	        
	        // create the socket
	        ProxyClient.ConnectResponse response = proxyclient.connect();
	        
	        if (response.getSocket() != null) {
	            Socket socket = response.getSocket();
            
                // go ahead and do an HTTP GET using the socket
                Writer out = new OutputStreamWriter(
                    socket.getOutputStream(), "utf8");
                out.write("GET "+url+" HTTP/1.1\r\n");  
                out.write("Host: servicestest.westfieldgrp.com\r\n"); 
                out.write("\r\n");  
                out.flush();
                System.out.println(socket.getInetAddress());
                return socket;
	           
	        } else {
	            // the proxy connect was not successful, check connect method for reasons why
	            System.out.println("Connect failed: " + response.getConnectMethod().getStatusLine());
	            System.out.println(response.getConnectMethod().getResponseBodyAsString());
	            return null;
	        }
	}}
