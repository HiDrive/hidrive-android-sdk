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

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.strato.hidrive.api.bll.directory.CreateDirectoryGateway;
import com.strato.hidrive.api.bll.directory.GetDirectoryGateway;
import com.strato.hidrive.api.bll.file.GetFileGateway;
import com.strato.hidrive.api.bll.file.GetThumbnailGateway;
import com.strato.hidrive.api.bll.file.PutFileGateway;
import com.strato.hidrive.api.bll.filesystem.CopyGateway;
import com.strato.hidrive.api.bll.filesystem.DeleteGateway;
import com.strato.hidrive.api.bll.filesystem.MoveGateway;
import com.strato.hidrive.api.bll.filesystem.RenameGateway;
import com.strato.hidrive.api.bll.sharelink.CreateShareLinkGateway;
import com.strato.hidrive.api.bll.sharelink.DeleteShareLinkGateway;
import com.strato.hidrive.api.bll.sharelink.EditShareLinkGateway;
import com.strato.hidrive.api.bll.sharelink.ReadListShareLinkGateway;
import com.strato.hidrive.api.bll.sharelink.ReadShareLinkGateway;
import com.strato.hidrive.api.connection.gateway.DomainGatewayResult;
import com.strato.hidrive.api.connection.gateway.StreamReadingGateway.StreamReadingGatewayListener;
import com.strato.hidrive.api.connection.gateway.interfaces.DomainGatewayHandler;
import com.strato.hidrive.api.dal.FileInfo;
import com.strato.hidrive.api.dal.RemoteFileInfo;
import com.strato.hidrive.api.dal.ShareLinkEntity;
import com.strato.hidrive.api.session.HiDriveSession;

/**
 *	Location of the HiDrive API functions.
 *	All methods has two versions: synchronous (ends with "Sync" postfix) and asynchronous.
 */
public class HiDriveRestClient {
	public static final String PATH_SEPARATOR = "/";
	public static final String ROOT_PATH = "root";
	public static final String USERS_DIR_NAME = "users";
	public static final String MOBILE_UPLOAD_DIR_NAME = "mobile upload";
	public static final String PUBLIC_DIR_NAME = "public";
	public static final String USERS_PATH = ROOT_PATH + PATH_SEPARATOR + USERS_DIR_NAME;
	public static final String PUBLIC_FOLDER_PATH = ROOT_PATH + PATH_SEPARATOR + PUBLIC_DIR_NAME;
	
	private HiDriveSession session;
	private Handler syncHandler;
	
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	
	/**
	 * Special domain gateway handler needed for handling file upload requests
	 */
	public interface UploadDomainGatewayHandler extends DomainGatewayHandler<Boolean>, StreamReadingGatewayListener {}

	public HiDriveRestClient(HiDriveSession session) {
		super();
		this.session = session;
		syncHandler = new Handler(Looper.getMainLooper());
	}
	
	/**
	 * Creates a new HiDrive folder.
	 * @param path the HiDrive path to the new folder.
	 * @param handler callback to handle request result.
	 */
	public void createDirectory(final String path, final DomainGatewayHandler<RemoteFileInfo> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<RemoteFileInfo> result = new CreateDirectoryGateway(path).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}

	public DomainGatewayResult<RemoteFileInfo> createDirectorySync(String path) {
		return new CreateDirectoryGateway(path).execute();
	}
	
	/**
	 * Returns information about directory and contained files/directories
	 * @param path the HiDrive path to the new folder.
	 * @param handler callback to handle request result.
	 */
	public void getDirectory(final String path, final DomainGatewayHandler<RemoteFileInfo> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<RemoteFileInfo> result = new GetDirectoryGateway(path, true).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<RemoteFileInfo> getDirectorySync(final String path) {
		return new GetDirectoryGateway(path, true).execute();
	}
	
	/**
	 * Download file
	 * @param path the HiDrive path to the new folder.
	 * @param handler callback to handle request result.
	 */
	public void loadFileForPath(final String path, final UploadDomainGatewayHandler handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<Boolean> result = new GetFileGateway(path, handler).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<Boolean> loadFileForPathSync(String path, StreamReadingGatewayListener handler) {
		return new GetFileGateway(path, handler).execute();
	}
	
	/**
	 * Upload file with [fileName] to directory [directoryName]
	 * @param directoryName directory where file will be placed.
	 * @param fileName name of created file.
	 * @param stream stream of file data.
	 * @param streamLength length of data stream
	 * @param handler callback to handle request result.
	 */
	public void uploadFile(final String directoryName, final String fileName, final InputStream stream, final long streamLength, final DomainGatewayHandler<RemoteFileInfo> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<RemoteFileInfo> result = new PutFileGateway(directoryName, fileName, stream, streamLength).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<RemoteFileInfo> uploadFileSync(String directoryName, String fileName, InputStream stream, long streamLength) {
		return new PutFileGateway(directoryName, fileName, stream, streamLength).execute();
	}
	
	/**
	 * Deletes a file or folder.
	 * @param fileInfo file entity
	 * @param handler callback to handle request result.
	 */
	public void deleteFile(final FileInfo fileInfo, final DomainGatewayHandler<List<FileInfo>> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<List<FileInfo>> result = new DeleteGateway(fileInfo).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<List<FileInfo>> deleteFileSync(FileInfo fileInfo) {
		return new DeleteGateway(fileInfo).execute();
	}
	
	/**
	 * Moves a file or folder from one path to another.
	 * @param source file entity
	 * @param destinationDirectory destination folder path
	 * @param handler callback to handle request result.
	 */
	public void moveFile(final FileInfo source, final String destinationDirectory, final DomainGatewayHandler<FileInfo> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<FileInfo> result = new MoveGateway(source, destinationDirectory).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<FileInfo> moveFileSync(FileInfo source, String destinationDirectory) {
		return new MoveGateway(source, destinationDirectory).execute();
	}
	
	/**
	 * Copies a file or folder from one path to another.
	 * @param source file entity
	 * @param destinationDirectory destination folder path
	 * @param handler callback to handle request result.
	 */
	public void copyFile(final FileInfo source, final String destinationDirectory, final DomainGatewayHandler<FileInfo> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<FileInfo> result = new CopyGateway(source, destinationDirectory).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<FileInfo> copyFileSync(FileInfo source, String destinationDirectory) {
		return new CopyGateway(source, destinationDirectory).execute();
	}
	
	/**
	 * Renames file or folder
	 * @param source file entity
	 * @param newName new file name
	 * @param handler callback to handle request result.
	 */
	public void renameFile(final FileInfo source, final String newName, final DomainGatewayHandler<FileInfo> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<FileInfo> result = new RenameGateway(source, newName).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<FileInfo> renameFileSync(FileInfo source, String newName) {
		return new RenameGateway(source, newName).execute();
	}
	
	/**
	 * Load thumbnail for file
	 * @param filePath file path
	 * @param width thumbnail width
	 * @param height thumbnail height
	 * @param handler callback to handle request result.
	 */
	public void loadThumbnailForFile(final String filePath, final int width, final int height, final DomainGatewayHandler<Bitmap> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<Bitmap> result = new GetThumbnailGateway(filePath, width, height).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<Bitmap> loadThumbnailForFileSync(String filePath, int width, int height) {
		return new GetThumbnailGateway(filePath, width, height).execute();
	}
	
	/**
	 * Load list of all created by user file share links.
	 * @param handler callback to handle request result.
	 */
	public void loadShareLinks(final DomainGatewayHandler<List<ShareLinkEntity>> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<List<ShareLinkEntity>> result = new ReadListShareLinkGateway().execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<List<ShareLinkEntity>> loadShareLinksSync() {
		return new ReadListShareLinkGateway().execute();
	}
	
	/**
	 * Load sharelink info
	 * @param linkId share link Id
	 * @param handler callback to handle request result.
	 */
	public void loadShareLink(final String linkId, final DomainGatewayHandler<ShareLinkEntity> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<ShareLinkEntity> result = new ReadShareLinkGateway(linkId).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<ShareLinkEntity> loadShareLinkSync(String linkId) {
		return new ReadShareLinkGateway(linkId).execute();
	}
	
	/**
	 * Create share link for file
	 * @param path path to file
	 * @param timeToLive time to live of share link
	 * @param downloadMaxCount maximal downloads count
	 * @param linkPassword password for share link
	 * @param handler callback to handle request result.
	 */
	public void createShareLink(final String path, final long timeToLive, final int downloadMaxCount, final String linkPassword, final DomainGatewayHandler<ShareLinkEntity> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<ShareLinkEntity> result = new CreateShareLinkGateway(path, timeToLive, downloadMaxCount, linkPassword).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<ShareLinkEntity> createShareLinkSync(String path, long timeToLive, int downloadMaxCount, String linkPassword) {
		return new CreateShareLinkGateway(path, timeToLive, downloadMaxCount, linkPassword).execute();
	}
	
	/**
	 * Change existing share link
	 * @param linkId share link Id
	 * @param timeToLive time to live of share link
	 * @param downloadMaxCount maximal downloads count
	 * @param linkPassword password for share link
	 * @param handler callback to handle request result.
	 */
	public void editShareLink(final String linkId, final long timeToLive, final int downloadMaxCount, final String linkPassword, final DomainGatewayHandler<ShareLinkEntity> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<ShareLinkEntity> result = new EditShareLinkGateway(linkId, timeToLive, downloadMaxCount, linkPassword).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<ShareLinkEntity> editShareLinkSync(String linkId, long timeToLive, int downloadMaxCount, String linkPassword) {
		return new EditShareLinkGateway(linkId, timeToLive, downloadMaxCount, linkPassword).execute();
	}
	
	/**
	 * Delete share link
	 * @param linkId share link Id
	 * @param handler callback to handle request result.
	 */
	public void deleteShareLink(final String linkId, final DomainGatewayHandler<String> handler) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				final DomainGatewayResult<String> result = new DeleteShareLinkGateway(linkId).execute();
				syncHandler.post(new Runnable() {
					@Override
					public void run() {
						handler.handleDomainGatewayResult(result);
					}
				});
			}
		});
	}
	
	public DomainGatewayResult<String> deleteShareLinkSync(String linkId) {
		return new DeleteShareLinkGateway(linkId).execute();
	}
	
	/**
	 * @return current connected user private directory
	 */
	public String getUserDirectoryPath() {
		if (session == null || !session.isLinked()) {
			throw new IllegalStateException("Session not linked");
		} else {
			return USERS_PATH + PATH_SEPARATOR + session.getUserName();
		}
	}

	/**
	 * @return associated HiDrive session object
	 */
	public HiDriveSession getSession() {
		return session;
	}
}
