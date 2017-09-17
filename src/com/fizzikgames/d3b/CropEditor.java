package com.fizzikgames.d3b;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.SwingConstants;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.UIManager;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * The Crop Editor is the editor window that allows for a crop selection when using manual crop.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class CropEditor extends JFrame {
	private static final long serialVersionUID = -6073821392152740068L;

	private JPanel contentPane;
	
	private ImageProcessor imgpro;
	private int imageindex;
	private BufferedImage image;
	
	private Shell shell;
	private Text textLeft;
	private Text textTop;
	private Text textRight;
	private Text textBot;

	/**
	 * Create the frame.
	 */
	public CropEditor(Shell sh, ImageProcessor pro, int imgindex, Text tl, Text tt, Text tr, Text tb) {
		final JFrame frame = this;
		
		setType(Type.UTILITY);
		setTitle("Press Enter to finalize crop selection.");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		shell = sh;
		imgpro = pro;
		imageindex = imgindex;
		image = imgpro.getBufferedImage(imageindex);
		textLeft = tl;
		textTop = tt;
		textRight = tr;
		textBot = tb;
		
		final Cursor blankCursor = frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null");
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		
		setBounds(0, 0, (int) Math.min(image.getWidth() + 100, screen.getWidth() - 50), (int) Math.min(image.getHeight() + 100, screen.getHeight() - 50));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBackground(UIManager.getColor("InternalFrame.inactiveTitleGradient"));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		scrollPane.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		scrollPane.getVerticalScrollBar().setUnitIncrement(25);
		contentPane.add(scrollPane);
		
		JPanel scrollPanel = new JPanel();
		scrollPanel.setBackground(UIManager.getColor("Label.background"));
		scrollPanel.setAutoscrolls(true);
		scrollPanel.setPreferredSize(new Dimension(image.getWidth() + 50, image.getHeight() + 50));
		scrollPane.setViewportView(scrollPanel);
		
		final CropEditorImage imageLabel = new CropEditorImage(image, imgpro.getImageCropSettings(imageindex));
		imageLabel.setVerticalAlignment(SwingConstants.CENTER);
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		imageLabel.setAutoscrolls(true);
		imageLabel.setOpaque(true);
		imageLabel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		scrollPanel.add(imageLabel);
		
		//this.setBounds(image.getWidth() / 4, image.getHeight() / 4, image.getWidth(), image.getHeight());
		
		//Listeners
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					shell.getDisplay().syncExec(new Runnable() {
						public void run() {
							try {
								//Close window and save the current crop settings
								CropSettings cs = imageLabel.getCropSettings();
								CropSettings static_cs = imgpro.getImageCropSettings(imageindex);
								
								static_cs.setX(cs.getX());
								static_cs.setY(cs.getY());
								static_cs.setWidth(cs.getWidth());
								static_cs.setHeight(cs.getHeight());
								
								textLeft.setText("" + cs.getX());
								textTop.setText("" + cs.getY());
								textRight.setText("" + (cs.getX() + cs.getWidth()));
								textBot.setText("" + (cs.getY() + cs.getHeight()));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					
					frame.dispose();
				}
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					//Close window without saving current crop settings
					frame.dispose();
				}
			}
		});
		imageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					imageLabel.setLeftMouseDown(true);
					imageLabel.setCurrentSelection(imageLabel.getSelection(e.getX(), e.getY()));
					if (imageLabel.getCurrentSelection() >= 0) imageLabel.setCursor(blankCursor);
					imageLabel.repaint();
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					imageLabel.setLeftMouseDown(false);
					imageLabel.setCurrentSelection(-1);
					displayCursor(imageLabel, imageLabel.getSelection(e.getX(), e.getY()));	
					imageLabel.repaint();
				}
			}
		});
		imageLabel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (imageLabel.getLeftMouseDown()) {
					imageLabel.moveSelection(imageLabel.getCurrentSelection(), e.getX(), e.getY());
				}
			}
			@Override
			public void mouseMoved(MouseEvent e) {
				if (!imageLabel.getLeftMouseDown()) {
					displayCursor(imageLabel, imageLabel.getSelection(e.getX(), e.getY()));					
				}
			}
		});
	}
	
	private void displayCursor(CropEditorImage imageLabel, int index) {
		if (index >= 0) {
			switch (index) {
				case CropEditorImage.sel_topLeft: {
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
					break;
				}
				case CropEditorImage.sel_top: {
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					break;
				}
				case CropEditorImage.sel_topRight: {
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
					break;
				}
				case CropEditorImage.sel_midLeft: {
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
					break;
				}
				case CropEditorImage.sel_mid: {
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					break;
				}
				case CropEditorImage.sel_midRight: {
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					break;
				}
				case CropEditorImage.sel_botLeft: {
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
					break;
				}
				case CropEditorImage.sel_bot: {
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
					break;
				}
				case CropEditorImage.sel_botRight: {
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
					break;
				}
			}
		}
		else {
			imageLabel.setCursor(Cursor.getDefaultCursor());
		}
	}
}
