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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

	private DateUtils() {}
	
	public static boolean isDifferentMonth(Date oneDate, Date secondDate) {
		return secondDate.getMonth() != oneDate.getMonth() || secondDate.getYear() != oneDate.getYear();
	}

	public static String getStringFromCurrentDate() {
		Calendar c = Calendar.getInstance();
	     System.out.println("Current time => "+c.getTime());

	     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	     return df.format(c.getTime());
	}
	
	public static String getStringFromCurrentDate(String format) {
		Calendar c = Calendar.getInstance();
	     System.out.println("Current time => "+c.getTime());

	     SimpleDateFormat df = new SimpleDateFormat(format);
	     return df.format(c.getTime());
	}
	
	public static String getStringFromDate(Date date) {
	     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	     return df.format(date);
	}
	
	public static Date getDateFromString(String dateString, String format){
		SimpleDateFormat formatter = new SimpleDateFormat(format); 
		try {
			return formatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return null;
	}
}
