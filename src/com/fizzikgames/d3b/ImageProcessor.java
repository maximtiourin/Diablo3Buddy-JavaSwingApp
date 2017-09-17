package com.fizzikgames.d3b;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.fizzikgames.d3b.utility.AutoCropUtil;
import com.fizzikgames.d3b.utility.StringUtil;

/**
 * The image processor handles the loading, screengrabbing, and manipulation of images for the diablo 3 buddy.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class ImageProcessor {
	protected class ImageRectangle {
		private int x;
		private int y;
		private int width;
		private int height;
		private BufferedImage img;
		
		public ImageRectangle(BufferedImage bimg, int ax, int ay) {
			img = bimg;
			x = ax;
			y = ay;
			width = img.getWidth();
			height = img.getHeight();
		}
		
		/**
		 * Returns true if this ImageRectangle intersects e
		 */
		public boolean intersects(ImageRectangle e) {
			Rectangle thisrect = new Rectangle(x, y, width, height);
			Rectangle erect = new Rectangle(e.getX(), e.getY(), e.getWidth(), e.getHeight());
			
			return thisrect.intersects(erect);
		}
		
		public ImageRectangle copy() {
			ImageRectangle rect = new ImageRectangle(img, x, y);
			return rect;
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

		public BufferedImage getImg() {
			return img;
		}

		public void setImg(BufferedImage img) {
			this.img = img;
		}
	}
	
	private Shell shell;
	private AutoCropUtil acutil;
	private ArrayList<BufferedImage> imageBufferList;
	private ArrayList<Image> imageList;
	private ArrayList<String> imageKeyList;
	private ArrayList<CropSettings> imageCropSettingsList;
	private ArrayList<BufferedImage> imageCropList;
	private int count;
	
	public ImageProcessor(Shell shell) {
		this.shell = shell;
		acutil = new AutoCropUtil();
		imageBufferList = new ArrayList<BufferedImage>();
		imageList = new ArrayList<Image>();
		imageKeyList = new ArrayList<String>();
		imageCropSettingsList = new ArrayList<CropSettings>();
		imageCropList = new ArrayList<BufferedImage>();
		count = 0;
	}
	
	public void addImage(Image img, BufferedImage buffer, String key) {
		imageBufferList.add(buffer);
		imageList.add(img);
		imageKeyList.add(key + " (" + count++ + ")");
		imageCropSettingsList.add(new CropSettings(buffer.getWidth(), buffer.getHeight()));
		imageCropList.add(buffer);
	}
	
	public int getImageListSize() {
		return imageList.size();
	}
	
	public BufferedImage getBufferedImage(int index) {
		return imageBufferList.get(index);
	}
	
	public Image getImage(String key) {
		int index = imageKeyList.indexOf(key);
		return imageList.get(index);
	}
	
	public String getImageKey(int index) {
		return imageKeyList.get(index);
	}
	
	public CropSettings getImageCropSettings(int index) {
		return imageCropSettingsList.get(index);
	}
	
	public void removeImage(int index) {
		Image img = imageList.get(index);
		imageList.remove(index);
		img.dispose();
		imageKeyList.remove(index);
		imageBufferList.remove(index);
		imageCropSettingsList.remove(index);
		imageCropList.remove(index);
	}
	
	public void loadImageFromPath(String path) {		
		Image img = new Image(shell.getDisplay(), path);
		
		BufferedImage bimg = null;
		try {
			bimg = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		addImage(img, bimg, StringUtil.substring(StringUtil.substring(path, StringUtil.firstOccurenceBeforePos(path, "\\", path.length() - 1), path.length(), true), "\\", true));
	}
	
	public void screenCapture() {
		BufferedImage screencapture = null;
		try {
			screencapture = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		} catch (HeadlessException e1) {
			e1.printStackTrace();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}

		File file = new File("screencapture.png");
		try {
			ImageIO.write(screencapture, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		loadImageFromPath("screencapture.png");
		
		if (file.exists()) file.delete();
	}
	
	//Resets the crop list with the image buffer
	private void noCrop(int index) {
		BufferedImage crop = imageBufferList.get(index);
		
		imageCropList.remove(index);
		imageCropList.add(index, crop);
	}
	
	/**
	 * Crops the image using a rectangle with topleft most (x1, y1) and bottomright most (x1 + w, y1 + h) source and dest points.
	 */
	private void manualCrop(int index, int x1, int y1, int w, int h) {
		BufferedImage crop = imageBufferList.get(index);
		
		//Constrain the dimensions of the crop to be at the very least 1x1
		int v_x1 = Math.max(0, x1);
		v_x1 = Math.min(v_x1, crop.getWidth() - 1);
		int v_y1 = Math.max(0, y1);
		v_y1 = Math.min(v_y1, crop.getHeight() - 1);
		int v_w = Math.max(1, w);
		v_w = Math.min(v_w, crop.getWidth() - v_x1);
		int v_h = Math.max(1, h);
		v_h = Math.min(v_h, crop.getHeight() - v_y1);
		
		crop = crop.getSubimage(v_x1, v_y1, v_w, v_h);
		
		imageCropList.remove(index);
		imageCropList.add(index, crop);
	}
	
	/**
	 * Attempts to find the item tooltip in the image and crop around it.
	 */
	private void automaticCrop(int index) {
		BufferedImage crop = imageBufferList.get(index);
		Rectangle croptangle = acutil.findCropArea(crop);
		CropSettings cs = imageCropSettingsList.get(index);
		cs.setX((int) croptangle.getX());
		cs.setY((int) croptangle.getY());
		cs.setWidth((int) croptangle.getWidth());
		cs.setHeight((int) croptangle.getHeight());
		
		crop = crop.getSubimage((int) croptangle.getX(), (int) croptangle.getY(), (int) croptangle.getWidth(), (int) croptangle.getHeight());
		
		imageCropList.remove(index);
		imageCropList.add(index, crop);
	}
	
	/**
	 * Generates and saves a compiled image of all images
	 */
	public void generateCompilation(ProgressBar bar) {
		bar.setState(SWT.NORMAL);
		bar.setSelection(0);
		int amount = imageBufferList.size();
		int increment = 50 / amount;		
		for (int i = 0; i < amount; i++) {
			CropSettings cs = imageCropSettingsList.get(i);
			switch (cs.getType()) {
				case CropSettings.CROP_NONE: {
					noCrop(i);
					break;
				}
				case CropSettings.CROP_MANUAL: {
					manualCrop(i, cs.getX(), cs.getY(), cs.getWidth(), cs.getHeight());
					break;
				}
				case CropSettings.CROP_AUTOMATIC: {
					automaticCrop(i);
					break;
				}
			}
			
			bar.setSelection((int) Math.min(i * increment, 50));
		}
		
		bar.setSelection(50);
		
		generate(bar);
	}
	
	/**
	 * Generates the combined image using crops, and the generation settings
	 * Algorithm by Maxim Tiourin! :P
	 */
	private void generate(ProgressBar bar) {
		int maxW = 1920; //Maximum width
		final String output = "gen/generationImage.png";
		
		//Populate new Crop list that will be sorted by height
		ArrayList<BufferedImage> newCropList = new ArrayList<BufferedImage>();
		ArrayList<BufferedImage> sortedCropList = new ArrayList<BufferedImage>();
		for (BufferedImage e : imageCropList) {
			newCropList.add(e);
		}
		
		BufferedImage highest;
		while (newCropList.size() > 0) {
			highest = newCropList.get(0);
			for (BufferedImage e : newCropList) {
				if (e.getHeight() > highest.getHeight()) {
					highest = e;
				}
			}
			sortedCropList.add(highest);
			newCropList.remove(highest);
		}
		
		bar.setSelection(60);
		
		ArrayList<ImageRectangle> rects = new ArrayList<ImageRectangle>(); //Hold mutable version of the image layout
		//Calculate maxHeight
		int prevH = 0;
		int maxH = 0;		
		int w = 0;
		int usedW = 0; //The highest amount of width used, despite the max width
		for (BufferedImage e : sortedCropList) {
			if (e.getWidth() <= maxW) {
				if ((w + e.getWidth()) > maxW) {
					prevH = maxH;
					
					if (w > usedW) {
						usedW = w;
					}
					
					w = 0;
				}
				
				//x = w;
				//y = prevH;
				rects.add(new ImageRectangle(e, w, prevH)); //Add rect representation of the image
				
				w += e.getWidth();
				
				if (maxH < (prevH + e.getHeight())) {
					maxH = prevH + e.getHeight();
				}
			}
		}
		
		if (w != 0) {
			if (w > usedW) {
				usedW = w;
			}
		}
		
		bar.setSelection(70);
		
		//Shift Rectangles
		shiftRectanglesUp(rects);
		
		//Calculate new Max height
		maxH = findRectanglesMaxHeight(rects);
		
		bar.setSelection(80);
		
		//Make sure atleast one image made it through the process within the constraints
		if ((maxH > 0) && (maxW > 0)) {
			//Create container image
			BufferedImage gen = new BufferedImage(usedW, maxH, BufferedImage.TYPE_INT_ARGB); //Switch usedW with maxW when you want to enforce a specific width
			Graphics2D g = (Graphics2D) gen.getGraphics();
			
			//Actually fill the image
			g.setColor(Color.black);
			g.fillRect(0, 0, maxW, maxH);
			
			for (ImageRectangle e : rects) {
				g.drawImage(e.getImg(), null, e.getX(), e.getY());
			}
			
			/*prevH = 0;
			int nmaxH = 0;		
			w = 0;
			for (BufferedImage e : sortedCropList) {
				if (e.getWidth() <= maxW) {
					if ((w + e.getWidth()) > maxW) {
						prevH = nmaxH;
						w = 0;
					}

					//x = w;
					//y = prevH;
					g.drawImage(e, null, w, prevH);

					w += e.getWidth();

					if (nmaxH < (prevH + e.getHeight())) {
						nmaxH = prevH + e.getHeight();
					}
				}
			}*/
			
			bar.setSelection(90);

			//Save the image
			try {
				ImageIO.write(gen, "png", new File(output));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			bar.setSelection(100);
		}
		else {
			//Generate Error Image
			bar.setSelection(100);
			bar.setState(SWT.ERROR);
			
			BufferedImage gen = new BufferedImage(1024, 50, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) gen.getGraphics();
			
			g.setColor(Color.red);
			g.fillRect(0, 0, maxW, 50);
			g.setColor(Color.white);
			g.drawString("Error Occured. No Images listed were below the maximum width allowed in the generation settings. Please check settings/images and try again.", 25, 25);
			
			//Save the image
			try {
				ImageIO.write(gen, "png", new File(output));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Shifts every rectangle up as far as possible without going (y < 0) and without intersecting
	 * any other rectangles, but will allow intersections by a preset amount of pixels on the x coordinates.
	 */
	private void shiftRectanglesUp(ArrayList<ImageRectangle> rects) {
		int allowedError = 5; //2 pixel on each side
		
		for (ImageRectangle e : rects) {
			//int test = e.getY();
			boolean collision = false;
			while (!collision) {
				e.setY(e.getY() - 1);
				
				if (e.getY() < 0) {
					//Moved out of bounds
					collision = true;
				}
				else {
					int i = 0;
					while ((i < rects.size()) && !collision) {
						//Loop through all rects to check for intersection, ignore current e
						if (!(e.equals(rects.get(i)))) {
							//Create copies with slight pixel offsets to account for error.
							ImageRectangle erect = e.copy();
							erect.setX(erect.getX() + allowedError);
							erect.setWidth(erect.getWidth() - (2 * allowedError));
							
							if (erect.intersects(rects.get(i))) {
								//System.out.println("Image #" + rects.indexOf(e) + " intersects Image#" + i);
								collision = true;
							}
						}
						i++;
					}
				}
			}
			
			e.setY(e.getY() + 1);
			//System.out.println("Image #" + rects.indexOf(e) + " shifted " + (test - e.getY()) + " pixels up.");
		}
	}
	
	/**
	 * Returns the maximum height of the rectangles
	 */
	private int findRectanglesMaxHeight(ArrayList<ImageRectangle> rects) {
		int maxHeight = 0;
		for (ImageRectangle e : rects) {
			if ((e.getY() + e.getHeight()) > maxHeight) {
				maxHeight = e.getY() + e.getHeight();
			}
		}
		
		return maxHeight;
	}
}
