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
package com.strato.hidrive.api.connection.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import com.strato.hidrive.api.connection.thread.interfaces.ICallBack;

public abstract class CallBackManager<T> implements Runnable {

	private static final int MAX_THREADS_COUNT = 2;

	private static ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS_COUNT);
	private Future<?> future;
	protected ICallBack<T> callBack;
	private boolean isCanceled = false;
	private Handler handler;

	public CallBackManager(ICallBack<T> callBack) {
		this.handler = new Handler(Looper.getMainLooper());
		this.callBack = callBack;
	}

	protected abstract T call();

	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		final T state = this.call();
		if (isCanceled) {
			return;
		}
		handler.post(new Runnable() {
			@Override
			public void run() {
				onCallBack(state);

			}
		});
	}

	protected void onCallBack(final T state) {
		if (callBack != null) {
			callBack.callBack(state);
		}
	}

	public void start() {
		future = executor.submit(this);
	}

	public void startInThread() {
		new Thread(this, "Call back manager thread").start();
	}

	public void cancel() {
		isCanceled = true;
		future.cancel(true);
	}
}
