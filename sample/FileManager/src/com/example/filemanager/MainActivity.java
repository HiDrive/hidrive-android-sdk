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
package com.example.filemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.filemanager.SharelinksContainer.OnSharelinksLoadedListener;
import com.example.filemanager.filelist.FileItemView;
import com.example.filemanager.filelist.FileListAdapter;
import com.strato.hidrive.api.HiDriveRestClient;
import com.strato.hidrive.api.connection.gateway.DomainGatewayResult;
import com.strato.hidrive.api.connection.gateway.interfaces.DomainGatewayHandler;
import com.strato.hidrive.api.dal.FileInfo;
import com.strato.hidrive.api.dal.RemoteFileInfo;
import com.strato.hidrive.api.dal.ShareLinkEntity;
import com.strato.hidrive.api.dal.TokenEntity;
import com.strato.hidrive.api.session.HiDriveSession;
import com.strato.hidrive.api.session.HiDriveSession.HiDriveSessionListener;

public class MainActivity extends Activity implements OnSharelinksLoadedListener {
	private static final String CLIENT_ID = "replace_me";
	private static final String CLIENT_SECRET = "replace_me";
	private static final String DOWNLOAD_CACHE_DIR = "HiDriveFileManagerSampleCache";
	private static final int ACTION_CREATE_SHARE_LINK = -102;
	private static final int ACTION_DELETE_SHARE_LINK = -101;

	private HiDriveSession hdSession;
	private HiDriveRestClient hdClient;

	private ProgressDialog progressDialog;
	private ListView lvFileList;
	private FileListAdapter fileListAdapter = new FileListAdapter();
	private RemoteFileInfo currentDirectory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();

		hdSession = new HiDriveSession(this, CLIENT_ID, CLIENT_SECRET, hiDriveSessionListener);
		hdClient = new HiDriveRestClient(hdSession);
		SharelinksContainer.getInstance().setListener(this);
		if (hdSession.isLinked()) {
			startLoadingDirectory(hdClient.getUserDirectoryPath());
			SharelinksContainer.getInstance().loadShareLinks(hdClient);
		} else {
			hdSession.link();
		}

	}

	private void initUI() {
		setContentView(R.layout.activity_main);
		lvFileList = (ListView) findViewById(R.id.lvFileList);
		lvFileList.setAdapter(fileListAdapter);
		lvFileList.setOnItemClickListener(onFileListClickListener);
		registerForContextMenu(lvFileList);

		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Please, wait...");
		progressDialog.setCancelable(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new_folder:
			showCreateFolderDialog();
			return true;

		default:
			return false;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.file_list, menu);
		AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
		FileInfo fileInfo = (FileInfo) fileListAdapter.getItem(acmi.position);
		if (fileInfo.isDirectory()) {
			return;//sharelinks can not be created for directories
		}
		if (SharelinksContainer.getInstance().getSharelinkForFile(fileInfo) != null) {
			menu.add(0, ACTION_DELETE_SHARE_LINK, 0, "Delete share link");
		} else {
			menu.add(0, ACTION_CREATE_SHARE_LINK, 0, "Create share link");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.action_rename:
			showFileRenameDialog((FileInfo) fileListAdapter.getItem(acmi.position));
			return true;
		case R.id.action_delete:
			deleteFile((FileInfo) fileListAdapter.getItem(acmi.position));
			return true;
		case R.id.action_copy:
			copyFile((FileInfo) fileListAdapter.getItem(acmi.position));
			return true;
		case R.id.action_move:
			moveFile((FileInfo) fileListAdapter.getItem(acmi.position));
			return true;
		case ACTION_DELETE_SHARE_LINK:
			deleteShareLink((FileInfo) fileListAdapter.getItem(acmi.position));
			return true;

		case ACTION_CREATE_SHARE_LINK:
			createShareLink((FileInfo) fileListAdapter.getItem(acmi.position));
			return true;

		default:
			return false;
		}
	}

	private void moveFile(FileInfo item) {
		progressDialog.show();
		hdClient.moveFile(item, hdClient.getUserDirectoryPath() + "/tmp", new DomainGatewayHandler<FileInfo>() {
			@Override
			public void handleDomainGatewayResult(DomainGatewayResult<FileInfo> result) {
				progressDialog.dismiss();
				if (result.getGatewayError() != null) {
					showToast("Error: " + result.getGatewayError().getErrorMessage());
				} else {
					showToast("Success");
					SharelinksContainer.getInstance().loadShareLinks(hdClient);
					fileListAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	private void copyFile(FileInfo item) {
		progressDialog.show();
		hdClient.copyFile(item, hdClient.getUserDirectoryPath() + "/tmp", new DomainGatewayHandler<FileInfo>() {
			@Override
			public void handleDomainGatewayResult(DomainGatewayResult<FileInfo> result) {
				progressDialog.dismiss();
				if (result.getGatewayError() != null) {
					showToast("Error: " + result.getGatewayError().getErrorMessage());
				} else {
					showToast("Success");
				}
			}
		});
	}

	private void createShareLink(FileInfo item) {
		final int ONE_HOUR_LIVE = 60 * 60;
		final int FIVE_DOWNLOADS = 5;
		progressDialog.show();
		hdClient.createShareLink(item.getFullPath(), ONE_HOUR_LIVE, FIVE_DOWNLOADS, null,
				new DomainGatewayHandler<ShareLinkEntity>() {
					@Override
					public void handleDomainGatewayResult(DomainGatewayResult<ShareLinkEntity> result) {
						progressDialog.dismiss();
						if (result.getGatewayError() != null) {
							showToast("Error: " + result.getGatewayError().getErrorMessage());
						} else {							
							copyLinkToClipboard(result);
							showToast("Sharelink copied to clipboard");
							SharelinksContainer.getInstance().getShareLinks().add(result.getResult());
							fileListAdapter.notifyDataSetChanged();
						}
					}
				});
	}

	private void deleteShareLink(FileInfo item) {
		ShareLinkEntity shareLink = SharelinksContainer.getInstance().getSharelinkForFile(item);
		progressDialog.show();
		hdClient.deleteShareLink(shareLink.getId(), new DomainGatewayHandler<String>() {
			@Override
			public void handleDomainGatewayResult(DomainGatewayResult<String> result) {
				progressDialog.dismiss();
				if (result.getGatewayError() != null) {
					showToast("Error: " + result.getGatewayError().getErrorMessage());
				} else {
					showToast("Success");
					SharelinksContainer.getInstance().loadShareLinks(hdClient);
				}
			}
		});
	}

	private void deleteFile(FileInfo fileInfo) {
		progressDialog.show();
		hdClient.deleteFile(fileInfo, new DomainGatewayHandler<List<FileInfo>>() {
			@Override
			public void handleDomainGatewayResult(DomainGatewayResult<List<FileInfo>> result) {
				if (result.getGatewayError() != null) {
					showToast("Error: " + result.getGatewayError().getErrorMessage());
					progressDialog.dismiss();
				} else {
					showToast("Success");
					startLoadingDirectory(currentDirectory.getFullPath());
				}
			}
		});
	}

	private void showFileRenameDialog(final FileInfo fileInfo) {
		final EditText editText = new EditText(this);
		editText.setText(fileInfo.getName());
		new AlertDialog.Builder(this).setView(editText).setTitle(R.string.action_rename)
				.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						renameFile(fileInfo, editText.getText().toString());
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	protected void renameFile(FileInfo fileInfo, String newName) {
		progressDialog.show();
		hdClient.renameFile(fileInfo, newName, new DomainGatewayHandler<FileInfo>() {
			@Override
			public void handleDomainGatewayResult(DomainGatewayResult<FileInfo> result) {
				if (result.getGatewayError() != null) {
					showToast("Error: " + result.getGatewayError().getErrorMessage());
					progressDialog.dismiss();
				} else {
					showToast("Success");
					startLoadingDirectory(currentDirectory.getFullPath());
					SharelinksContainer.getInstance().loadShareLinks(hdClient);
				}
			}
		});
	}

	private void showCreateFolderDialog() {
		final EditText editText = new EditText(this);
		new AlertDialog.Builder(this).setView(editText).setTitle(R.string.action_new_folder)
				.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						createFolder(editText.getText().toString());
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	private void createFolder(final String folderName) {
		progressDialog.show();
		hdClient.createDirectory(currentDirectory.getFullPath() + "/" + folderName,
				new DomainGatewayHandler<RemoteFileInfo>() {
					@Override
					public void handleDomainGatewayResult(DomainGatewayResult<RemoteFileInfo> result) {
						if (result.getGatewayError() != null) {
							showToast("Error: " + result.getGatewayError().getErrorMessage());
							progressDialog.dismiss();
						} else {
							showToast("Success");
							startLoadingDirectory(currentDirectory.getFullPath());
						}
					}
				});
	}

	protected void startLoadingDirectory(String path) {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		hdClient.getDirectory(path, new DomainGatewayHandler<RemoteFileInfo>() {
			@Override
			public void handleDomainGatewayResult(DomainGatewayResult<RemoteFileInfo> result) {
				progressDialog.dismiss();
				if (result.getGatewayError() != null) {
					showToast("Error: " + result.getGatewayError().getErrorMessage());
				} else {
					setCurrentDirectory(result.getResult());
				}
			}
		});
	}

	protected void setCurrentDirectory(RemoteFileInfo directory) {
		this.currentDirectory = directory;
		setTitle(currentDirectory.getFullPath());
		fileListAdapter.setFiles(directory.getChilds());
		fileListAdapter.notifyDataSetChanged();
	}

	protected void downloadAndOpenFile(FileInfo fileInfo) {
		final File localFile = new File(Environment.getExternalStorageDirectory() + File.separator + DOWNLOAD_CACHE_DIR
				+ File.separator + fileInfo.getFullPath());
		if (localFile.exists()) {
			openLocalFile(localFile);
			return;
		}
		new File(localFile.getParent()).mkdirs();
		final ProgressDialog determinatedProgressDlg = new ProgressDialog(this);
		determinatedProgressDlg.setIndeterminate(false);
		determinatedProgressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		determinatedProgressDlg.setMax(100);
		determinatedProgressDlg.show();
		hdClient.loadFileForPath(fileInfo.getFullPath(), new HiDriveRestClient.UploadDomainGatewayHandler() {
			@Override
			public OutputStream onPrepareOutputStream() {
				try {
					return new FileOutputStream(localFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			public void onDownloadProgress(final long downloaded, final long totalSize) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						determinatedProgressDlg.setProgress((int) (downloaded * 100 / totalSize));
					}
				});
			}

			@Override
			public void handleDomainGatewayResult(DomainGatewayResult<Boolean> result) {
				determinatedProgressDlg.dismiss();
				if (result.getGatewayError() != null) {
					showToast("Error: " + result.getGatewayError().getErrorMessage());
				} else {
					openLocalFile(localFile);
				}
			}
		});
	}

	private void openLocalFile(File localFile) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(localFile.getAbsolutePath());
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		if (type != null) {
			intent.setDataAndType(Uri.fromFile(localFile), type);
		} else {
			intent.setData(Uri.fromFile(localFile));
		}
		try {
			startActivity(intent);
		} catch (Exception e) {
			showToast("Can't open file");
		}
	}

	private HiDriveSessionListener hiDriveSessionListener = new HiDriveSessionListener() {
		@Override
		public void onConnectionProblem(Exception error) {
			showToast("Connection error: " + error.getMessage());
			finish();
		}

		@Override
		public void onAuthorizationComplete(String userName, TokenEntity token) {
			showToast("Authorization complete.\nUsername: " + userName);
			startLoadingDirectory(hdClient.getUserDirectoryPath());
			SharelinksContainer.getInstance().loadShareLinks(hdClient);
		}

		@Override
		public void onAuthorizationCancelled() {
			showToast("Authorization cancelled");
			finish();
		}
	};

	private OnItemClickListener onFileListClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
			FileInfo fileInfo = ((FileItemView) view).getFileInfo();
			if (fileInfo.isDirectory()) {
				startLoadingDirectory(currentDirectory.getFullPath() + "/" + fileInfo.getName());
			} else {
				downloadAndOpenFile(fileInfo);
			}
		}
	};

	@Override
	public void onBackPressed() {
		if (currentDirectory == null || hdClient.getUserDirectoryPath().equals(currentDirectory.getFullPath())) {
			super.onBackPressed();
		} else {
			String currentPath = currentDirectory.getFullPath();
			String parentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
			startLoadingDirectory(parentPath);
		}
	}

	protected void showToast(final String string) {
		Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSharelinksLoaded() {
		fileListAdapter.notifyDataSetChanged();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	private void copyLinkToClipboard(DomainGatewayResult<ShareLinkEntity> result) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
		    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		    clipboard.setText(result.getResult().getDownloadUri());
		} else {
		    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); 
		    android.content.ClipData clip = android.content.ClipData.newPlainText("sharelink",result.getResult().getDownloadUri());
		    clipboard.setPrimaryClip(clip);
		}
	}
}
