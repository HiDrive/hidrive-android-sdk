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

import android.graphics.Bitmap;
import android.util.Log;

import com.strato.hidrive.api.interfaces.DataReader;
import com.strato.hidrive.api.utils.DateUtils;

public class CustomerPreferences implements Comparable<CustomerPreferences>{
	private String mnc;
	private String mcc;
	private Date startDate;
	private Date endDate;
	private String os;
	private String manufacturer;
	private String partnerId;
	private String partnerRefId;
	private String partnerSubId;
	private String product;
	private String productName;
	private String notificationText;
	private String graphicLink;
	private Bitmap adImage;
	private int priority;
	private String creationDate;
	
	public CustomerPreferences(){
		super();
	}
	
	public CustomerPreferences(DataReader dataReader){
		parseStartDate(dataReader.readStringWithName("start_date"));
		parseEndDate(dataReader.readStringWithName("end_date"));
		
		mnc = dataReader.readStringWithName("mnc");
		mcc = dataReader.readStringWithName("mcc");		
		os = dataReader.readStringWithName("os");
		manufacturer = dataReader.readStringWithName("manufacturer");
		partnerId = dataReader.readStringWithName("partner_id");
		partnerRefId = dataReader.readStringWithName("partner_ref_id");
		partnerSubId = dataReader.readStringWithName("partner_sub_id");
		product = dataReader.readStringWithName("product");
		productName = dataReader.readStringWithName("product_name");
		notificationText = dataReader.readStringWithName("notification_text");
		graphicLink = dataReader.readStringWithName("graphic_link");
		priority = dataReader.readIntWithName("priority");
		creationDate = dataReader.readStringWithName("creation_date");
	}
	
	public void parseStartDate(String dateString){
		startDate = DateUtils.getDateFromString(dateString + " 00:00:00","MM/dd/yyyy HH:mm:ss");
	}
	
	public void parseEndDate(String dateString){
		endDate = DateUtils.getDateFromString(dateString + " 23:59:59","MM/dd/yyyy HH:mm:ss");
	}
	
	public String getMnc(){
		return mnc;
	}

	public void setMnc(String mnc){
		this.mnc = mnc;
	}

	public String getMcc(){
		return mcc;
	}

	public void setMcc(String mcc){
		this.mcc = mcc;
	}

	public Bitmap getAdImage(){
		return adImage;
	}

	public String getOs(){
		return os;
	}

	public void setOs(String os){
		this.os = os;
	}

	public String getManufacturer(){
		return manufacturer;
	}

	public void setManufacturer(String manufacturer){
		this.manufacturer = manufacturer;
	}

	public Date getStartDate(){
		return startDate;
	}

	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}

	public Date getEndDate(){
		return endDate;
	}

	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}

	public void setAdImage(Bitmap image){
		this.adImage = image;
	}

	public String getNotificationText(){
		return notificationText;
	}
	
	public String getGraphicLink(){
		return graphicLink;
	}
	
	public boolean hasGraphicLink(){
		return this.graphicLink.length() > 0;
	}
	
	public String getPartnerId(){
		return partnerId;
	}

	public String getPartnerRefId(){
		return partnerRefId;
	}

	public String getPartnerSubId(){
		return partnerSubId;
	}

	public String getProduct() {
		return product;
	}

	public String getProductName() {
		return productName;
	}

	public String getId() {
		return creationDate;
	}
	
	public Boolean isNotificationEmpty(){
		return notificationText == null || notificationText.equalsIgnoreCase("");
	}
	
	public Boolean isPartnerIdEmpty(){
		return partnerId == null || partnerId.equalsIgnoreCase("");
	}
	
	public Boolean isPartnerRefIdEmpty(){
		return partnerRefId == null || partnerRefId.equalsIgnoreCase("");
	}
	
	public Boolean isPartnerSubIdEmpty(){
		return partnerSubId == null || partnerSubId.equalsIgnoreCase("");
	}
	
	public Boolean isProductEmpty(){
		return product == null || product.equalsIgnoreCase("");
	}
	
	public Boolean isProductNameEmpty(){
		return productName == null || productName.equalsIgnoreCase("");
	}

	@Override
	public int compareTo(CustomerPreferences another){
		return another.priority - this.priority;
	}	
	
	public boolean isMatched(String mcc, String mnc, String manufacturer, String os){	
		return (this.isDateMatched() && this.isMccMatched(mcc) && this.isMncMatched(mnc) && this.isManufacturerMatched(manufacturer) && this.isOSMatched(os));
	}
	
	private boolean isDateMatched(){
		if(startDate == null || endDate == null){
			return true;
		}
		Date currentDate = new Date();
		return currentDate.compareTo(startDate) >= 0 && currentDate.compareTo(endDate) <= 0;
	}
	
	private boolean isMccMatched(String mccForMatch){
		return (this.mcc == null  || this.mcc.equalsIgnoreCase("") || this.mcc.equalsIgnoreCase(mccForMatch));
	}
	
	private boolean isMncMatched(String mncForMatch){
		return (this.mnc == null  || this.mnc.equalsIgnoreCase("") || this.mnc.equalsIgnoreCase(mncForMatch));
	}
	
	public boolean isManufacturerMatched(String manufacturerForMatch){
		return (this.manufacturer == null || this.manufacturer.equalsIgnoreCase("") || this.manufacturer.equalsIgnoreCase(manufacturerForMatch));
	}
	
	private boolean isOSMatched(String osForMatch){
		return (this.os == null || this.os.equalsIgnoreCase("") || this.os.equalsIgnoreCase(osForMatch));
	}
	
	public void writeToLog(){
		Log.i("CustomerPreferences", "mnc=" + mnc);
		Log.i("CustomerPreferences", "mcc=" + mcc);
		Log.i("CustomerPreferences", "startDate=" + DateUtils.getStringFromDate(startDate));
		Log.i("CustomerPreferences", "endDate=" + DateUtils.getStringFromDate(endDate));
		Log.i("CustomerPreferences", "os=" + os);
		Log.i("CustomerPreferences", "manufacturer=" + manufacturer);
		Log.i("CustomerPreferences", "partnerId=" + partnerId);
		Log.i("CustomerPreferences", "partnerRefId=" + partnerRefId);
		Log.i("CustomerPreferences", "partnerSubId=" + partnerSubId);
		Log.i("CustomerPreferences", "product=" + product);
		Log.i("CustomerPreferences", "productName=" + productName);
		Log.i("CustomerPreferences", "notificationText=" + notificationText);
		Log.i("CustomerPreferences", "graphicLink=" + graphicLink);
		Log.i("CustomerPreferences", "priority=" + priority);
		Log.i("CustomerPreferences", "creationDate=" + creationDate);
	}
}
