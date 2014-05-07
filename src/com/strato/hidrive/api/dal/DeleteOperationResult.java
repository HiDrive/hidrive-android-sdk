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

import java.util.List;

import org.apache.http.HttpStatus;

public class DeleteOperationResult {
	private int succesCount;
	private int errorCount;
	
	
	public DeleteOperationResult(List<OperationResult> results){
		for (OperationResult result : results){
			int status = result.getStatus();
			if (status == HttpStatus.SC_OK){
				this.succesCount++;
			}
			else{
				this.errorCount++;
			}
		}
	}
	
	public int getSuccesCount() {
		return this.succesCount;
	}
	
	public int getErrorCount() {
		return this.errorCount;
	}
}
