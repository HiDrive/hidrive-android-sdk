# HiDrive Android SDK #
## Introduction ##

The HiDrive SDK for Android is the easiest way to integrate your Android app with HiDrive cloud storage service. The SDK provides support for login with HiDrive authentication, reading and writing to HiDrive APIs.
This multipart tutorial walks you through integrating HiDrive into an Android app. You'll create a simple app that authenticates and then loads random photo from HiDrive user private folder.
This sample app is based on the RandomThumbnail sample app bundled with the SDK. You can use the completed sample as a reference or jump to a specific step to see how to implement a specific feature.


##Requirements##

1. To complete the tutorial, you'll need to be familiar with Android development.
2. You need installed android SDK with minimal Android 2.1 (API 7) version of android platform API.
3. You should have a `client id` and `client secret` (please visit [HiDrive developer portal](https://dev.strato.com/hidrive/) to obtain it).


##Building and using the example app##

1.	Open the android project `sample` directory
2.	Fill in the values for `CLIENT_ID` and `CLIENT_SECRET` in MainActivity.java 
3.	Build and Run app 
4.	Once running, make sure you can use the app to login and view photos in your user HiDrive folder


##Adding HiDriveSDK to your project##

Simply add `HiDriveSDK.jar` and `gson-2.2.4.jar` to your apps java build path. This libraries are located in sdk's lib directory.

##Authenticating your app##

The HiDrive API uses OAuth v2. 
You will need to provide your `client id` and `client secret`. Visit [HiDrive developer portal](https://dev.strato.com/) to obtain it. Once you have your client id and client secret, you can create the `HiDriveSession` object for your app. To do this, add the following in your code  
```
HiDriveSession session = new HiDriveSession(getApplicationContext(), CLIENT_ID, CLIENT_SECRET, hidriveSessionListener);
```
You also need to implement `HiDriveSessionListener` interface for processing session related events.

Now we are all set to start the authentication flow. We'll start by calling the `session.link();`
method which will ask the user to authorize your app.    

The `HiDriveSession` will call your implemented `HiDriveSessionListener` with one of the following callbacks:
```
@Override
public void onConnectionProblem(Exception error) {

}

@Override
public void onAuthorizationComplete(String userName, TokenEntity token) {

}

@Override
public void onAuthorizationCancelled() {

}
```
##Uploading files##

The `HiDriveRestClient` is your gateway to accessing HiDrive once the user has linked their account. The simplest way to use `HiDriveRestClient` from an object is to add a property:
```
private HiDriveRestClient hdClient;
...
hdClient = new HiDriveRestClient(session);
```
Now that you have created a `HiDriveRestClient` object, you are ready to make request. First, let's upload a file: 
```
private void uploadFile(File file, DomainGatewayHandler<RemoteFileInfo> handler) {
	FileInputStream stream;
	try {
		stream = new FileInputStream(file);
	} catch (FileNotFoundException e) {
		e.printStackTrace();
		showToast("Can not get file");
		return;
	}
	hdClient.uploadFile(hdClient.getUserDirectoryPath(), file.getName(), stream, file.length(), handler);
}
```

When calling this method the file with same name  will be placed in the user's HiDrive directory. 
All the methods on `HiDriveRestClient` are asynchronous, meaning they don't immediately return the data they are meant to load. Each method takes an `DomainGatewayHandler<?>` handler parameter. This is interface which you should implement for processing asynchronous request result. A `handleDomainGatewayResult` callback will give you the data you requested or contain an `GatewayError object` that has more details on why the request failed. 
If all goes well, the file will now be in the root of your app folder. 

##Get content of a directory##

For example, let's load content of user home directory
```
hdClient.getDirectory(hdClient.getUserDirectoryPath(), handler);
```

The rest client will call your handler:
```
new DomainGatewayHandler<RemoteFileInfo>() {
	@Override
	public void handleDomainGatewayResult(final DomainGatewayResult<RemoteFileInfo> result) {
		runOnUiThread(new Runnable() {
		@Override
		public void run() {
			if (result.getGatewayError() != null) {								showToast(result.getGatewayError().getErrorMessage());
			} else {												RemoteFileInfo directoryInfo = result.getResult();
				//TODO:process directoryInfo
			}
		}
	});
}
```
In `RemoteFileInfo objects` all information about files and folders in a user's HiDrive is stored.

##Downloading files##

You need call `hdClient.loadFileForPath(path, listener, resultHandler);` method to download the file. 

Here, `path` is the path in the user's HiDrive (you probably got this from a node object). 
To find out when the file download either succeeds or fails implement the `UploadDomainGatewayHandler` interface. It also has additional methods to control file downloading process:
```
new HiDriveRestClient.UploadDomainGatewayHandler() {
	@Override
	public OutputStream onPrepareOutputStream() {
		// prepare output stream for write downloaded data to
		return new FileOutputStream("myFile.zip");
	}
			
	@Override
	public void onDownloadProgress(long downloaded, long totalSize) {
		// here you can update UI to show file downloading progress
		// important: this method called inside network thread and you should first synchronize with main thread before updating UI	
	}
			
	@Override
	public void handleDomainGatewayResult(DomainGatewayResult<Boolean> result) {
		// TODO process result	
	}
}
```
Main method there is `onPrepareOutputStream` where you should return an `OutputStream` which will be used to save received data. Other methods can be used to control file downloading process.

##Next steps##

With this you should be equipped with everything you need to get started with the HiDrive API. If you're still not sure about something, the [community forum](https://dev.strato.com/hidrive/community) is a great place to find information and get help from fellow developers. Good luck!


