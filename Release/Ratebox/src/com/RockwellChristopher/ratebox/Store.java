/*
 *@author	Christopher Rockwell 
 *Description: This class is a constructor for the movie information.
 */

package com.RockwellChristopher.ratebox;

import java.io.Serializable;

// create movie constructor for storing the movie info into an array
public class Store implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String storeStr;

	public Store() {
		// TODO Auto-generated constructor stub
	}

	public Store(String str) {
		this.storeStr = str;
	}
}
