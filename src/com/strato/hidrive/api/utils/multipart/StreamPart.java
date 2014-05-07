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
package com.strato.hidrive.api.utils.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.protocol.HTTP;

import com.strato.hidrive.api.utils.multipart.helper.UrlEncodingHelper;

public final class StreamPart extends BasePart {

	private final InputStream stream;
	private final long streamLength;

	public StreamPart(String name, InputStream stream, long streamLength, String filename, String contentType) {
		this.stream = stream;
		this.streamLength = streamLength;
		final String partName = UrlEncodingHelper.encode(name, HTTP.DEFAULT_PROTOCOL_CHARSET);
		final String partFilename = UrlEncodingHelper.encode(filename, HTTP.DEFAULT_PROTOCOL_CHARSET);
		final String partContentType = (contentType == null) ? HTTP.DEFAULT_CONTENT_TYPE : contentType;

		headersProvider = new IHeadersProvider() {
			public String getContentDisposition() {
				return "Content-Disposition: form-data; name=\"" + partName //$NON-NLS-1$
						+ "\"; filename=\"" + partFilename + '"'; //$NON-NLS-1$
			}

			public String getContentType() {
				return "Content-Type: " + partContentType; //$NON-NLS-1$
			}

			public String getContentTransferEncoding() {
				return "Content-Transfer-Encoding: binary"; //$NON-NLS-1$
			}
		};
	}

	public long getContentLength(Boundary boundary) {
		return getHeader(boundary).length + streamLength + CRLF.length;
	}

	public void writeTo(OutputStream out, Boundary boundary) throws IOException {
		out.write(getHeader(boundary));
		try {
			byte[] tmp = new byte[4096];
			int l;
			while ((l = stream.read(tmp)) != -1) {
				out.write(tmp, 0, l);
			}
		} finally {
			stream.close();
		}
		out.write(CRLF);
	}
}
