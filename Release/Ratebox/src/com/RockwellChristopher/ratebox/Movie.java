/*
 *@author	Christopher Rockwell 
 *Description: This class is a constructor for the movie information.
 */

package com.RockwellChristopher.ratebox;

import java.io.Serializable;

// create movie constructor for storing the movie info into an array
public class Movie implements Serializable {


	private static final long serialVersionUID = 1L;
	public String movTitle;
	public String movImage;

	public Movie(String title, String image) {
		this.movTitle = title;
		this.movImage = image;
	}

	@Override
	public String toString() {
		return this.movTitle;
	}
}
