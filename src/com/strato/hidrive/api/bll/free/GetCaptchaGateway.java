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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.strato.hidrive.api.JSONDataReader;
import com.strato.hidrive.api.connection.httpgateway.interfaces.HTTPGatewayVisitor;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.PostRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.interfaces.DataReader;

public class GetCaptchaGateway extends HiDriveFreeGateway<String>{

	public GetCaptchaGateway() {
		super();
	}
	
	protected DataReader prepareDataReader() {	
		return new JSONDataReader(jsonObject);
	}

	@Override
	protected String prepareObject(DataReader datareader) {
		return datareader.readStringWithName("gd_image");
	}

	@Override
	protected Request prepareRequest() {
		List<BaseParam<?>> params = new ArrayList<BaseParam<?>>();
		JSONObject json = new JSONObject();
		try {
			json.put("width", "117");
			json.put("height", "24");
			json.put("bgcolor","#FFFFFF");
		} catch (JSONException e) {
			if (e!=null && e.getMessage()!=null){
				Log.e(getClass().getSimpleName(), e.getMessage());
			}
		}
		params.add(new Param("postdata", json.toString()));
		return new PostRequest("getcaptcha", params);
	}
	
	protected HTTPGatewayVisitor createHTTPGatewayVisitor() {
		return new HiDriveFreeGatewayVisitor();
	}

}
