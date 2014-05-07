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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;

import com.strato.hidrive.api.dal.interfaces.IGalleryInfo;
import com.strato.hidrive.api.dal.interfaces.IGalleryInfoCollection;
import com.strato.hidrive.api.interfaces.DataReader;
import com.strato.hidrive.api.utils.FileUtils;

/**
 *	A entity that describes a file or folder stored remotely.
 */
@SuppressLint("DefaultLocale")
public class RemoteFileInfo implements FileInfo, IGalleryInfoCollection {
	private static final long serialVersionUID = -1093876213995226274L;

	private final int MILLISECONDS_IN_SECOND = 1000;

	private String id;
	private String path;
	private String name;
	private boolean isDirectory;
	private long lastModified;
	private long creationTime;
	private long contentLength;
	private boolean sizeWasCalculated;
	private boolean isHidden;
	private boolean isReadOnly;
	private ArrayList<ShareLinkEntity> shares;
	private transient boolean selected = false;
	private transient boolean focused = false;

	private List<FileInfo> childs;

	/**
	 * Constructs instance from DataReader
	 * 
	 * @param dataReader DataReader with data about fields values
	 */
	public RemoteFileInfo(DataReader dataReader) {
		this(dataReader, null);

	}

	/**
	 * Constructs instance from DataReader and parent folder path (used to construct full path, if such info not presented in datareader)
	 * @param dataReader DataReader with data about fields values
	 * @param optionalParentPath parent folder path or null
	 */
	public RemoteFileInfo(DataReader dataReader, String optionalParentPath) {
		this.name = dataReader.readStringWithName("name");

		if (optionalParentPath != null) {
			this.path = optionalParentPath + File.separator + name;
		} else {
			this.path = dataReader.readStringWithName("path");
		}

		this.id = dataReader.readStringWithName("id");
		this.lastModified = dataReader.readLongWithName("mtime") * MILLISECONDS_IN_SECOND;
		this.creationTime = dataReader.readLongWithName("ctime") * MILLISECONDS_IN_SECOND;
		this.isDirectory = "dir".equals(dataReader.readStringWithName("type"));
		this.isReadOnly = !dataReader.readBooleanWithName("writable");
		this.contentLength = dataReader.readLongWithName("size");
		this.sizeWasCalculated = this.isDirectory ? false : true;

		readShares(dataReader);
		
		readChilds(dataReader);
	}

	private void readChilds(DataReader dataReader) {
		if (dataReader.isFieldExists("members")) {
			childs = new ArrayList<FileInfo>();
			List<DataReader> memberReaders = dataReader.readDataReaderListWithName("members");
			for (DataReader reader : memberReaders) {
				RemoteFileInfo child = new RemoteFileInfo(reader, path);
				childs.add(child);
			}
		}
	}

	private void readShares(DataReader dataReader) {
		if (dataReader.isFieldExists("rshare")) {
			shares = new ArrayList<ShareLinkEntity>();
			List<DataReader> shareReaders = dataReader.readDataReaderListWithName("rshare");
			for (DataReader reader : shareReaders) {
				ShareLinkEntity share = new ShareLinkEntity(reader);
				shares.add(share);
			}
		}
	}

	/**
	 * Constructs instance directly by primary field values
	 */
	public RemoteFileInfo(String path, String name, boolean isDirectory, long lastModified, long creationTime,
			long contentLength, boolean isHidden, boolean isReadOnly) {
		this.path = path;
		this.name = name;
		this.isDirectory = isDirectory;
		this.lastModified = lastModified;
		this.creationTime = creationTime;
		this.contentLength = contentLength;
		this.isHidden = isHidden;
		this.isReadOnly = isReadOnly;
		this.sizeWasCalculated = this.isDirectory ? false : true;
	}

	/**
	 * Constructs instance only by file path
	 */
	public RemoteFileInfo(String path) {
		this.path = path;
		this.name = FileUtils.extractFileNameWithExtentions(path);
	}

	public String getName() {
		return name;
	}

	public String getFullPath() {
		return path;
	}

	@Override
	public long getLastModified() {
		return lastModified;
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}

	public String getPath() {
		return path;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public boolean isHidden() {
		return isHidden || (getName() == null) || getName().startsWith(".");
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	/**
	 * @deprecated not for public use.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @deprecated not for public use.
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * @deprecated not for public use.
	 */
	public boolean canSelected() {
		if ("/users/".equals(getPath()) || "/users".equals(getPath())) {
			return false;
		}
		if ("/public/".equals(getPath()) || "/public".equals(getPath())) {
			return false;
		}
		return true;
	}

	public String getExtension() {
		String extension = "";

		if (!isDirectory && getName().contains(separator)) {
			String[] tempPath = getName().split("\\" + separator);
			if (tempPath.length > 0) {
				extension = tempPath[tempPath.length - 1].toLowerCase(Locale.getDefault());
			}
		}
		return extension;
	}

	/**
	 * Compares this object with the specified object for order.
	 */
	@Override
	public int compareTo(FileInfo another) {
		if (isDirectory && !another.isDirectory()) {
			return -1;
		}
		if (!isDirectory && another.isDirectory()) {
			return 1;
		}
		return getName().compareTo(another.getName());
	}

	@Override
	public long getContentLength() {
		if (this.isDirectory && !this.sizeWasCalculated) {
			if (this.getChilds() == null) {
				return 0;
			}

			long directorySize = 0;
			for (FileInfo item : this.getChilds()) {
				if (item.isDirectory() && item.getChilds() == null) {
					continue;
				}
				directorySize += item.getContentLength();
			}

			this.sizeWasCalculated = true;
			this.contentLength = directorySize;
		}
		return contentLength;
	}

	public boolean isEqual(FileInfo file) {
		return file != null && getPath().equals(file.getPath()) && isDirectory == file.isDirectory()
				&& lastModified == file.getLastModified() && (contentLength == file.getContentLength() || isDirectory);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object file) {
		return this.isEqual((FileInfo) file);
	}

	@Override
	public ArrayList<FileInfo> getChilds() {
		return (ArrayList<FileInfo>) childs;
	}

	/**
	 * Returns folder contents as list of IGalleryInfo instances
	 */
	@Override
	public ArrayList<IGalleryInfo> getGalleryInfoChilds() {
		ArrayList<IGalleryInfo> list = new ArrayList<IGalleryInfo>();
		for (FileInfo file : childs) {
			list.add((IGalleryInfo) file);
		}
		return list;
	}

	/**
	 * Set folder contents info
	 */
	public void setChilds(ArrayList<FileInfo> childs) {
		this.childs = childs;
	}

	/**
	 * Finds file with given path inside file tree. Recursively looks through childs of this file info
	 * 
	 * @param path path of file to find
	 * @return founded file or null if no file with given path found
	 */
	public RemoteFileInfo findFileWithPath(String path) {
		if (this.getPath().equals(path)) {
			return this;
		} else if (childs != null) {
			for (FileInfo file : childs) {
				RemoteFileInfo di = ((RemoteFileInfo) file).findFileWithPath(path);
				if (di != null) {
					return di;
				}
			}
		}
		return null;
	}

	/**
	 * Same as {@link #findFileWithPath  findFileWithPath}, but found only directories
	 * 
	 */
	public RemoteFileInfo findDirWithChilds(String path) {
		RemoteFileInfo di = findFileWithPath(path);
		if ((di != null) && di.isDirectory() && (di.getChilds() != null)) {
			return di;
		} else {
			return null;
		}
	}

	/**
	 * Copies primary info from another instance of RemoteFileInfo
	 * 
	 * @param info instance of RemoteFileInfo to copy from
	 * @param withChilds set to true if children also should be copied
	 */
	public void copyInfoFrom(RemoteFileInfo info, boolean withChilds) {
		id = info.getId();
		isDirectory = info.isDirectory();
		lastModified = info.getLastModified();
		contentLength = info.getContentLength();
		isHidden = info.isHidden();
		isReadOnly = info.isReadOnly;
		sizeWasCalculated = info.sizeWasCalculated;
		shares = info.shares;
		if (withChilds) {
			childs = info.getChilds();
		}
	}

	@Override
	public boolean hasSharedLink() {
		return getFirstValidShareLinkOrShareDir() != null;
	}
	
	/**
	 * Get first valid share link or share dir info from all associated share information
	 * 
	 * @return first found valid share link or sharedir information entity
	 */
	public ShareLinkEntity getFirstValidShareLinkOrShareDir() {
		if (shares == null) {
			return null;
		}
		for (ShareLinkEntity share : shares) {
			if (share.isSharelinkValid() && share.isShareLinkOrShareDirType()) {
				return share;
			}
		}
		return null;
	}

	@Override
	public String getShareLinkId() {
		ShareLinkEntity entity = getFirstValidShareLinkOrShareDir();
		if (entity != null) {
			return entity.getId();
		}
		return null;
	}

	@Override
	public ArrayList<ShareLinkEntity> getShares(boolean createListIfNull) {
		if (createListIfNull && shares == null) {
			shares = new ArrayList<ShareLinkEntity>();
		}
		return shares;
	}

	@Override
	public int getChildsCount(boolean includeHiddenFiles) {
		int childsCount = 0;
		if (includeHiddenFiles) {
			childsCount = this.getChilds().size();
		} else {
			childsCount = notSystemFilesChilds().size();
		}
		return childsCount;
	}

	private List<FileInfo> notSystemFilesChilds() {
		ArrayList<FileInfo> notSysFiles = new ArrayList<FileInfo>();

		for (FileInfo file : this.childs) {
			if (!file.isHidden()) {
				notSysFiles.add(file);
			}
		}

		return notSysFiles;
	}

	@Override
	public String getLastModifiedDateDescription() {
		return getDateDescription(this.lastModified);
	}

	private String getDateDescription(long timestamp) {
		return DateFormat.format("d. MMM yyyy", new Date(timestamp)).toString();
	}

	/**
	 * @deprecated not for public use.
	 */
	@Override
	public boolean sizeWasCalculated() {
		return sizeWasCalculated;
	}

	/**
	 * @deprecated not for public use.
	 */
	@Override
	public boolean isFocused() {
		return focused;
	}

	/**
	 * @deprecated not for public use.
	 */
	@Override
	public void setFocused(boolean isFocused) {
		this.focused = isFocused;
	}

	/**
	 * Get unique file identifier
	 * 
	 * @return unique file identifier
	 */
	public String getId() {
		return id;
	}
}
