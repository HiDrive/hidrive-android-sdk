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

import java.util.Random;

import org.apache.http.util.EncodingUtils;

import android.text.TextUtils;

/* package */ class Boundary {

    /* The pool of ASCII chars to be used for generating a multipart boundary. */
    private final static char[] MULTIPART_CHARS =
            "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();   //$NON-NLS-1$

    private final String boundary;
    private final byte[] startingBoundary;
    private final byte[] closingBoundary;
    
    /* package */ Boundary(String boundary) {
        if (TextUtils.isEmpty(boundary)) {
            boundary = generateBoundary();
        }
        this.boundary = boundary;
        
        final String starting = "--" + boundary + MultipartEntity.CRLF;         //$NON-NLS-1$
        final String closing  = "--" + boundary + "--" + MultipartEntity.CRLF;  //$NON-NLS-1$
        
        startingBoundary = EncodingUtils.getAsciiBytes(starting);
        closingBoundary  = EncodingUtils.getAsciiBytes(closing);
    }
    
    /* package */ String getBoundary() {
        return boundary;
    }

    /* package */ byte[] getStartingBoundary() {
        return startingBoundary;
    }

    /* package */ byte[] getClosingBoundary() {
        return closingBoundary;
    }
    
    private static String generateBoundary() {
        // Boundary delimiters must not appear within the encapsulated material, 
        // and must be no longer than 70 characters, not counting the two
        // leading hyphens.
        Random rand = new Random();
        final int count = rand.nextInt(11) + 30; // a random size from 30 to 40
        StringBuilder buffer = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }
}
