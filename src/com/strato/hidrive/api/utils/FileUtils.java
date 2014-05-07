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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtils {
	
	private FileUtils() {
	}
	
	public static final char EXTENSIONS_SEPARATOR = '.';
	public static final char SIZE_SEPARATOR = '.';
	public static final char PATH_SEPARATOR = '/';

	public static int calcSDCardSize() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();

		return (int) (bytesAvailable / 1048576);
	}

	public static String correctPath(String path) {
		if (!path.endsWith("/")) {
			return path + "/";
		}
		return path;
	}

	public static boolean deleteDir(File dir) {
		if (dir == null){
			return false;
		}
		
		if (dir.exists() && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static long getDirectorySize(File dir) {
		long size = 0;
		if (dir.exists() && dir.isDirectory()) {
			File[] children = dir.listFiles();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					if (children[i].isDirectory()) {
						size += getDirectorySize(children[i]);
					} else {
						size += children[i].length();
					}
				}
			}
		}
		return size;
	}
	
	public static void deleteFileOrDir(File file) {
		if (file == null){
			return;
		}
		if (file.exists()) {
			if (file.isDirectory()) {
				deleteDir(file);
			} else {
				file.delete();
			}
		}
	}

	public static long getFileOrDirSize(File file_or_dir) {
		return file_or_dir.isDirectory() ? getDirectorySize(file_or_dir) : file_or_dir.length();
	}

	// adds to files all directory child files and parent directory himself
	public static void addAllDirectoryFiles(File dir, List<File> files) {
		if (dir.exists() && dir.isDirectory()) {
			files.add(dir);
			File[] childrens = dir.listFiles();
			if (childrens != null) {
				for (int i = 0; i < childrens.length; i++) {
					if (childrens[i].isDirectory()) {
						addAllDirectoryFiles(childrens[i], files);
					} else {
						files.add(childrens[i]);
					}
				}
			}
		}
	}

	public static long countFileListContentLength(List<File> files) {
		long totalContentLength = 0;
		for (File file : files) {
			if (!file.isDirectory()) {
				totalContentLength += file.length();
			}
		}
		return totalContentLength;
	}

	public static boolean copyFile(File from_file, File to_file) {
		FileInputStream from = null; // Stream to read from source
		FileOutputStream to = null; // Stream to write to destination
		try {
			from = new FileInputStream(from_file); // Create input stream
			to = new FileOutputStream(to_file); // Create output stream
			byte[] buffer = new byte[4096]; // To hold file contents
			int bytes_read; // How many bytes in buffer

			while ((bytes_read = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytes_read); // write
			}
		} catch (Exception e) {
			return false;
		}
		// Always close the streams, even if exceptions were thrown
		finally {
			if (from != null) {
				try {
					from.close();
				} catch (IOException e) {
					;
				}
			}
			if (to != null) {
				try {
					to.close();
				} catch (IOException e) {
				}
			}
		}
		return true;
	}

	public static String extractFileExtension(String fullPath) {
		int dot = fullPath.lastIndexOf(EXTENSIONS_SEPARATOR);
		if (dot == -1) {
			return "";
		}
		return fullPath.substring(dot + 1).toLowerCase();
	}
	
	public static boolean isFileNameHasExtension(String fileName) {
		return fileName.lastIndexOf(".") != -1;
	}
	
	public static String makeNonConflictingFileName(String originalName, int index) {
		int dotPos = originalName.lastIndexOf(".");
		if (dotPos > 0) {
			return originalName.substring(0, dotPos) + "(" + index + ")" + originalName.substring(dotPos);
		} else {
			return originalName + "(" + index + ")";
		}
	}

	public static String extractFileName(String fullPath) {

		int dot = fullPath.lastIndexOf(EXTENSIONS_SEPARATOR);
		int sep = fullPath.lastIndexOf(PATH_SEPARATOR);

		if (dot == -1 || sep > dot) {
			dot = fullPath.length();
		}

		if (dot == fullPath.length() - 1) {
			dot += 1;
		}

		return fullPath.substring(sep + 1, dot);
	}

	public static String extractFileNameWithExtentions(String fullPath) {
		return fullPath.substring(fullPath.lastIndexOf(PATH_SEPARATOR) + 1, fullPath.length());
	}

	public static String extractPath(String pathWithFileName) {
		return pathWithFileName.substring(0, pathWithFileName.lastIndexOf(PATH_SEPARATOR));
	}

	public static Object loadObject(Context context, String fileName) {
		Object data = null;
		try {
			FileInputStream stream = context.openFileInput(fileName);
			if (stream != null) {
				ObjectInputStream in = new ObjectInputStream(stream);
				data = in.readObject();
				return data;
			}
		} catch (Exception e) {
			Log.w(FileUtils.class.getSimpleName(), e.getClass().getSimpleName());
		}
		return null;
	}

	public static void saveObject(Serializable data, Context context, String fileName) {
		try {
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			if (fos != null) {
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(data);
				oos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String findGalleryImagePath(Uri selectedImage, Context context) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor == null){
        	return null;
        }
        
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        cursor.close();
        return null;
	}
}
