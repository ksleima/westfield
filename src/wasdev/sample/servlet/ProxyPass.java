package wasdev.sample.servlet;

import sun.misc.BASE64Encoder;
import java.io.*;
import java.net.*;

public class ProxyPass {
    public ProxyPass(String proxyHost, int proxyPort, final String userid, final String password, String url) {

        try {
        /* Create a HttpURLConnection Object and set the properties */
            URL u = new URL(url);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            HttpURLConnection uc = (HttpURLConnection)u.openConnection(proxy);

            Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
            //if (getRequestorType().equals(RequestorType.PROXY)) {
            return new PasswordAuthentication("statica3924", "731871029c0c6382".toCharArray());
            //}
            //return super.getPasswordAuthentication();
            }
            });

            uc.connect();

            /* Print the content of the url to the console. */
            showContent(uc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showContent(HttpURLConnection uc) throws IOException {
        InputStream i = uc.getInputStream();
        char c;
        InputStreamReader isr = new InputStreamReader(i);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /*public static void main(String[] args) {

        String proxyhost = "proxy host";
        int proxyport = port;
        String proxylogin = "proxy username";
        String proxypass = "proxy password";
        String url = "https://....";
        new ProxyPass(proxyhost, proxyport, proxylogin, proxypass, url);

    }*/
}