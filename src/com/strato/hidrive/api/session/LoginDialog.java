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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;

import com.strato.hidrive.api.dal.TokenEntity;
import com.strato.hidrive.api.session.LoginView.LoginViewListener;

public class LoginDialog extends Dialog {
	private LoginView loginView;
	private String clientSecret;
	private String clientId;
	private LoginViewListener loginViewListenerDelegate;
	private OnCancelListener onCancellListener;

	public LoginDialog(Context context,
			String clientId, String clientSecret, LoginViewListener loginViewListener, OnCancelListener onCancellListener) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		this.clientSecret = clientSecret;
		this.clientId = clientId;
		this.loginViewListenerDelegate = loginViewListener;
		this.onCancellListener = onCancellListener;
		init();
	}

	private void init() {
		setCancelable(true);
		setOnCancelListener(onCancellListener);
		loginView = new LoginView(getContext(), clientId, clientSecret);
		loginView.setListener(loginViewListener);
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		addContentView(loginView, lp);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	private LoginViewListener loginViewListener = new LoginViewListener() {

		@Override
		public void onConnectionProblem(Exception error) {
			dismiss();
			if (loginViewListenerDelegate != null) {
				loginViewListenerDelegate.onConnectionProblem(error);
			}
		}

		@Override
		public void onAuthSuccess(String userName, TokenEntity token) {
			dismiss();
			if (loginViewListenerDelegate != null) {
				loginViewListenerDelegate.onAuthSuccess(userName, token);
			}
		}
	};
}
