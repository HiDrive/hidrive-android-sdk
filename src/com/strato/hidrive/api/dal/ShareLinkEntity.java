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

import java.io.Serializable;
import java.util.Date;

import com.strato.hidrive.api.interfaces.DataReader;

/**
 * A entity that describes a sharelink information associated with an remote file or directory.
 */
public class ShareLinkEntity implements Serializable {
	private static final long serialVersionUID = -7729296057329617677L;
	private final int MILLISECONDS_IN_SECOND = 1000;
	private final int SECONDS_IN_DAY = 24 * 60 * 60;
	
	public static final String SHARE_LINK_TYPE = "sharelink";
	public static final String SHARE_DIR_TYPE = "sharedir";

	private long createdTimestamp;
	private long timeToLive;
	private long lastModifiedTimestamp;
	private int downloadMaxCount;
	private int downloadsCount;

	private String downloadUri;
	private String path;
	private String id;
	private String password;
	private String status;
	private String username;
	
	private boolean writable;
	private String share_type;
	private String file_type;
	private String pid;
	private long valid_until;
	private boolean readable;
	private long remaining;
	private boolean has_password;
	
	private transient RemoteFileInfo file;

	/**
	 * Constructs instance from DataReader
	 * 
	 * @param dataReader DataReader with data about fields values
	 */
	public ShareLinkEntity(DataReader dataReader) {
		this.createdTimestamp = dataReader.readLongWithName("created");
		this.path = dataReader.readStringWithName("path");
		this.timeToLive = dataReader.readLongWithName("ttl") * MILLISECONDS_IN_SECOND;
		this.downloadMaxCount = dataReader.readIntWithName("maxcount");
		this.downloadsCount = dataReader.readIntWithName("count");
		this.downloadUri = dataReader.readStringWithName("uri");
		this.path = dataReader.readStringWithName("path");
		this.id = dataReader.readStringWithName("id");
		this.lastModifiedTimestamp = dataReader.readLongWithName("last_modified") * MILLISECONDS_IN_SECOND;
		this.password = dataReader.readStringWithName("password");
		this.status = dataReader.readStringWithName("status");
		this.username = dataReader.readStringWithName("username");
		
		this.writable = dataReader.readBooleanWithName("writable");
		this.share_type = dataReader.readStringWithName("share_type");
		this.file_type = dataReader.readStringWithName("file_type");
		this.pid = dataReader.readStringWithName("pid");
		this.valid_until = dataReader.readLongWithName("valid_until");
		this.readable = dataReader.readBooleanWithName("readable");
		this.remaining = dataReader.readLongWithName("remaining");
		this.has_password = dataReader.readBooleanWithName("has_password");
	}
	
	/**
	 * Get associated remote file info if any (api not guarantee that this was set)
	 * 
	 * @return associated file info or null
	 */
	public RemoteFileInfo getFile() {
		return file;
	}

	/**
	 * Sets associated file info
	 * 
	 * @param file file info to associate
	 */
	public void setFile(RemoteFileInfo file) {
		this.file = file;
	}

	/**
	 * Get share expiry.
	 * 
	 * @return share expiry. A positive number defining milliseconds from now.
	 */
	public long getTimeToLive() {
		return timeToLive;
	}

	/**
	 * Get share expiry in seconds
	 * @return share expiry. A positive number defining seconds from now.
	 */
	public long getTimeToLiveInSeconds() {
		return timeToLive / MILLISECONDS_IN_SECOND;
	}

	/**
	 * Total number of allowed successful downloads.
	 */
	public int getDownloadMaxCount() {
		return this.downloadMaxCount;
	}

	/**
	 * Get number of successfully completed downloads so far. 
	 * 
	 * @return number of successfully completed downloads so far. 
	 */
	public int getDownloadsCount() {
		return this.downloadsCount;
	}

	/**
	 * Calculate remaining allowed downloads.
	 * 
	 * @return remaining allowed downloads
	 */
	public int getDownloadRemaining() {
		return this.downloadMaxCount - this.downloadsCount;
	}

	/**
	 * Get download URI for the sharelink.
	 * 
	 * @return download URI for the sharelink. Only given on valid sharelinks
	 */
	public String getDownloadUri() {
		return this.downloadUri;
	}

	/**
	 * Get path to the linked file.
	 * 
	 * @return path to the linked file or directory.
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Get public path id to the linked file.
	 * 
	 * @return public path id to the linked file.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Get password for "private" sharelinks.
	 * 
	 * @return clear text password for "private" sharelinks.
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Check is sharelink protected by password.
	 * 
	 * @return true if sharelink protected with an password.
	 */
	public Boolean hasPasswordProtection() {
		return (this.password != null && this.password.length() != 0) || has_password;
	}

	/**
	 * Get sharelink status.
	 * 
	 * @return can have one of three states: "valid": Linked file is ready for download "expired": Sharelink has exceeded a limit (ttl or download count) "invalid": The file of the sharelink does not exist.
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Get sharelink creator name.
	 * 
	 * @return user alias name of the sharelink's creator.
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Get sharelink creation time
	 * 
	 * @return UNIX time stamp (seconds since the epoch) when the sharelink has been created.
	 */
	public long getCreatedTimestamp() {
		return this.createdTimestamp;
	}

	/**
	 * Get user-readable sharelink creation time
	 * 
	 * @return user-readable sharelink creation time
	 */
	public String getCreatedDateDescription() {
		return getDateDescription(createdTimestamp);
	}

	/**
	 * Get user-readable sharelink downloads expires description
	 * 
	 * @return user-readable sharelink downloads expires description
	 */
	public String getDownloadsExpiresDateDescription() {
		return getDateDescription(this.createdTimestamp + this.timeToLive);
	}

	/**
	 * Get user-readable sharelink last modification time
	 * 
	 * @return user-readable sharelink last modification time
	 */
	public String getLastModifiedDateDescription() {
		return getDateDescription(this.lastModifiedTimestamp);
	}

	private String getDateDescription(long timestamp) {
		return new Date(timestamp).toLocaleString();
	}

	/**
	 * Calculate number of days from now while sharelink will remains valid
	 * 
	 * @return number of days from now while sharelink will remains valid
	 */
	public int getValidDays() {
		long days = this.timeToLive / (SECONDS_IN_DAY * MILLISECONDS_IN_SECOND);
		if (this.timeToLive % (SECONDS_IN_DAY * MILLISECONDS_IN_SECOND) > 0) {
			days++;
		}
		return (int)days;
	}

	/**
	 * Set associated file path
	 * 
	 * @param path associated file path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Check is sharelink is valid
	 * 
	 * @return true if sharelink is valid
	 */
	public boolean isSharelinkValid(){
		return "valid".equals(status);
	}

	/**
	 * Get type of associated shared object
	 * 
	 * @return type description of associated shared object
	 */
	public String getFile_type() {
		return file_type;
	}

	/**
	 * The public id of the shared file.
	 * 
	 * @return public id of the shared file.
	 */
	public String getPid() {
		return pid;
	}
	
	/**
	 * Checks if sharelink is associated with file or directory object.
	 * 
	 * @return true if sharelink is associated with file or directory object.
	 */
	public boolean isShareLinkOrShareDirType() {
		return share_type == null || share_type.equals("") || 
				SHARE_LINK_TYPE.equals(share_type) || SHARE_DIR_TYPE.equals(share_type);
	}
}
