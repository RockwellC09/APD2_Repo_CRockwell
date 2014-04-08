package com.RockwellChristopher.ratebox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {

	/**
	 * Helper function for fetching all data from an InputStream
	 * as a byte array.
	 * 
	 * @param _is The stream from which data will be fetched.
	 * @return The stream data in byte array format.
	 */
	public static synchronized byte[] streamToBytes(InputStream _is) {
		byte[] byteData = null;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
 
		int count = 0;
		try {
			while (count != -1) {
				byte[] bytes = new byte[2048];
				count = _is.read(bytes);
				if(count == -1) {
					continue;
				}
 
				byteStream.write(bytes, 0, count);
				bytes = null;
			}
 
			byteData = new byte[byteStream.size()];
			byteData = byteStream.toByteArray();
 
			byteStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
 
		return byteData;
	}
 
	/**
	 * Helper function for copying all data from one stream into another.
	 * 
	 * @param is - The source stream.
	 * @param os - The destination stream.
	 */
	public static synchronized void streamToStream(InputStream _is, OutputStream _os) {
 
		int count = 0;
		try {
			while(count != -1) {
				byte[] bytes = new byte[2048];
				count = _is.read(bytes);
				if(count == -1) {
					continue;
				}
 
				_os.write(bytes, 0, count);
				bytes = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
}
