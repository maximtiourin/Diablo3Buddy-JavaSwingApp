package com.fizzikgames.d3b.utility;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * The autocrop util handles the creation and finding of image data for use with autocropping.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class AutoCropUtil {
	protected class MatrixPoint {
		private int row;
		private int column;
		
		public MatrixPoint(int r, int c) {
			row = r;
			column = c;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getColumn() {
			return column;
		}

		public void setColumn(int column) {
			this.column = column;
		}
	}
	protected class MatrixRectangle {
		private int row;
		private int column;
		private int height;
		private int width;
		
		public MatrixRectangle(int r, int c, int h, int w) {
			row = r;
			column = c;
			height = h;
			width = w;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getColumn() {
			return column;
		}

		public void setColumn(int column) {
			this.column = column;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}		
	}
	protected class PixelMatrixData {
		private int width;
		private int height;
		private int matrix[][];
		
		public PixelMatrixData(int w, int h, int m[][]) {
			width = w;
			height = h;
			matrix = m;
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

		public int[][] getMatrix() {
			return matrix;
		}

		public void setMatrix(int[][] matrix) {
			this.matrix = matrix;
		}
	}
	
	//private static final String samplePath = "data/autocrop/samples/1920x1080.png";
	private static final String samplePath_topleft[] = {"data/autocrop/samples/1920x1080_topleft_a.png",
														"data/autocrop/samples/1920x1080_topleft_b.png", 
														"data/autocrop/samples/1920x1080_topleft_c.png",
														"data/autocrop/samples/1920x1080_topleft_d.png", 
														"data/autocrop/samples/1920x1080_topleft_e.png", 
														"data/autocrop/samples/1920x1080_topleft_f.png", 
														"data/autocrop/samples/1920x1080_topleft_g.png", 
														"data/autocrop/samples/1920x1080_topleft_h.png"};
	private static final String samplePath_topright[] = {"data/autocrop/samples/1920x1080_topright_a.png",
														 "data/autocrop/samples/1920x1080_topright_b.png", 
														 "data/autocrop/samples/1920x1080_topright_c.png",
														 "data/autocrop/samples/1920x1080_topright_d.png", 
														 "data/autocrop/samples/1920x1080_topright_e.png", 
														 "data/autocrop/samples/1920x1080_topright_f.png", 
														 "data/autocrop/samples/1920x1080_topright_g.png", 
														 "data/autocrop/samples/1920x1080_topright_h.png"};
	private static final String samplePath_botleft[] = {"data/autocrop/samples/1920x1080_botleft_a.png", 
														"data/autocrop/samples/1920x1080_botleft_b.png", 
														"data/autocrop/samples/1920x1080_botleft_c.png",
														"data/autocrop/samples/1920x1080_botleft_d.png", 
														"data/autocrop/samples/1920x1080_botleft_e.png", 
														"data/autocrop/samples/1920x1080_botleft_f.png", 
														"data/autocrop/samples/1920x1080_botleft_g.png", 
														"data/autocrop/samples/1920x1080_botleft_h.png"};
	private static final String samplePath_botright[] = {"data/autocrop/samples/1920x1080_botright_a.png", 
														 "data/autocrop/samples/1920x1080_botright_b.png", 
														 "data/autocrop/samples/1920x1080_botright_c.png",
														 "data/autocrop/samples/1920x1080_botright_d.png", 
														 "data/autocrop/samples/1920x1080_botright_e.png", 
														 "data/autocrop/samples/1920x1080_botright_f.png", 
														 "data/autocrop/samples/1920x1080_botright_g.png", 
														 "data/autocrop/samples/1920x1080_botright_h.png"};
	private static final String structPath_topleft[] = {"data/autocrop/structs/1920x1080_topleft_a.struct", 
														"data/autocrop/structs/1920x1080_topleft_b.struct", 
														"data/autocrop/structs/1920x1080_topleft_c.struct",
														"data/autocrop/structs/1920x1080_topleft_d.struct", 
														"data/autocrop/structs/1920x1080_topleft_e.struct", 
														"data/autocrop/structs/1920x1080_topleft_f.struct", 
														"data/autocrop/structs/1920x1080_topleft_g.struct", 
														"data/autocrop/structs/1920x1080_topleft_h.struct"};
	private static final String structPath_topright[] = {"data/autocrop/structs/1920x1080_topright_a.struct",
														 "data/autocrop/structs/1920x1080_topright_b.struct", 
														 "data/autocrop/structs/1920x1080_topright_c.struct", 
														 "data/autocrop/structs/1920x1080_topright_d.struct", 
														 "data/autocrop/structs/1920x1080_topright_e.struct", 
														 "data/autocrop/structs/1920x1080_topright_f.struct", 
														 "data/autocrop/structs/1920x1080_topright_g.struct", 
														 "data/autocrop/structs/1920x1080_topright_h.struct"};
	private static final String structPath_botleft[] = {"data/autocrop/structs/1920x1080_botleft_a.struct",
														"data/autocrop/structs/1920x1080_botleft_b.struct", 
														"data/autocrop/structs/1920x1080_botleft_c.struct", 
														"data/autocrop/structs/1920x1080_botleft_d.struct", 
														"data/autocrop/structs/1920x1080_botleft_e.struct", 
														"data/autocrop/structs/1920x1080_botleft_f.struct", 
														"data/autocrop/structs/1920x1080_botleft_g.struct", 
														"data/autocrop/structs/1920x1080_botleft_h.struct"};
	private static final String structPath_botright[] = {"data/autocrop/structs/1920x1080_botright_a.struct",
														 "data/autocrop/structs/1920x1080_botright_b.struct", 
														 "data/autocrop/structs/1920x1080_botright_c.struct", 
														 "data/autocrop/structs/1920x1080_botright_d.struct", 
														 "data/autocrop/structs/1920x1080_botright_e.struct", 
														 "data/autocrop/structs/1920x1080_botright_f.struct", 
														 "data/autocrop/structs/1920x1080_botright_g.struct", 
														 "data/autocrop/structs/1920x1080_botright_h.struct"};
	private static final int topLeftCount = 8;
	private static final int topRightCount = 8;
	private static final int botLeftCount = 8;
	private static final int botRightCount = 8;
	private ArrayList<PixelMatrixData> pmd_topleft;
	private ArrayList<PixelMatrixData> pmd_topright;
	private ArrayList<PixelMatrixData> pmd_botleft;
	private ArrayList<PixelMatrixData> pmd_botright;
	private static final int topleft = 0;
	private static final int topright = 1;
	private static final int botleft = 2;
	private static final int botright = 3;
	
	public AutoCropUtil() {
		ensureSampleData();
		
		//Init matrix data
		pmd_topleft = new ArrayList<PixelMatrixData>();
		pmd_topright = new ArrayList<PixelMatrixData>();
		pmd_botleft = new ArrayList<PixelMatrixData>();
		pmd_botright = new ArrayList<PixelMatrixData>();
		for (int i = 0; i < topLeftCount; i++) {
			pmd_topleft.add(convertStringToPixelMatrix(readSampleData(new File(structPath_topleft[i]))));
		}
		for (int i = 0; i < topRightCount; i++) {
			pmd_topright.add(convertStringToPixelMatrix(readSampleData(new File(structPath_topright[i]))));
		}
		for (int i = 0; i < botLeftCount; i++) {
			pmd_botleft.add(convertStringToPixelMatrix(readSampleData(new File(structPath_botleft[i]))));
		}
		for (int i = 0; i < botRightCount; i++) {
			pmd_botright.add(convertStringToPixelMatrix(readSampleData(new File(structPath_botright[i]))));
		}
	}
	
	/**
	 * Finds the area to crop using struct data of all 4 corners in the bufferedImage, and returns a rectangle of the area.
	 * Search first for the top left corner, then does a refined search using that corner as 0,0 for the other corners.
	 * Returns a Rectangle with the dimensions of the entire image if no smaller area could be found.
	 */
	public Rectangle findCropArea(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		
		//Find topleft corner and then reduce the problem.
		//System.out.println("Starting");
		MatrixPoint mp_topleft = findSubImage(img, pmd_topleft, topLeftCount, new MatrixRectangle(0, 0, height, width));
		MatrixPoint mp_topright = null;
		MatrixPoint mp_botleft = null;
		MatrixPoint mp_botright = null;
		if (mp_topleft != null) {
			//System.out.println("Topleft check.");
			//Find top Right Corner and then reduce
			mp_topright = findSubImage(img, pmd_topright, topRightCount, new MatrixRectangle(mp_topleft.getRow(), mp_topleft.getColumn(), pmd_topright.get(0).getHeight(), width - mp_topleft.getColumn()));
			if (mp_topright != null) {
				//System.out.println("Topright check.");
				//Find bot Left Corner and then Reduce
				mp_botleft = findSubImage(img, pmd_botleft, botLeftCount, new MatrixRectangle(mp_topleft.getRow(), mp_topleft.getColumn(), height - mp_topleft.getRow(), pmd_botleft.get(0).getWidth()));
				if (mp_botleft != null) {
					//System.out.println("Botleft check.");
					//Find bot Right Corner and Finish
					mp_botright = findSubImage(img, pmd_botright, botRightCount, new MatrixRectangle(mp_botleft.getRow(), mp_topright.getColumn(), pmd_botright.get(0).getHeight(), pmd_botright.get(0).getWidth()));
					if (mp_botright != null) {
						//System.out.println("Botright check.");
						/*System.out.println("Top Left (" + mp_topleft.getColumn() + ", " + mp_topleft.getRow() + ")");
						System.out.println("Bot Right (" + (mp_botright.getColumn() + pmd_botright.getWidth()) + ", " + (mp_botright.getRow() + pmd_botright.getHeight()) + ")");*/
						return new Rectangle(mp_topleft.getColumn(), mp_topleft.getRow(), 
								(mp_botright.getColumn() + pmd_botright.get(0).getWidth()) - mp_topleft.getColumn(), 
								(mp_botright.getRow() + pmd_botright.get(0).getHeight()) - mp_topleft.getRow());
					}
				}
			}
		}
		
		return new Rectangle(0, 0, width, height);
	}
	
	/**
	 * Attempts to find the pixel data within a rectangular subset of the image and return the MatrixPoint of it's top left most corner.
	 * Returns null if no match is found.
	 */
	private MatrixPoint findSubImage(BufferedImage img, ArrayList<PixelMatrixData> pmd, int pmdCount, MatrixRectangle searchArea) {
		//Search for starting pixels that would meet initial condition.
		int r = searchArea.getRow();
		int c = searchArea.getColumn();
		while (r < (searchArea.getRow() + searchArea.getHeight())) {
			while (c < (searchArea.getColumn() + searchArea.getWidth())) {
				for (int i = 0; i < pmdCount; i++) {
					if (pmd.get(i).getMatrix()[0][0] == img.getRGB(c, r)) {
						//If a starting condition is met, attempt to match the rectangle
						int nr = 0;
						int nc = 0;
						boolean found = true;
						while ((nr < pmd.get(i).getHeight()) && found) {
							while ((nc < pmd.get(i).getWidth()) && found) {
								//First check that height and width are within bounds
								if (r + pmd.get(i).getHeight() > (searchArea.getRow() + searchArea.getHeight())) {
									found = false;
								}
								if (c + pmd.get(i).getWidth() > (searchArea.getColumn() + searchArea.getWidth())) {
									found = false;
								}
								
								//Check the data pixels against the img pixels
								if (pmd.get(i).getMatrix()[nr][nc] != img.getRGB(c + nc, r + nr)) {
									//System.out.println("Pixel Search Failed for #" + i + " at " + (r + nr) + ", " + (c + nc) + " starting from position " + r + ", " + c);
									found = false;
								}
								
								nc++;
							}
							nc = 0;
							nr++;
						}
						
						//If found, return the starting point
						if (found) return new MatrixPoint(r, c);
					}
				}
				
				c++;
			}
			c = searchArea.getColumn();
			r++;
		}
		
		return null;
	}
	
	/**
	 * Converts a string with pixel data into a pixel matrix
	 */
	private PixelMatrixData convertStringToPixelMatrix(String s) {
		String chop = s;
		String trim[];
		String seperator = " ";
		
		//Get Width
		trim = StringUtil.trimSubstring(chop, seperator, true);
		chop = trim[1];
		
		int width = Integer.valueOf(trim[0]);
		
		//Get Height
		trim = StringUtil.trimSubstring(chop, seperator, true);
		chop = trim[1];
		
		int height = Integer.valueOf(trim[0]);
		
		//Fill Pixel Matrix
		int matrix[][] = new int[height][width];
		
		int r = 0;
		int c = 0;
		
		while (r < height) {
			while (c < width) {
				trim = StringUtil.trimSubstring(chop, seperator, true);
				chop = trim[1];
				
				matrix[r][c] = Integer.valueOf(trim[0]);
				
				c++;
			}
			r++;
			c = 0;
		}
		
		return new PixelMatrixData(width, height, matrix);
	}
	
	/**
	 * Makes sure sample structs exist for use in autocropping
	 */
	private void ensureSampleData() {
		File file;
		
		for (int i = 0; i < topLeftCount; i++) {
			file = new File(structPath_topleft[i]);
			
			if (!file.exists()) {
				createSampleData(topleft, i, file);
			}
		}
		for (int i = 0; i < topRightCount; i++) {
			file = new File(structPath_topright[i]);
			
			if (!file.exists()) {
				createSampleData(topright, i, file);
			}
		}
		for (int i = 0; i < botLeftCount; i++) {
			file = new File(structPath_botleft[i]);
			
			if (!file.exists()) {
				createSampleData(botleft, i, file);
			}
		}
		for (int i = 0; i < botRightCount; i++) {
			file = new File(structPath_botright[i]);
			
			if (!file.exists()) {
				createSampleData(botright, i, file);
			}
		}
	}
	
	/**
	 * Creates sample data
	 */
	private void createSampleData(int index, int type, File f) {
		try {
			BufferedImage img = null;
			switch (index) {
				case topleft: {
					img = ImageIO.read(new File(samplePath_topleft[type]));
					break;
				}
				case topright: {
					img = ImageIO.read(new File(samplePath_topright[type]));
					break;
				}
				case botleft: {
					img = ImageIO.read(new File(samplePath_botleft[type]));
					break;
				}
				case botright: {
					img = ImageIO.read(new File(samplePath_botright[type]));
					break;
				}
			}
			
			int width = img.getWidth();
			int height = img.getHeight();
			int pixels[][] = new int[height][width];
			
			//Get pixel values
			for (int r = 0; r < height; r++) {
				for (int c = 0; c < width; c++) {
					pixels[r][c] = img.getRGB(c, r);
				}
			}
			
			//Write struct
			f.createNewFile();
			FileOutputStream out = new FileOutputStream(f, false);
			String data = "";
				//width height info
			data += width + " " + height + " ";
				//pixel info
			for (int r = 0; r < height; r++) {
				for (int c = 0; c < width; c++) {
					data += pixels[r][c] + " ";
				}
			}
				//write info
			out.write(data.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads sample data from the struct file into a string.
	 */
	private String readSampleData(File f) {
		try {
			String data = "";
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader in = new BufferedReader(isr);
			
			String line = "";
			while ((line = in.readLine()) != null) {
				data += line;
			}
			
			return data;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
