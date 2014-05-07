/**
* Copyright 2014 STRATO AG
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.strato.hidrive.api.connection.httpgateway;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.util.Log;

import com.strato.hidrive.api.HttpClientManager;
import com.strato.hidrive.api.connection.httpgateway.interfaces.HTTPGatewayHandler;
import com.strato.hidrive.api.connection.httpgateway.interfaces.HTTPGatewayVisitor;
import com.strato.hidrive.api.connection.httpgateway.interfaces.UriRedirector;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.connection.httpgateway.response.Response;
import com.strato.hidrive.api.connection.httpgateway.result.HTTPGatewayResult;
import com.strato.hidrive.api.connection.thread.CallBackManager;
import com.strato.hidrive.api.connection.thread.interfaces.ICallBack;
import com.strato.hidrive.api.utils.Base64Utils;
import com.strato.hidrive.api.utils.StubSSLSocketFactory;

public class HTTPGateway<T> implements ICallBack<HTTPGatewayResult<T>> {
	protected HTTPGatewayVisitor visitor;
	protected DefaultHttpClient httpClient;
	private String accessToken;
	private static BasicCookieStore cookieStore = new BasicCookieStore();
	private CallBackManager<HTTPGatewayResult<T>> callBackManager;
	private static UriRedirector uriRedirector = null;

	public HTTPGateway(String accessToken) {
		this.accessToken = accessToken;
		this.visitor = createHTTPGatewayVisitor();
		this.httpClient = createHttpClient();
	}

	public HTTPGateway(String accessToken, HTTPGatewayVisitor visitor) {
		this.accessToken = accessToken;
		this.visitor = visitor;
		this.httpClient = createHttpClient();
	}

	public HTTPGateway(String accessToken, HTTPGatewayVisitor visitor, DefaultHttpClient client) {
		this(accessToken, visitor);
		this.httpClient = client;
	}

	private HTTPGatewayHandler<T> httpGatewayHandler;

	public void sendRequestAsync(final String baseUri, final Request request, final ResponseHandler<T> responseHandler, HTTPGatewayHandler<T> httpGatewayHandler) {
		this.httpGatewayHandler = httpGatewayHandler;
		this.callBackManager = new CallBackManager<HTTPGatewayResult<T>>(this) {
			protected HTTPGatewayResult<T> call() {
				return sendRequest(baseUri, request, responseHandler);
			}
		};
		this.callBackManager.start();
	}
	
	public void cancelAsyncRequest(){
		if (this.callBackManager != null)
		{
			this.callBackManager.cancel();
		}
	}

	public HTTPGatewayResult<T> sendRequest(String baseUri, Request request, ResponseHandler<T> responseHandler) {
		try {
			HttpRequestBase httpRequest = request.createHttpRequest(baseUri);
			
			if (uriRedirector != null) {
				httpRequest.setURI(new URI(uriRedirector.redirectUri(httpRequest.getURI().toString())));
			}
			
			if (accessToken.length() != 0) {
				httpRequest.addHeader("Authorization", "Bearer " + Base64Utils.base64Encode(accessToken));
			}
			
			httpClient.setCookieStore(cookieStore);
			
			T responseData = this.httpClient.execute(httpRequest, responseHandler, HttpClientManager.getInstance().getLocalHttpContext());
			List<Cookie> cookies = this.httpClient.getCookieStore().getCookies();
			if (cookies.isEmpty()) {
				Log.i("Cookie", "None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					Log.i("Cookie", "- " + cookies.get(i).toString());
				}
			}

			return new HTTPGatewayResult<T>(null, false, new Response<T>(responseData));
		} catch (Exception e) {
			e.printStackTrace();
			return new HTTPGatewayResult<T>(e, false, null);
		}
	}

	protected void onHandleHTTPGatewayResult(HTTPGatewayResult<T> httpGatewayResult) {
		if (httpGatewayHandler != null) {
			httpGatewayHandler.handleHTTPGatewayResult(httpGatewayResult);
		}
	}

	protected HTTPGatewayVisitor createHTTPGatewayVisitor() {
		return new DefaultHTTPGatewayVisitor();
	}

	protected DefaultHttpClient createHttpClient() {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 20000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 50000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		return sslStubClient(new DefaultHttpClient(httpParameters));
	}

	@Override
	public void callBack(HTTPGatewayResult<T> state) {
		onHandleHTTPGatewayResult(state);
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setUserAgentString(String userAgentString) {
		HttpProtocolParams.setUserAgent(httpClient.getParams(), userAgentString);
	}
	
	/**
	 * wrap an httpclient with this stub for prevent ssl unverified exceptions (for testing purposes) 
	 */
	public DefaultHttpClient sslStubClient(HttpClient client) {
	    try {
	        X509TrustManager tm = new X509TrustManager() { 
	            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(null, new TrustManager[]{tm}, null);
	        SSLSocketFactory ssf = new StubSSLSocketFactory(ctx);
	        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        ClientConnectionManager ccm = client.getConnectionManager();
	        SchemeRegistry sr = ccm.getSchemeRegistry();
	        sr.register(new Scheme("https", ssf, 443));
	        return new DefaultHttpClient(ccm, client.getParams());
	    } catch (Exception ex) {
	        return null;
	    }
	}
	
	public static void installURIRedirector(UriRedirector redirector) {
		HTTPGateway.uriRedirector = redirector;
	}
}
