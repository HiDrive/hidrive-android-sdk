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

import java.util.Date;

import android.text.format.DateFormat;

import com.strato.hidrive.api.interfaces.DataReader;

public class ChangelogNews {
	private final int MILLISECONDS_IN_SECOND = 1000;

	private long createdTimestamp;
	private String subject;
	private String teaser;
	private String content;
	private String language;

	public String getLanguage() {
		return language;
	}

	public ChangelogNews(DataReader dataReader) {
		super();

		this.createdTimestamp = dataReader.readLongWithName("t_create") * MILLISECONDS_IN_SECOND;
		this.subject = dataReader.readStringWithName("subject");
		this.teaser = dataReader.readStringWithName("teaser");
		this.content = dataReader.readStringWithName("content");
		this.language = dataReader.readStringWithName("lang");
	}

	public String getCreatedDateDescription() {
		return DateFormat.format("dd. MMM yyyy", new Date(this.createdTimestamp)).toString();
	}

	public String getSubject() {
		return subject;
	}

	public String getTeaser() {
		return teaser;
	}

	public String getContent() {
		return content;
	}

}
