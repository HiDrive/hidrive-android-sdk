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
package com.example.hidrivesample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.strato.hidrive.api.HiDriveRestClient;
import com.strato.hidrive.api.connection.gateway.DomainGatewayResult;
import com.strato.hidrive.api.connection.gateway.interfaces.DomainGatewayHandler;
import com.strato.hidrive.api.dal.FileInfo;
import com.strato.hidrive.api.dal.RemoteFileInfo;
import com.strato.hidrive.api.dal.TokenEntity;
import com.strato.hidrive.api.session.HiDriveSession;
import com.strato.hidrive.api.session.HiDriveSession.HiDriveSessionListener;

public class MainActivity extends Activity {

	private static final String CLIENT_ID = "replace_me";
	private static final String CLIENT_SECRET = "replace_me";

	final static private int NEW_PICTURE = 1;

	private HiDriveSession session;
	private HiDriveRestClient hdClient;

	private Button btnLink;
	private Button btnDownload;
	private Button btnUpload;
	private ImageView ivPicture;
	private RelativeLayout rlContent;
	private TextView tvHint;
	private ProgressBar pbProgress;

	private String mCameraFileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		session = new HiDriveSession(this, CLIENT_ID, CLIENT_SECRET, hidriveSessionListener);
		hdClient = new HiDriveRestClient(session);

		initUI();

		updateUI();
	}

	private void initUI() {
		setContentView(R.layout.activity_main);
		btnLink = (Button) findViewById(R.id.btnLink);
		btnDownload = (Button) findViewById(R.id.btnDownload);
		btnUpload = (Button) findViewById(R.id.btnUpload);
		ivPicture = (ImageView) findViewById(R.id.ivPicture);
		rlContent = (RelativeLayout) findViewById(R.id.rlContent);
		tvHint = (TextView) findViewById(R.id.tvHint);
		pbProgress = (ProgressBar) findViewById(R.id.pbProgress);

		btnLink.setOnClickListener(onLinkBtnClickListener);
		btnDownload.setOnClickListener(onDownloadClickListener);
		btnUpload.setOnClickListener(onUploadClickListener);
	}

	public void updateUI() {
		btnLink.setText(session.isLinked() ? R.string.button_unlink_title
				: R.string.button_link_title);
		rlContent.setVisibility(session.isLinked() ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NEW_PICTURE) {
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = null;
				if (data != null) {
					uri = data.getData();
				}
				if (uri == null && mCameraFileName != null) {
					uri = Uri.fromFile(new File(mCameraFileName));
				}
				File file = new File(mCameraFileName);

				if (uri != null) {
					uploadFile(file);
				}
			}
		}
	}

	private void uploadFile(File file) {
		FileInputStream stream;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			showToast("Can not get file");
			return;
		}
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Uploading...");
		dialog.show();
		hdClient.uploadFile(hdClient.getUserDirectoryPath(), file.getName(), stream, file.length(),
				new DomainGatewayHandler<RemoteFileInfo>() {
					@Override
					public void handleDomainGatewayResult(DomainGatewayResult<RemoteFileInfo> result) {
						dialog.dismiss();
						if (result.getGatewayError() != null) {
							showToast(result.getGatewayError().getErrorMessage());
						} else {
							showToast("Image succesfully uploaded");
						}
					}
				});
	}

	private void downloadRandomPictureFromFolder(RemoteFileInfo folder) {
		List<FileInfo> images = new ArrayList<FileInfo>();
		for (FileInfo file : folder.getChilds()) {
			if (!file.isDirectory()
					&& ("png".equals(file.getExtension()) || "jpg".equals(file.getExtension()))) {
				images.add(file);
			}
		}
		if (images.isEmpty()) {
			tvHint.setVisibility(View.VISIBLE);
			pbProgress.setVisibility(View.INVISIBLE);
			return;
		}

		tvHint.setVisibility(View.INVISIBLE);
		FileInfo choosedImage = images.get((int) (Math.random() * images.size()));

		hdClient.loadThumbnailForFile(choosedImage.getFullPath(), ivPicture.getWidth(),
				ivPicture.getHeight(), new DomainGatewayHandler<Bitmap>() {
					@Override
					public void handleDomainGatewayResult(DomainGatewayResult<Bitmap> result) {
						pbProgress.setVisibility(View.INVISIBLE);
						if (result.getGatewayError() != null) {
							showToast(result.getGatewayError().getErrorMessage());
						} else {
							ivPicture.setImageBitmap(result.getResult());
						}
					}
				});
	}

	private OnClickListener onLinkBtnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (session.isLinked()) {
				session.unlink();
			} else {
				session.link();
			}
			updateUI();
		}
	};

	private OnClickListener onDownloadClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			pbProgress.setVisibility(View.VISIBLE);
			hdClient.getDirectory(hdClient.getUserDirectoryPath(),
					new DomainGatewayHandler<RemoteFileInfo>() {
						@Override
						public void handleDomainGatewayResult(
								DomainGatewayResult<RemoteFileInfo> result) {
							if (result.getGatewayError() != null) {
								pbProgress.setVisibility(View.INVISIBLE);
								showToast(result.getGatewayError().getErrorMessage());
							} else {
								downloadRandomPictureFromFolder(result.getResult());
							}
						}
					});
		}
	};

	private OnClickListener onUploadClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

			Date date = new Date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");

			String newPicFile = df.format(date) + ".jpg";
			String outPath = new File(Environment.getExternalStorageDirectory(), newPicFile)
					.getPath();
			File outFile = new File(outPath);

			mCameraFileName = outFile.toString();
			Uri outuri = Uri.fromFile(outFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
			try {
				startActivityForResult(intent, NEW_PICTURE);
			} catch (ActivityNotFoundException e) {
				showToast("There doesn't seem to be a camera.");
			}
		}
	};

	private HiDriveSessionListener hidriveSessionListener = new HiDriveSessionListener() {

		@Override
		public void onConnectionProblem(Exception error) {
			updateUI();
			showToast("Connection error: " + error.getMessage());
		}

		@Override
		public void onAuthorizationComplete(String userName, TokenEntity token) {
			updateUI();
			showToast("Authorization complete.\nUsername: " + userName);
		}

		@Override
		public void onAuthorizationCancelled() {
			updateUI();
			showToast("Authorization cancelled");
		}
	};

	protected void showToast(final String string) {
		Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
	}
}
