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
package com.strato.hidrive.api.session;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.strato.hidrive.api.bll.oauth.GetRefreshTokenGateway;
import com.strato.hidrive.api.connection.gateway.DomainGatewayResult;
import com.strato.hidrive.api.connection.gateway.interfaces.DomainGatewayHandler;
import com.strato.hidrive.api.dal.TokenEntity;
import com.strato.hidrive.api.oauth.OAuthConst;
import com.strato.hidrive.api.oauth.OAuthTokenManager;

public class LoginView extends RelativeLayout {

	private WebView webView;
	private ProgressBar pbLoadingLogin;
	private String lastPageUrl;
	private String clientId, clientSecret;
	private LoginViewListener listener;
	
	public interface LoginViewListener {
		void onConnectionProblem(Exception error);
		void onAuthSuccess(String alias, TokenEntity entity);
	}

	public LoginView(Context context) {
		super(context);
	}

	public LoginView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoginView(Context context, String clientId, String clientSecret) {
		super(context);
		init(clientId, clientSecret);
	}

	public void init(String clientId, String clientSecret) {
		setClientId(clientId);
		setClientSecret(clientSecret);
		initUI();
		initWebView();
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public void setListener(LoginViewListener listener) {
		this.listener = listener;
	}

	private void initUI() {
		webView = new WebView(getContext());
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		addView(webView, lp);
		pbLoadingLogin = new ProgressBar(getContext());
		pbLoadingLogin.setVisibility(GONE);
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(pbLoadingLogin, lp);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				pbLoadingLogin.setVisibility(View.VISIBLE);
				// fastfix: in some cases onPageStarted called several times for
				// one page
				if (url.equals(lastPageUrl)) {
					return;
				}
				lastPageUrl = url;

				checkRedirectUrl(url);
			}

			/*
			 * //stub for preventing ssl unverified errors (for testing
			 * purposes)
			 * 
			 * @Override public void onReceivedSslError(WebView view,
			 * SslErrorHandler handler, SslError error) { handler.proceed(); }
			 */

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				pbLoadingLogin.setVisibility(View.GONE);

			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Log.e("Login error", description);
				pbLoadingLogin.setVisibility(View.GONE);
			}
		});

		configureWebViewKeyboard();
		WebSettings webViewSettings = webView.getSettings();
		webViewSettings.setJavaScriptEnabled(true);
		loadLoginUrl();
	}

	private void loadLoginUrl() {
		webView.loadUrl(String.format(OAuthConst.LOGIN_URL, clientId, chooseLanguage(), "user%2Crw"));
	}

	private String chooseLanguage() {
		Log.d(LoginView.class.getSimpleName(), Locale.getDefault().getLanguage());
		if (Arrays.asList(OAuthConst.LOGIN_PAGE_SUPPORTED_LANGUAGES).contains(Locale.getDefault().getLanguage())) {
			return Locale.getDefault().getLanguage();
		} else {
			return OAuthConst.LOGIN_PAGE_DEFAULT_LANGUAGE;
		}
	}

	private void configureWebViewKeyboard() {
		webView.requestFocus(View.FOCUS_DOWN);
		webView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					break;
				}
				return false;
			}
		});
	}

	private void checkRedirectUrl(String url) {
		URI uri = URI.create(url);
		if (!OAuthConst.OAUTH_CALLBACK_HOST.equals(uri.getHost())) {
			return;
		}
		List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
		for (NameValuePair param : params) {
			if (OAuthConst.AUTH_CODE_QUERY_KEY.equals(param.getName())) {
				loadBlankPage();
				retrieveAccessToken(param.getValue(), getScopes(params));
			}
		}
	}
	
	private String getScopes(List<NameValuePair> params) {
		for (NameValuePair param : params) {
			if (OAuthConst.AUTH_SCOPE_QUERY_KEY.equals(param.getName())) {
				return param.getValue();
			}
		}
		return "";
	}

	protected void retrieveAccessToken(String authCode, String scopes) {
		new GetRefreshTokenGateway(authCode, clientId, clientSecret)
				.executeAsync(new DomainGatewayHandler<TokenEntity>() {
					@Override
					public void handleDomainGatewayResult(DomainGatewayResult<TokenEntity> result) {
						if (result.getError() != null) {
							if (listener != null) {
								listener.onConnectionProblem(result.getError());
							}
						} else {
							TokenEntity entity = result.getResult();
							OAuthTokenManager tokenManager = new OAuthTokenManager(getContext(), clientId, clientSecret);
							tokenManager.saveRefreshToken(entity.getRefreshToken());
							tokenManager.saveAccessToken(entity);
							
							if (listener != null) {
								listener.onAuthSuccess(entity.getAlias(), entity);
							}
						}
					}
				});
	}

	private void loadBlankPage() {
		webView.loadData("", "text/plain", "utf-8");
	}

	protected void reloadWebView() {
		if (webView != null) {
			webView.reload();
		}
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if (visibility == View.VISIBLE) {
			reloadWebView();
		}
	}

	public class receiverScreen extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_USER_PRESENT) && getWindowVisibility() == View.VISIBLE) {
				reloadWebView();
			}
		}

	}

}
