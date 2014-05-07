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
package com.strato.hidrive.api.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

	private static final Pattern UNICODE_ENTITY_PATTERN = Pattern.compile("(?i)\\\\[u][0-9a-f]{4}");
	
	public static final long Kb = 1024;
	public static final long Mb = Kb * 1024;
	public static final long Gb = Mb * 1024;
	public static final long Tb = Gb * 1024;

	private StringUtils() {
	}

	public static String unescapeHiDriveCoding(String str) throws IOException {
		if (str == null) {
			return "";
		}
		Matcher matcher = UNICODE_ENTITY_PATTERN.matcher(str);
		ByteArrayOutputStream out = new ByteArrayOutputStream(str.length());
		int lastPos = 0;
		while (matcher.find()) {			
			out.write(str.substring(lastPos, matcher.start()).getBytes());
			int value = Integer.parseInt(matcher.group().substring(2), 16);
			out.write(value);
			lastPos = matcher.end();
		}
		out.write(str.substring(lastPos).getBytes());
		return new String(out.toByteArray(), "utf-8");
	}
	
	public static ByteArrayOutputStream unescapeHiDriveCodingStream(String str) throws IOException {
		if (str == null) {
			return null;
		}
		Matcher matcher = UNICODE_ENTITY_PATTERN.matcher(str);
		ByteArrayOutputStream out = new ByteArrayOutputStream(str.length());
		int lastPos = 0;
		while (matcher.find()) {			
			out.write(str.substring(lastPos, matcher.start()).getBytes());
			int value = Integer.parseInt(matcher.group().substring(2), 16);
			out.write(value);
			lastPos = matcher.end();
		}
		out.write(str.substring(lastPos).getBytes());
		return out;
	}


	public static String encodeUrlQueryValue(String path) {
		try {
			String value = URLEncoder.encode(path, "UTF-8");
			value = value.replaceAll("\\+", "%20");
			return value;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return path;
		}
	}
	
	public static boolean stringEndsWith(String str, String suffix, boolean ignoreCase) {
		if (str == null || suffix == null) {
			return (str == null && suffix == null);
		}
		if (suffix.length() > str.length()) {
			return false;
		}
		int strOffset = str.length() - suffix.length();
		return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
	}
}
