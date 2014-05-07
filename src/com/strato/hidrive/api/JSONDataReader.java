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
package com.strato.hidrive.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.strato.hidrive.api.interfaces.DataReader;

public class JSONDataReader implements DataReader {

	interface ElementValue<T> {
		public T value(JsonElement jsonElement);
	}

	private JsonObject jsonObject;

	public void setJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public JSONDataReader(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	private <T> T get(String name, T defaultValue, ElementValue<T> elementValue) {
		return isFieldExists(name) ?  elementValue.value(jsonObject.get(name)) : defaultValue;
	}

	@Override
	public int readIntWithName(String name) {
		int defaultValue = 0;
		return get(name, defaultValue, new ElementValue<Integer>() {
			@Override
			public Integer value(JsonElement jsonElement) {
				return jsonElement.getAsInt();
			}
		});
	}

	@Override
	public long readLongWithName(String name) {
		long defaultValue = 0L;
		return get(name, defaultValue, new ElementValue<Long>() {
			@Override
			public Long value(JsonElement jsonElement) {
				return jsonElement.getAsLong();
			}
		});
	}

	@Override
	public boolean readBooleanWithName(String name) {
		boolean defaultValue = false;
		return get(name, defaultValue, new ElementValue<Boolean>() {
			@Override
			public Boolean value(JsonElement jsonElement) {
				return jsonElement.getAsBoolean();
			}
		});
	}

	@Override
	public String readStringWithName(String name) {
		String defaultValue = "";
		return get(name, defaultValue, new ElementValue<String>() {
			@Override
			public String value(JsonElement jsonElement) {
				return jsonElement.getAsString();
			}
		});
	}

	@Override
	public Date readDateWithName(String name) {
		return null;
	}

	@Override
	public DataReader readDataReaderWithName(String name) {
		if (!isFieldExists(name)) {
			return null;
		}
		JsonObject defaultValue = null;
		return new JSONDataReader(get(name, defaultValue, new ElementValue<JsonObject>() {
			@Override
			public JsonObject value(JsonElement jsonElement) {
				return jsonElement.getAsJsonObject();
			}
		}));
	}

	@Override
	public List<DataReader> readDataReaderListWithName(String name) {
		if (!isFieldExists(name)) {
			return null;
		}

		JsonArray defaultValue = null;
		JsonArray array = get(name, defaultValue, new ElementValue<JsonArray>() {
			@Override
			public JsonArray value(JsonElement jsonElement) {
				return jsonElement.getAsJsonArray();
			}
		});

		List<DataReader> dataReaders = new ArrayList<DataReader>();
		for (JsonElement jsonObject : array) {
			dataReaders.add(new JSONDataReader((JsonObject) jsonObject));
		}
		return dataReaders;

	}

	@Override
	public String readStringValue() {
		return null;
	}

	@Override
	public double readDoubleWithName(String name) {
		double defaultValue = 0D;
		return get(name, defaultValue, new ElementValue<Double>() {
			@Override
			public Double value(JsonElement jsonElement) {
				return jsonElement.getAsDouble();
			}
		});
	}

	@Override
	public boolean isFieldExists(String name) {
		return jsonObject.has(name) && !jsonObject.get(name).isJsonNull();
	}

}
