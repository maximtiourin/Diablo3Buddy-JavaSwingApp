package com.fizzikgames.d3b;

/**
 * CropSettings store information relevant to the cropping of an image using the ImageProcessor
 * @author Maxim Tiourin
 * @version 1.00
 */
public class CropSettings {
	private int x;
	private int y;
	private int width;
	private int height;
	private int type;
	
	public static final int CROP_NONE = 0;
	public static final int CROP_MANUAL = 1;
	public static final int CROP_AUTOMATIC = 2;
	
	public CropSettings(int width, int height) {
		x = 0;
		y = 0;
		this.width = width;
		this.height = height;
		type = CROP_NONE;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
