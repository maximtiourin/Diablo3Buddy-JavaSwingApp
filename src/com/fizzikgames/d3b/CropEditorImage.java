package com.fizzikgames.d3b;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JLabel;

/**
 * The Crop editor image is a JLabel that displays the image to be cropped, as well as the mutable visual crop rectangle
 * that helps the user select the proper area to crop.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class CropEditorImage extends JLabel {
	private static final long serialVersionUID = 8113655472321308697L;
	private BufferedImage image;
	private CropSettings settings;
	private boolean leftMouseDown;
	private int currentSelection;
	private static final Color c1 = new Color(57, 167, 212, 100); //Teal
	private static final Color c2 = new Color(25, 215, 25, 150); //Greenish
	private static final Color c3 = new Color(215, 25, 25, 150); //Redish
	private static final Color c4 = new Color(75, 75, 215, 150); //Blueish
	private static final Color c5 = new Color(236, 217, 255, 180); //Yellowish
	private ArrayList<Point> selections;
	private int selectLength;
	private static final int minSelectLength = 12;
	private static final int maxSelectLength = 72;
	private static final int minWidth = 38;
	private static final int minHeight = 38;
	public static final int sel_topLeft = 	0;
	public static final int sel_top = 		1;
	public static final int sel_topRight = 	2;
	public static final int sel_midLeft = 	3;
	public static final int sel_mid = 		4;
	public static final int sel_midRight = 	5;
	public static final int sel_botLeft = 	6;
	public static final int sel_bot = 		7;
	public static final int sel_botRight = 	8;	
	
	public CropEditorImage(BufferedImage image, CropSettings stngs) {
		super("");
		this.image = image;
		this.settings = stngs;
		this.leftMouseDown = false;
		this.currentSelection = -1;
		this.selectLength = maxSelectLength;
		
		//Initialize Selections
		selections = new ArrayList<Point>();
		selections.add(new Point(settings.getX(), settings.getY()));															//0
		selections.add(new Point(settings.getX() + (settings.getWidth() / 2), settings.getY()));								//1
		selections.add(new Point(settings.getX() + settings.getWidth(), settings.getY()));										//2
		selections.add(new Point(settings.getX(), settings.getY() + (settings.getHeight() / 2)));								//3
		selections.add(new Point(settings.getX() + (settings.getWidth() / 2), settings.getY() + (settings.getHeight() / 2)));	//4
		selections.add(new Point(settings.getX() + settings.getWidth(), settings.getY() + (settings.getHeight() / 2)));			//5
		selections.add(new Point(settings.getX(), settings.getY() + settings.getHeight()));										//6
		selections.add(new Point(settings.getX() + (settings.getWidth() / 2), settings.getY() + settings.getHeight()));			//7
		selections.add(new Point(settings.getX() + settings.getWidth(), settings.getY() + settings.getHeight()));				//8
	}
	
	@Override
	public void paint(Graphics g) {		
		Graphics2D g2 = (Graphics2D) g;
		Point tl = selections.get(sel_topLeft);
		Point br = selections.get(sel_botRight);
		
		//Calculate Select Length Ratio
		double imageArea = getWidth() * getHeight();
		double cropArea = (int) ((br.getX() - tl.getX()) * (br.getY() - tl.getY()));
		selectLength = (int) Math.max(minSelectLength, (int) ((double) maxSelectLength / (imageArea / cropArea)));
		selectLength = (int) Math.min(selectLength, getWidth() / 6);
		selectLength = (int) Math.min(selectLength, getHeight() / 6);
		
		int d = selectLength / 2;
		
		//Image
		g2.drawImage(image, null, 0, 0);
		
		//Bounding area
		g2.setColor(c1);
		g2.fillRect((int) tl.getX(), (int) tl.getY(), (int) (br.getX() - tl.getX()), (int) (br.getY() - tl.getY()));
		g2.setColor(c3);
		g2.drawRect((int) tl.getX(), (int) tl.getY(), (int) (br.getX() - tl.getX()), (int) (br.getY() - tl.getY()));
		
		//Border selections
		for (int i = 0; i < selections.size(); i++) {
			Point e = selections.get(i);			
			g2.setColor(Color.black);
			g2.drawRect((int) e.getX() - d, (int) e.getY() - d, selectLength, selectLength);
			if (i == currentSelection) {
				g2.setColor(c5);
			}
			else {
				if (i % 2 == 0) {
					if (i == sel_mid) g2.setColor(c2);
					else g2.setColor(c4);
				}
				else {
					g2.setColor(c3);
				}
			}
			g2.fillRect((int) e.getX() - d, (int) e.getY() - d, selectLength, selectLength);
		}
	}
	
	/**
	 * Returns the index of a selection at the coordinates if it is located there, otherwise -1 for no selection
	 */
	public int getSelection(int x, int y) {
		for (int i = 0; i < selections.size(); i++) {
			Point p = selections.get(i);
			int d = selectLength / 2;
			int px = (int) p.getX() - d;
			int py = (int) p.getY() - d;			
			
			if (((x >= px) && (x <= px + selectLength)) && ((y >= py) && (y <= py + selectLength))) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Moves the selection with the given index to the new position, using certain constraints.
	 */
	public boolean moveSelection(int index, int x, int y) {
		if ((index < 0) || (index >= selections.size())) return false;
		
		Point p = selections.get(index);
		Point s1;
		Point s2;
		
		switch (index) {
			case sel_topLeft: {				
				//Check Horizontal Bounds
				s1 = selections.get(sel_botRight);
				p.setLocation(Math.min(x, s1.getX() - minWidth), p.getY());
				p.setLocation(Math.max(0, p.getX()), p.getY());
				
				//Check Vertical Bounds
				s1 = selections.get(sel_botRight);
				p.setLocation(p.getX(), Math.min(y, s1.getY() - minHeight));
				p.setLocation(p.getX(), Math.max(0, p.getY()));
				
				//Update Relevant Positions
				s1 = selections.get(sel_botLeft);
				s1.setLocation(p.getX(), s1.getY());
				s1 = selections.get(sel_topRight);
				s1.setLocation(s1.getX(), p.getY());
				
				
				s1 = selections.get(sel_top);
				s2 = selections.get(sel_botRight);
				s1.setLocation(p.getX() + ((s2.getX() - p.getX()) / 2), p.getY());
				s1 = selections.get(sel_bot);
				s2 = selections.get(sel_botRight);
				s1.setLocation(p.getX() + ((s2.getX() - p.getX()) / 2), s1.getY());
				
				s1 = selections.get(sel_midLeft);
				s2 = selections.get(sel_botRight);
				s1.setLocation(p.getX(), p.getY() + ((s2.getY() - p.getY()) / 2));
				s1 = selections.get(sel_mid);
				s2 = selections.get(sel_botRight);
				s1.setLocation(p.getX() + ((s2.getX() - p.getX()) / 2), p.getY() + ((s2.getY() - p.getY()) / 2));
				s1 = selections.get(sel_midRight);
				s2 = selections.get(sel_botRight);
				s1.setLocation(s1.getX(), p.getY() + ((s2.getY() - p.getY()) / 2));
				
				break;
			}
			case sel_top: {		
				//Check Vertical Bounds
				s1 = selections.get(sel_botRight);
				p.setLocation(p.getX(), Math.min(y, s1.getY() - minHeight));
				p.setLocation(p.getX(), Math.max(0, p.getY()));
				
				//Update Relevant Positions
				s1 = selections.get(sel_topLeft);
				s1.setLocation(s1.getX(), p.getY());
				s1 = selections.get(sel_topRight);
				s1.setLocation(s1.getX(), p.getY());
				
				
				s1 = selections.get(sel_midLeft);
				s2 = selections.get(sel_botRight);
				s1.setLocation(s1.getX(), p.getY() + ((s2.getY() - p.getY()) / 2));
				s1 = selections.get(sel_mid);
				s2 = selections.get(sel_botRight);
				s1.setLocation(s1.getX(), p.getY() + ((s2.getY() - p.getY()) / 2));
				s1 = selections.get(sel_midRight);
				s2 = selections.get(sel_botRight);
				s1.setLocation(s1.getX(), p.getY() + ((s2.getY() - p.getY()) / 2));
				
				break;
			}
			case sel_topRight: {
				//Check Horizontal Bounds
				s1 = selections.get(sel_botLeft);
				p.setLocation(Math.max(x, s1.getX() + minWidth), p.getY());
				p.setLocation(Math.min(this.getWidth(), p.getX()), p.getY());
				
				//Check Vertical Bounds
				s1 = selections.get(sel_botLeft);
				p.setLocation(p.getX(), Math.min(y, s1.getY() - minHeight));
				p.setLocation(p.getX(), Math.max(0, p.getY()));
				
				//Update Relevant Positions
				s1 = selections.get(sel_topLeft);
				s1.setLocation(s1.getX(), p.getY());
				s1 = selections.get(sel_botRight);
				s1.setLocation(p.getX(), s1.getY());
				
				
				s1 = selections.get(sel_top);
				s2 = selections.get(sel_botLeft);
				s1.setLocation(p.getX() - ((p.getX() - s2.getX()) / 2), p.getY());
				s1 = selections.get(sel_bot);
				s2 = selections.get(sel_botLeft);
				s1.setLocation(p.getX() - ((p.getX() - s2.getX()) / 2), s1.getY());
				
				s1 = selections.get(sel_midLeft);
				s2 = selections.get(sel_botLeft);
				s1.setLocation(s1.getX(), p.getY() + ((s2.getY() - p.getY()) / 2));
				s1 = selections.get(sel_mid);
				s2 = selections.get(sel_botLeft);
				s1.setLocation(p.getX() - ((p.getX() - s2.getX()) / 2), p.getY() + ((s2.getY() - p.getY()) / 2));
				s1 = selections.get(sel_midRight);
				s2 = selections.get(sel_botLeft);
				s1.setLocation(p.getX(), p.getY() + ((s2.getY() - p.getY()) / 2));
				
				break;
			}
			case sel_midLeft: {
				//Check Horizontal Bounds
				s1 = selections.get(sel_botRight);
				p.setLocation(Math.min(x, s1.getX() - minWidth), p.getY());
				p.setLocation(Math.max(0, p.getX()), p.getY());				
				
				//Update Relevant Positions
				s1 = selections.get(sel_topLeft);
				s1.setLocation(p.getX(), s1.getY());
				s1 = selections.get(sel_botLeft);
				s1.setLocation(p.getX(), s1.getY());				
				
				s1 = selections.get(sel_top);
				s2 = selections.get(sel_botRight);
				s1.setLocation(p.getX() + ((s2.getX() - p.getX()) / 2), s1.getY());
				s1 = selections.get(sel_bot);
				s2 = selections.get(sel_botRight);
				s1.setLocation(p.getX() + ((s2.getX() - p.getX()) / 2), s1.getY());
				
				s1 = selections.get(sel_mid);
				s2 = selections.get(sel_botRight);
				s1.setLocation(p.getX() + ((s2.getX() - p.getX()) / 2), s1.getY());
				
				break;
			}
			case sel_mid: {
				s1 = selections.get(sel_topLeft);
				s2 = selections.get(sel_botRight);
				int oldx = (int) p.getX();
				int oldy = (int) p.getY();
				
				//Get width and height bounds
				int cropWidth = (int) (s2.getX() - s1.getX());
				int cropHeight = (int) (s2.getY() - s1.getY());
				int whalf = cropWidth / 2;
				int hhalf = cropHeight / 2;
				
				//Check Horizontal Bounds
				p.setLocation(Math.max(x, whalf), p.getY());
				p.setLocation(Math.min(getWidth() - whalf, p.getX()), p.getY());
				
				//Check Vertical Bounds
				p.setLocation(p.getX(), Math.max(y, hhalf));
				p.setLocation(p.getX(), Math.min(getHeight() - hhalf, p.getY()));
				
				
				//Update Relevant Positions (with simple translation)
				int dx = ((int) p.getX()) - oldx;
				int dy = ((int) p.getY()) - oldy;
				s1 = selections.get(sel_topLeft);
				s1.setLocation(s1.getX() + dx, s1.getY() + dy);
				s1 = selections.get(sel_top);
				s1.setLocation(s1.getX() + dx, s1.getY() + dy);
				s1 = selections.get(sel_topRight);
				s1.setLocation(s1.getX() + dx, s1.getY() + dy);
				s1 = selections.get(sel_midLeft);
				s1.setLocation(s1.getX() + dx, s1.getY() + dy);
				s1 = selections.get(sel_midRight);
				s1.setLocation(s1.getX() + dx, s1.getY() + dy);
				s1 = selections.get(sel_botLeft);
				s1.setLocation(s1.getX() + dx, s1.getY() + dy);
				s1 = selections.get(sel_bot);
				s1.setLocation(s1.getX() + dx, s1.getY() + dy);
				s1 = selections.get(sel_botRight);
				s1.setLocation(s1.getX() + dx, s1.getY() + dy);
				
				break;
			}
			case sel_midRight: {
				//Check Horizontal Bounds
				s1 = selections.get(sel_botLeft);
				p.setLocation(Math.max(x, s1.getX() + minWidth), p.getY());
				p.setLocation(Math.min(this.getWidth(), p.getX()), p.getY());
				
				//Update Relevant Positions
				s1 = selections.get(sel_topRight);
				s1.setLocation(p.getX(), s1.getY());
				s1 = selections.get(sel_botRight);
				s1.setLocation(p.getX(), s1.getY());
				
				
				s1 = selections.get(sel_top);
				s2 = selections.get(sel_botLeft);
				s1.setLocation(p.getX() - ((p.getX() - s2.getX()) / 2), s1.getY());
				s1 = selections.get(sel_bot);
				s2 = selections.get(sel_botLeft);
				s1.setLocation(p.getX() - ((p.getX() - s2.getX()) / 2), s1.getY());
				
				s1 = selections.get(sel_mid);
				s2 = selections.get(sel_botLeft);
				s1.setLocation(p.getX() - ((p.getX() - s2.getX()) / 2), s1.getY());
				
				break;
			}
			case sel_botLeft: {
				//Check Horizontal Bounds
				s1 = selections.get(sel_topRight);
				p.setLocation(Math.min(x, s1.getX() - minWidth), p.getY());
				p.setLocation(Math.max(0, p.getX()), p.getY());
				
				//Check Vertical Bounds
				s1 = selections.get(sel_topRight);
				p.setLocation(p.getX(), Math.max(y, s1.getY() + minHeight));
				p.setLocation(p.getX(), Math.min(getHeight(), p.getY()));
				
				//Update Relevant Positions
				s1 = selections.get(sel_topLeft);
				s1.setLocation(p.getX(), s1.getY());
				s1 = selections.get(sel_botRight);
				s1.setLocation(s1.getX(), p.getY());
				
				
				s1 = selections.get(sel_top);
				s2 = selections.get(sel_topRight);
				s1.setLocation(p.getX() + ((s2.getX() - p.getX()) / 2), s1.getY());
				s1 = selections.get(sel_bot);
				s2 = selections.get(sel_topRight);
				s1.setLocation(p.getX() + ((s2.getX() - p.getX()) / 2), p.getY());
				
				s1 = selections.get(sel_midLeft);
				s2 = selections.get(sel_topRight);
				s1.setLocation(p.getX(), p.getY() - ((p.getY() - s2.getY()) / 2));
				s1 = selections.get(sel_mid);
				s2 = selections.get(sel_topRight);
				s1.setLocation(p.getX() + ((s2.getX() - p.getX()) / 2), p.getY() - ((p.getY() - s2.getY()) / 2));
				s1 = selections.get(sel_midRight);
				s2 = selections.get(sel_topRight);
				s1.setLocation(s1.getX(), p.getY() - ((p.getY() - s2.getY()) / 2));
				
				break;
			}
			case sel_bot: {		
				//Check Vertical Bounds
				s1 = selections.get(sel_topRight);
				p.setLocation(p.getX(), Math.max(y, s1.getY() + minHeight));
				p.setLocation(p.getX(), Math.min(getHeight(), p.getY()));
				
				//Update Relevant Positions
				s1 = selections.get(sel_botLeft);
				s1.setLocation(s1.getX(), p.getY());
				s1 = selections.get(sel_botRight);
				s1.setLocation(s1.getX(), p.getY());
				
				
				s1 = selections.get(sel_midLeft);
				s2 = selections.get(sel_topRight);
				s1.setLocation(s1.getX(), p.getY() - ((p.getY() - s2.getY()) / 2));
				s1 = selections.get(sel_mid);
				s2 = selections.get(sel_topRight);
				s1.setLocation(s1.getX(), p.getY() - ((p.getY() - s2.getY()) / 2));
				s1 = selections.get(sel_midRight);
				s2 = selections.get(sel_topRight);
				s1.setLocation(s1.getX(), p.getY() - ((p.getY() - s2.getY()) / 2));
				
				break;
			}
			case sel_botRight: {
				//Check Horizontal Bounds
				s1 = selections.get(sel_topLeft);
				p.setLocation(Math.max(x, s1.getX() + minWidth), p.getY());
				p.setLocation(Math.min(getWidth(), p.getX()), p.getY());
				
				//Check Vertical Bounds
				s1 = selections.get(sel_topLeft);
				p.setLocation(p.getX(), Math.max(y, s1.getY() + minHeight));
				p.setLocation(p.getX(), Math.min(getHeight(), p.getY()));
				
				//Update Relevant Positions
				s1 = selections.get(sel_botLeft);
				s1.setLocation(s1.getX(), p.getY());
				s1 = selections.get(sel_topRight);
				s1.setLocation(p.getX(), s1.getY());
				
				
				s1 = selections.get(sel_top);
				s2 = selections.get(sel_topLeft);
				s1.setLocation(p.getX() - ((p.getX() - s2.getX()) / 2), s1.getY());
				s1 = selections.get(sel_bot);
				s2 = selections.get(sel_topLeft);
				s1.setLocation(p.getX() - ((p.getX() - s2.getX()) / 2), p.getY());
				
				s1 = selections.get(sel_midLeft);
				s2 = selections.get(sel_topLeft);
				s1.setLocation(s1.getX(), p.getY() - ((p.getY() - s2.getY()) / 2));
				s1 = selections.get(sel_mid);
				s2 = selections.get(sel_topLeft);
				s1.setLocation(p.getX() - ((p.getX() - s2.getX()) / 2), p.getY() - ((p.getY() - s2.getY()) / 2));
				s1 = selections.get(sel_midRight);
				s2 = selections.get(sel_topLeft);
				s1.setLocation(p.getX(), p.getY() - ((p.getY() - s2.getY()) / 2));
				
				break;
			}
		}
		
		repaint();
		
		return true;
	}
	
	public int getCurrentSelection() {
		return currentSelection;
	}
	
	public void setCurrentSelection(int index) {
		currentSelection = index;
	}
	
	public boolean getLeftMouseDown() {
		return leftMouseDown;
	}
	
	public void setLeftMouseDown(boolean b) {
		leftMouseDown = b;
	}
	
	/**
	 * Returns a CropSettings object using the current crop rectangle of the image.
	 */
	public CropSettings getCropSettings() {
		Point s1 = selections.get(sel_topLeft);
		Point s2 = selections.get(sel_botRight);
		
		CropSettings cs = new CropSettings((int) (s2.getX() - s1.getX()), (int) (s2.getY() - s1.getY()));
		cs.setX((int) s1.getX());
		cs.setY((int) s1.getY());
		cs.setType(CropSettings.CROP_MANUAL);
		
		return cs;
	}
}
