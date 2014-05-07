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
import java.util.ArrayList;

import com.strato.hidrive.api.dal.interfaces.IGalleryInfo;

/**
 * A entity that describes a file or folder.
 */
public interface FileInfo extends Comparable<FileInfo>, Serializable, IGalleryInfo {
	static final String separator = ".";

	/**
	 * Get name of file
	 * 
	 * @return name of file
	 */
	String getName();

	/** 
	 * Get file path
	 * 
	 * @return file path
	 */
	String getPath();

	/**
	 * 	Get full file path. For compatibility only - currently the same as {@link FileInfo#getPath()}
	 * 
	 * * @return file path
	 */
	String getFullPath();

	/**
	 * Check is this entry represents directory
	 * 
	 * @return True if this entry is a directory, or false if it's a file.
	 */
	boolean isDirectory();

	/**
	 * Get last modified date timestamp
	 * 
	 * @return last modified date
	 */
	long getLastModified();

	/**
	 * Get creation time of this file
	 * 
	 * @return creation time of this file
	 */
	long getCreationTime();

	/**
	 * Get size of the file.
	 * 
	 * @return size of the file.
	 */
	long getContentLength();

	/**
	 * @deprecated not for public use.
	 */
	boolean sizeWasCalculated();

	/**
	 * Indicates what entry is hidden or system file
	 * 
	 * @return true if file hidden
	 */
	boolean isHidden();

	/**
	 * Indicates file is for read only
	 * 
	 * @return true if file is read only
	 */
	boolean isReadOnly();

	/**
	 * Get file extension
	 * 
	 * @return file extension
	 */
	String getExtension();

	/**
	 * Compares this file with another
	 * 
	 * @return true if files equal (determined by comparison path, size and some other flags)
	 */
	boolean isEqual(FileInfo file);

	/**
	 * @deprecated not for public use.
	 */
	boolean isSelected();

	/**
	 * @deprecated not for public use.
	 */
	boolean canSelected();

	/**
	 * @deprecated not for public use.
	 */
	void setSelected(boolean selected);

	/**
	 * Contains directory contents
	 * 
	 * @return array of files contained inside directory. Can be null if entry not a directory or if directory content info not loaded
	 */
	ArrayList<FileInfo> getChilds();

	/**
	 * Check is file has valid sharelink
	 * 
	 * @return true if file or directory has a valid sharelink
	 */
	boolean hasSharedLink();

	/**
	 * Get all associated sharelinks
	 * 
	 * @param createListIfNull guarantees not null result
	 * @return list of all associated sharelinks
	 */
	ArrayList<ShareLinkEntity> getShares(boolean createListIfNull);

	/**
	 * Get sharelink id
	 * 
	 * @return sharelink id if any
	 */
	String getShareLinkId();

	/**
	 * Get user-readable last modified date
	 * 
	 * @return user-readable last modified date
	 */
	String getLastModifiedDateDescription();

	/**
	 * Get count of directory files
	 * 
	 * @param includeHiddenFiles include or not hidden files
	 * @return count of files and subdirectories contained inside this folder
	 */
	int getChildsCount(boolean includeHiddenFiles);
}
