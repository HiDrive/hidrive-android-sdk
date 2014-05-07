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
package com.example.filemanager.filelist;

import com.example.filemanager.R;
import com.example.filemanager.SharelinksContainer;
import com.strato.hidrive.api.dal.FileInfo;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class FileItemView extends FrameLayout {
	private FileInfo fileInfo;

	private TextView tvFileName;
	private ImageView ivFolderHint;
	private TextView tvSharelink;	

	public FileItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public FileItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FileItemView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.view_file_item, this);
		tvFileName = (TextView) findViewById(R.id.tvFileName);
		ivFolderHint = (ImageView) findViewById(R.id.ivFolderHint);
		tvSharelink = (TextView) findViewById(R.id.tvSharelink);
	}

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
		tvFileName.setText(fileInfo.getName());
		ivFolderHint.setVisibility(fileInfo.isDirectory() ? VISIBLE : INVISIBLE);
		tvSharelink.setVisibility(
				SharelinksContainer.getInstance().getSharelinkForFile(fileInfo) != null ?
						VISIBLE : GONE);
	}
	

}
