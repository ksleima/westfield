package wasdev.sample.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
public class HTTPProxyDemo {
  public static void main(String[] args) {
    new HTTPProxyDemo();
  }
  
  public HTTPProxyDemo(){
   
  }
  
  public String getResponse(StaticProxyService proxy, String urlToRead) {
        String result = "";
        try {
         URL url = new URL(urlToRead);
           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
           conn.setRequestProperty("Proxy-Authorization", "Basic " + proxy.getEncodedAuth());
           conn.setRequestProperty("Accept-Encoding", "gzip");
           Authenticator.setDefault(proxy.getAuth());
           conn.setRequestMethod("GET");
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
           result = new String(bos.toByteArray());
           is.close();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return result;
     }    
}