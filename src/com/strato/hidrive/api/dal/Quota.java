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
package com.strato.hidrive.api.dal;

import com.strato.hidrive.api.interfaces.DataReader;
import com.strato.hidrive.api.utils.FileUtils;

public class Quota {
	private String name;
	private long quota;
	private long used;
	private long available;
	private long usedBySnapshots;

	public Quota(DataReader rootDataReader) {
		this.name = rootDataReader.readStringWithName("name");
		this.quota = rootDataReader.readLongWithName("quota");
		this.used = rootDataReader.readLongWithName("used");
		this.available = rootDataReader.readLongWithName("available");
		this.usedBySnapshots = rootDataReader.readLongWithName("usedbysnapshots");
	}

	public long getUsedBySnapshots() {
		return usedBySnapshots;
	}

	public String getName() {
		return name;
	}

	public long getQuota() {
		return this.quota;
	}

	public long getAvailableSpace() {
		return this.available;
	}

	public long getUsedSpace() {
		return this.used;
	}
}
