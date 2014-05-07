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

import java.util.ArrayList;
import java.util.Collections;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.strato.hidrive.api.dal.FileInfo;

public class FileListAdapter extends BaseAdapter {

	private ArrayList<FileInfo> files = new ArrayList<FileInfo>();

	@Override
	public int getCount() {
		return files.size();
	}

	@Override
	public Object getItem(int index) {
		return files.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup container) {
		FileItemView view;
		if (convertView != null) {
			view = (FileItemView) convertView;
		} else {
			view = new FileItemView(container.getContext());
		}
		view.setFileInfo(files.get(position));
		return view;
	}

	public void setFiles(ArrayList<FileInfo> files) {
		if (files == null) {
			files = new ArrayList<FileInfo>();
		}
		this.files = files;
		Collections.sort(files);
	}

}
