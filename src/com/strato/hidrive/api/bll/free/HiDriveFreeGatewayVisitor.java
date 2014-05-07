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
package com.strato.hidrive.api.bll.free;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.strato.hidrive.api.connection.httpgateway.DefaultHTTPGatewayVisitor;
import com.strato.hidrive.api.connection.httpgateway.request.Param;

public class HiDriveFreeGatewayVisitor  extends DefaultHTTPGatewayVisitor{
	
	JSONObject json=new JSONObject();
	
	protected String getUrl() {
		return "https://www.free-hidrive.com/order/";
	}
	
	public void visit(Param param) {
		try {
			JSONObject jsonValue = new JSONObject(param.getValue());
			json.put(param.getName(), jsonValue);
			return;
		}
		catch (JSONException e) {
			e.printStackTrace();		
		}
		
		try {
			json.put(param.getName(), param.getValue());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public List<NameValuePair> getHttpPostRequest() {
		super.getHttpPostRequest().add(new BasicNameValuePair("postdata",json.toString()));
		return super.getHttpPostRequest();
	}
}
