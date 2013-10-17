package org.nyer.sns.http.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class HttpUtil {

    /**
     * @param time
     * @return
     */
    public static String formatTimeGMT(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(new SimpleTimeZone(0, "GMT+0"));
        Date date = new Date(time);
        String dateAsString = dateFormat.format(date);
        return dateAsString + " GMT";
    }

    /**
     * @param maxThreadsPerHost 同时可以在跟host交互的线程数， 如果超过这个数的话其他请求就会等待。
     * @param connectionTimeoutSecs //http链接建立后过多少时间才断开， 否则很多请求就可以通过这个连接进行传输，这个值可以稍微设的高点， 如果通过这个连接传输时发生异常，那么这个请求就会自动关闭。
     * @param soTimeoutSecs 单个请求超时时间，即超过这个时间就抛异常了：SocketTimeoutException Read timed out
     *        java.net.ConnectException：表示服务端连不上，即挂了
     */
    public static DefaultHttpClient makeHttpClient(int maxThreadsPerHost, int connectionTimeoutSecs, int soTimeoutSecs) {
        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        connectionManager.setMaxTotal(maxThreadsPerHost);
        connectionManager.setDefaultMaxPerRoute(maxThreadsPerHost);
        
        DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager);
        
        HttpParams params = httpClient.getParams();
        params.setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, connectionTimeoutSecs * 1000L);
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeoutSecs * 1000);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeoutSecs * 1000);
        
        // 让CookieStore实例不保存cookie。单个请求如果需要使用CookieStore，可以在发起请求时设置其它CookiePolicy。
        params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);

        // 不允许重定向
        httpClient.setRedirectStrategy(new RedirectStrategy() {
            
            @Override
            public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
                throws ProtocolException {
                return false;
            }
            
            @Override
            public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
                throws ProtocolException {
                return null;
            }
        });

        // gzip支持
        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {

            public void process(final HttpResponse response, final HttpContext context) throws HttpException,
                IOException {
                HttpEntity entity = response.getEntity();
                if (entity != null) { // be aware
                    Header contentEncodingHeader = entity.getContentEncoding();
                    if (contentEncodingHeader != null) {
                        HeaderElement[] codecs = contentEncodingHeader.getElements();
                        for (int i = 0; i < codecs.length; i++) {
                            if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                                response.setEntity(new GzipDecompressingEntity(entity));
                                return;
                            }
                        }
                    }
                }
            }

        });
        return httpClient;
        
    }
    
    public static UrlEncodedFormEntity createUrlEncodedFormEntity(List<NameValuePair> parameters, String charset) {
        try {
            return new UrlEncodedFormEntity(parameters, charset);
        } catch (UnsupportedEncodingException e) {
            // ignore
            throw new RuntimeException("Failed to create entity.", e);
        }
    }
    
    public static void main(String[] args) throws Exception {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 1970);
        System.out.println(c.getTimeInMillis());

    }

	public static boolean openURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				//doc
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows")) {
				//Windows
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);
			} else {
				//assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime().exec(
							new String[] { "which", browsers[count] })
							.waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser != null) {
					Runtime.getRuntime().exec(new String[] { browser, url });
				}
			}
			return true ;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false ;
		}
	}

}