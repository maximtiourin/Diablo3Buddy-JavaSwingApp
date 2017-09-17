package com.fizzikgames.d3b;

import java.awt.Component;
import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Button;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import swing2swt.layout.BoxLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import swing2swt.layout.FlowLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;

import de.ksquared.system.keyboard.GlobalKeyListener;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ProgressBar;

import com.fizzikgames.d3b.utility.StringUtil;
import org.eclipse.swt.widgets.Combo;

public class D3B {
	public static final String TITLE = "Diablo 3 Buddy";
	public static final String VERSION = "00.07.00";
	public static final String DEFAULT_HOTKEY = "121";
	public static final String DEFAULT_HOTKEY_TEXT = "F10";
	public static final int MIN_WINDOW_WIDTH = 800;
	public static final int MIN_WINDOW_HEIGHT = 600;
	public static int WINDOW_WIDTH = 800;
	public static int WINDOW_HEIGHT = 600;
	public static boolean MAXIMIZED = false;
	
	private DataBindingContext m_bindingContext;

	protected Shell shell;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected Button btnNoCrop;
	protected Button btnManualCrop;
	private Button btnAutomaticCrop;
	private List imageList;
	private TabFolder tabFolder_1;
	
	private ImageProcessor imgpro;
	
	//Initialized
	private boolean initialMaximize;
	//Screen Image Dragging
	private Image dragStartPointer;
	private boolean imageRightDragging;
	private int imageDragx;
	private int imageDragy;
	//Screen Grabbing
	private Text txtPrtScrn;
	private Button btnHotkeyEnabled;
	private String screenGrabKeycode;
	private Text textManualLeft;
	private Text textManualTop;
	private Text textManualRight;
	private Text textManualBot;
	
	////////////////////Auto fields
	private StyledText cropInfoText;
	private Button btnSetCrop;
	private Button btnSetAll;
	private Button btnGenerate;
	private ScrolledComposite scrolledComposite;
	private Label screenImageLabel;
	private Label lblImgDimensions;
	private Text txtAutocropInfo;
	private Text textGenPath;
	private Text textGenIndexingString;
	private Text text;
	private Text text_1;
	private Text text_2;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Config.get().init();
		
		WINDOW_WIDTH = Config.get().integerAt("Display Settings", "width");
		WINDOW_HEIGHT = Config.get().integerAt("Display Settings", "height");
		MAXIMIZED = Config.get().booleanAt("Display Settings", "maximized");
		
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					D3B window = new D3B();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Settings.get().init();
		
		initialMaximize = false;
		
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}		
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if (shell.getMaximized()) {
					MAXIMIZED = true;
				}
				else if (!shell.getMaximized() && MAXIMIZED) {
					if (initialMaximize) {
						shell.setBounds(250, 250, MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
					}
					MAXIMIZED = false;
				}
			}
		});
		shell.setMinimumSize(new Point(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
		shell.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		shell.setText(TITLE);
		shell.setModified(false);
		shell.setMaximized(MAXIMIZED);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		shell.setImages(new Image[] {new Image(shell.getDisplay(), "assets/images/ui/icons/logo_16.png"), new Image(shell.getDisplay(), "assets/images/ui/icons/logo_32.png")});
		
		initialMaximize = true;
		
		//Initialize other vars
		imgpro = new ImageProcessor(shell);
		imageRightDragging = false;
		imageDragx = 0;
		imageDragy = 0;
		screenGrabKeycode = Settings.screengrab_hotkey;
		/* Images */
			//UI
		ImageData dspdata = new ImageData("assets/images/ui/dragStartPointer.png");
		dspdata.transparentPixel = dspdata.palette.getPixel(new RGB(255, 255, 255));
		dragStartPointer = new Image(shell.getDisplay(), dspdata);
		//Endinit
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setFont(SWTResourceManager.getFont("Plantagenet Cherokee", 11, SWT.BOLD));
		
		TabItem tbtmDiabloBuddy = new TabItem(tabFolder, 0);
		tbtmDiabloBuddy.setText("Information");
		
		Composite composite_7 = new Composite(tabFolder, SWT.NONE);
		tbtmDiabloBuddy.setControl(composite_7);
		formToolkit.paintBordersFor(composite_7);
		composite_7.setLayout(new GridLayout(1, false));
		
		Group group_2 = new Group(composite_7, SWT.NONE);
		group_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		group_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.adapt(group_2);
		formToolkit.paintBordersFor(group_2);
		
		Label lblInfoLogo = new Label(group_2, SWT.NONE);
		lblInfoLogo.setAlignment(SWT.CENTER);
		lblInfoLogo.setImage(new Image(shell.getDisplay(), "assets/images/art/logowithblankbg.png"));
		formToolkit.adapt(lblInfoLogo, true, true);
		
		TabItem tbtmScreenGrabber = new TabItem(tabFolder, SWT.NONE);
		tbtmScreenGrabber.setImage(new Image(shell.getDisplay(), "assets/images/ui/icons/logo_16.png"));
		tbtmScreenGrabber.setText("Screen Grabber");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmScreenGrabber.setControl(composite);
		composite.setLayout(new GridLayout(2, false));
		
		scrolledComposite = new ScrolledComposite(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.DOUBLE_BUFFERED);
		
		GridData gd_scrolledComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_scrolledComposite.heightHint = 350;
		gd_scrolledComposite.widthHint = 600;
		scrolledComposite.setLayoutData(gd_scrolledComposite);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setAlwaysShowScrollBars(true);	
		Composite composite_1 = new Composite(scrolledComposite, SWT.NONE);
		final Canvas dragStartPointerLabel = new Canvas(composite_1, SWT.NO_BACKGROUND);
		dragStartPointerLabel.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(dragStartPointer, 0, 0);
			}
		});
		dragStartPointerLabel.setBounds(0, 0, 25, 25);		
		dragStartPointerLabel.setVisible(false);
		
		screenImageLabel = new Label(composite_1, SWT.NONE | SWT.DOUBLE_BUFFERED);
		screenImageLabel.setBounds(0, 0, 55, 15);
		formToolkit.adapt(screenImageLabel, true, true);
		scrolledComposite.setContent(composite_1);
		
		dragStartPointerLabel.moveAbove(screenImageLabel);
		
		Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayout(new GridLayout(1, false));
		GridData gd_composite_3 = new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1);
		gd_composite_3.heightHint = 175;
		gd_composite_3.widthHint = 169;
		composite_3.setLayoutData(gd_composite_3);
		formToolkit.adapt(composite_3);
		formToolkit.paintBordersFor(composite_3);
		
		Composite composite_9 = new Composite(composite_3, SWT.NONE);
		GridData gd_composite_9 = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_composite_9.heightHint = 58;
		gd_composite_9.widthHint = 74;
		composite_9.setLayoutData(gd_composite_9);
		formToolkit.adapt(composite_9);
		formToolkit.paintBordersFor(composite_9);
		Button btnAdd = new Button(composite_9, SWT.NONE);
		btnAdd.setBounds(0, 0, 75, 25);
		btnAdd.setToolTipText("Adds a new image from the filesystem.");
		formToolkit.adapt(btnAdd, true, true);
		btnAdd.setText("Add");
		
		final ProgressBar generateBar = new ProgressBar(composite_9, SWT.BORDER | SWT.SMOOTH);
		generateBar.setBounds(0, 31, 163, 23);
		formToolkit.adapt(generateBar, true, true);
		Button btnDelete = new Button(composite_9, SWT.NONE);
		btnDelete.setBounds(88, 0, 75, 25);
		btnDelete.setToolTipText("Deletes the currently selected image.");
		formToolkit.adapt(btnDelete, true, true);
		btnDelete.setText("Delete");
		//Delete Image
		btnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == Settings.MOUSE_LEFT) {
					if (imageList.getItemCount() > 0) {
						int index = imageList.getSelectionIndex();
						imgpro.removeImage(index);
						imageList.remove(index);
						int newIndex = imageList.getItemCount();
						if (newIndex > 0) {
							selectImageFromImagelist(Math.max(0, index - 1));
							setCropSelectionForImage(Math.max(0, index - 1));
						} 
						else {
							selectImageFromImagelist(-1);
							
							toggleCropRadioButton(CropSettings.CROP_NONE);
							enableCropSelection(false);
							
							setCropSelectionForImage(-1);
							
							enableGeneration(false);
						}
					}
					else {
						toggleCropRadioButton(CropSettings.CROP_NONE);
						enableCropSelection(false);
						
						setCropSelectionForImage(-1);
						
						enableGeneration(false);
					}
				}
			}
		});
		//Add File Image
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == Settings.MOUSE_LEFT) {
					FileDialog fileDialog = new FileDialog(shell, SWT.MULTI);
					fileDialog.setFilterExtensions(new String[]{"*.png"});
					fileDialog.open();
					
					String[] files = fileDialog.getFileNames();
					String prependPath = fileDialog.getFilterPath() + "\\";
					
					if (files.length > 0) {
						int amount = files.length;
						int increment = 100 / amount;
						generateBar.setState(SWT.NORMAL);
						generateBar.setSelection(0);
						for (int i = 0; i < amount; i++) {
							imgpro.loadImageFromPath(prependPath + files[i]);
							imageList.add(imgpro.getImageKey(imgpro.getImageListSize() - 1));
							generateBar.setSelection((int) Math.min(i * increment, 100));
						}
						generateBar.setSelection(100);
						
						selectImageFromImagelist(Math.max(0, imageList.getItemCount() - 1));
								
						enableCropSelection(true);
						enableGeneration(true);
						
						noCrop(imageList.getSelectionIndex());
					}
				}
			}
		});
		
		imageList = new List(composite_3, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_imageList = new GridData(SWT.CENTER, SWT.FILL, false, true, 1, 1);
		gd_imageList.heightHint = 135;
		gd_imageList.widthHint = 139;
		imageList.setLayoutData(gd_imageList);
		imageList.setFont(SWTResourceManager.getFont("Lucida Console", 8, SWT.NORMAL));
		formToolkit.adapt(imageList, true, true);
		
		Composite composite_8 = new Composite(composite_3, SWT.NONE);
		composite_8.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		formToolkit.adapt(composite_8);
		formToolkit.paintBordersFor(composite_8);
		
		btnGenerate = new Button(composite_8, SWT.NONE);
		btnGenerate.setBounds(10, 0, 143, 43);
		btnGenerate.setToolTipText("Generates an image compilation from the list of images using current generation settings.");
		btnGenerate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == Settings.MOUSE_LEFT) {
					long time = System.currentTimeMillis();
					
					btnGenerate.setEnabled(false);
					imgpro.generateCompilation(generateBar);
					btnGenerate.setEnabled(true);
					
					time = System.currentTimeMillis() - time;
					System.out.println("Image Compilation Generated. " + time);
				}
			}
		});
		btnGenerate.setEnabled(false);
		btnGenerate.setFont(SWTResourceManager.getFont("Lucida Sans Unicode", 14, SWT.BOLD));
		formToolkit.adapt(btnGenerate, true, true);
		btnGenerate.setText("Generate");
		
		tabFolder_1 = new TabFolder(composite, SWT.NONE);
		//tabFolder_1.setVisible(false);
		GridData gd_tabFolder_1 = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
		gd_tabFolder_1.widthHint = 595;
		tabFolder_1.setLayoutData(gd_tabFolder_1);
		tabFolder_1.setFont(SWTResourceManager.getFont("Times New Roman CE", 8, SWT.NORMAL));
		formToolkit.adapt(tabFolder_1);
		formToolkit.paintBordersFor(tabFolder_1);
		
		final TabItem tbtmCropSettings = new TabItem(tabFolder_1, SWT.NONE);
		tbtmCropSettings.setText("Crop Settings");
		
		final Composite composite_2 = new Composite(tabFolder_1, SWT.NONE);
		tbtmCropSettings.setControl(composite_2);
		formToolkit.paintBordersFor(composite_2);
		composite_2.setLayout(new GridLayout(2, false));
		
		Group group = new Group(composite_2, SWT.NONE);
		GridData gd_group = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
		gd_group.heightHint = 130;
		gd_group.widthHint = 134;
		group.setLayoutData(gd_group);
		formToolkit.adapt(group);
		formToolkit.paintBordersFor(group);
		
		btnSetAll = new Button(group, SWT.NONE);
		btnSetAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == Settings.MOUSE_LEFT) {
					//Set all settings
					int count = imageList.getItemCount();
					if (count > 0) {
						int croptype = CropSettings.CROP_NONE;
						if (btnAutomaticCrop.getSelection()) croptype = CropSettings.CROP_AUTOMATIC;
						else if (btnManualCrop.getSelection()) croptype = CropSettings.CROP_MANUAL;
						
						for (int i = 0; i < count; i++) {
							imgpro.getImageCropSettings(i).setType(croptype);
						}
					}
				}
			}
		});
		btnSetAll.setEnabled(false);
		btnSetAll.setBounds(101, 128, 35, 15);
		btnSetAll.setToolTipText("Copies the current selection settings for all of the images.");
		btnSetAll.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		formToolkit.adapt(btnSetAll, true, true);
		btnSetAll.setText("Set All");
		
		btnAutomaticCrop = new Button(group, SWT.RADIO);
		btnAutomaticCrop.setEnabled(false);
		btnAutomaticCrop.setBounds(10, 10, 120, 16);
		btnAutomaticCrop.setToolTipText("Will attempt to find the Item Tooltip automatically. (Only works in 1920x1080 Resolution Diablo 3 Screenshots)");
		btnAutomaticCrop.setFont(SWTResourceManager.getFont("Lucida Sans Unicode", 10, SWT.NORMAL));
		formToolkit.adapt(btnAutomaticCrop, true, true);
		btnAutomaticCrop.setText("Automatic Crop");
		
		btnManualCrop = new Button(group, SWT.RADIO);
		btnManualCrop.setEnabled(false);
		btnManualCrop.setBounds(10, 32, 100, 16);
		btnManualCrop.setFont(SWTResourceManager.getFont("Lucida Sans Unicode", 10, SWT.NORMAL));
		btnManualCrop.setToolTipText("Manually set the bounds of the image to be used.");
		formToolkit.adapt(btnManualCrop, true, true);
		btnManualCrop.setText("Manual Crop");
		
		btnNoCrop = new Button(group, SWT.RADIO);
		btnNoCrop.setEnabled(false);
		btnNoCrop.setBounds(10, 54, 73, 16);
		btnNoCrop.setFont(SWTResourceManager.getFont("Lucida Sans Unicode", 10, SWT.NORMAL));
		btnNoCrop.setToolTipText("Will use the entire image.");
		formToolkit.adapt(btnNoCrop, true, true);
		btnNoCrop.setText("No Crop");
		
		cropInfoText = new StyledText(group, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		cropInfoText.setEnabled(false);
		cropInfoText.setBounds(10, 78, 120, 60);
		cropInfoText.setFont(SWTResourceManager.getFont("Lucida Sans Unicode", 7, SWT.NORMAL));
		cropInfoText.setDoubleClickEnabled(false);
		cropInfoText.setEditable(false);
		formToolkit.adapt(cropInfoText);
		formToolkit.paintBordersFor(cropInfoText);
		
		Group group_1 = new Group(composite_2, SWT.NONE);
		GridData gd_group_1 = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_group_1.heightHint = 130;
		gd_group_1.widthHint = 409;
		group_1.setLayoutData(gd_group_1);
		formToolkit.adapt(group_1);
		formToolkit.paintBordersFor(group_1);
		
		Composite composite_5 = new Composite(group_1, SWT.BORDER);
		composite_5.setBounds(10, 20, 193, 118);
		formToolkit.adapt(composite_5);
		formToolkit.paintBordersFor(composite_5);
		
		Label label = new Label(composite_5, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.CENTER);
		label.setBounds(10, 13, 169, 4);
		formToolkit.adapt(label, true, true);
		
		Label lblAutomaticSettings = new Label(composite_5, SWT.CENTER);
		lblAutomaticSettings.setFont(SWTResourceManager.getFont("DejaVu Sans", 8, SWT.BOLD));
		lblAutomaticSettings.setBounds(10, 0, 169, 15);
		formToolkit.adapt(lblAutomaticSettings, true, true);
		lblAutomaticSettings.setText("Automatic Settings");
		
		txtAutocropInfo = new Text(composite_5, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		txtAutocropInfo.setText("Automatic Cropping will attempt to find the Item Tooltip in the Image and crop around it. If a tooltip cannot be found, the whole image will be used. Automatic Cropping currently only works with 1920x1080 resolution Diablo 3 screenshots of any size.");
		txtAutocropInfo.setEditable(false);
		txtAutocropInfo.setEnabled(false);
		txtAutocropInfo.setFont(SWTResourceManager.getFont("Lucida Sans Unicode", 7, SWT.NORMAL));
		txtAutocropInfo.setBounds(10, 23, 169, 63);
		formToolkit.adapt(txtAutocropInfo, true, true);
		
		Label lblIndexingText = new Label(composite_5, SWT.NONE);
		lblIndexingText.setToolTipText("To display indexing text, enable 'Image Label Indexing' in the 'Generation Settings' tab. Indexing text is a text string displayed below each index number/letter in the image.");
		lblIndexingText.setFont(SWTResourceManager.getFont("Raavi", 8, SWT.NORMAL));
		lblIndexingText.setBounds(10, 92, 68, 15);
		formToolkit.adapt(lblIndexingText, true, true);
		lblIndexingText.setText("Indexing Text:");
		
		textGenIndexingString = new Text(composite_5, SWT.BORDER);
		textGenIndexingString.setEnabled(false);
		textGenIndexingString.setBounds(79, 90, 100, 21);
		formToolkit.adapt(textGenIndexingString, true, true);
		
		Composite composite_6 = new Composite(group_1, SWT.BORDER);
		composite_6.setBounds(212, 20, 193, 118);
		formToolkit.adapt(composite_6);
		formToolkit.paintBordersFor(composite_6);
		
		Label lblDoubleClickImage = new Label(composite_6, SWT.NONE);
		lblDoubleClickImage.setAlignment(SWT.CENTER);
		lblDoubleClickImage.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.NORMAL));
		lblDoubleClickImage.setBounds(20, 15, 148, 15);
		formToolkit.adapt(lblDoubleClickImage, true, true);
		lblDoubleClickImage.setText("Double Click  Image to Open Editor");
		
		final Label lblCursorLoc2 = new Label(composite_6, SWT.NONE);
		lblCursorLoc2.setText("Y: 0");
		lblCursorLoc2.setFont(SWTResourceManager.getFont("Raavi", 8, SWT.NORMAL));
		lblCursorLoc2.setBounds(51, 90, 40, 15);
		formToolkit.adapt(lblCursorLoc2, true, true);
		
		lblImgDimensions = new Label(composite_6, SWT.NONE);
		lblImgDimensions.setText("W: 1920 H: 1080");
		lblImgDimensions.setFont(SWTResourceManager.getFont("Raavi", 8, SWT.NORMAL));
		lblImgDimensions.setBounds(8, 101, 90, 15);
		formToolkit.adapt(lblImgDimensions, true, true);
		
		final Label lblCursorLoc = new Label(composite_6, SWT.NONE);
		lblCursorLoc.setFont(SWTResourceManager.getFont("Raavi", 8, SWT.NORMAL));
		lblCursorLoc.setBounds(10, 90, 40, 15);
		formToolkit.adapt(lblCursorLoc, true, true);
		lblCursorLoc.setText("X: 0");
		
		Label label_1 = new Label(composite_6, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.CENTER);
		label_1.setBounds(10, 13, 169, 4);
		formToolkit.adapt(label_1, true, true);
		
		Label lblManualSettings = new Label(composite_6, SWT.CENTER);
		lblManualSettings.setFont(SWTResourceManager.getFont("DejaVu Sans", 8, SWT.BOLD));
		lblManualSettings.setBounds(10, 0, 169, 15);
		formToolkit.adapt(lblManualSettings, true, true);
		lblManualSettings.setText("Manual Settings");
		
		Label lblLeft = new Label(composite_6, SWT.NONE);
		lblLeft.setAlignment(SWT.RIGHT);
		lblLeft.setFont(SWTResourceManager.getFont("Gautami", 12, SWT.NORMAL));
		lblLeft.setBounds(10, 30, 38, 31);
		formToolkit.adapt(lblLeft, true, true);
		lblLeft.setText("Left:");
		
		Label lblRight = new Label(composite_6, SWT.NONE);
		lblRight.setAlignment(SWT.RIGHT);
		lblRight.setText("Right:");
		lblRight.setFont(SWTResourceManager.getFont("Gautami", 12, SWT.NORMAL));
		lblRight.setBounds(10, 65, 38, 31);
		formToolkit.adapt(lblRight, true, true);
		
		Label lblBot = new Label(composite_6, SWT.NONE);
		lblBot.setText("Bot:");
		lblBot.setFont(SWTResourceManager.getFont("Gautami", 12, SWT.NORMAL));
		lblBot.setAlignment(SWT.RIGHT);
		lblBot.setBounds(90, 65, 38, 31);
		formToolkit.adapt(lblBot, true, true);
		
		Label lblTop = new Label(composite_6, SWT.NONE);
		lblTop.setText("Top:");
		lblTop.setFont(SWTResourceManager.getFont("Gautami", 12, SWT.NORMAL));
		lblTop.setAlignment(SWT.RIGHT);
		lblTop.setBounds(90, 30, 38, 31);
		formToolkit.adapt(lblTop, true, true);
		
		textManualLeft = new Text(composite_6, SWT.BORDER);
		textManualLeft.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				if (verifyNumericString(e.text, false)) {
					e.doit = true;
				}
				else {
					e.doit = false;
				}
			}
		});
		textManualLeft.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textManualLeft.selectAll();
			}
		});
		textManualLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == Settings.MOUSE_LEFT) {
					textManualLeft.selectAll();
				}
			}
		});
		textManualLeft.setEnabled(false);
		textManualLeft.setTextLimit(4);
		textManualLeft.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.ITALIC));
		textManualLeft.setText("0");
		textManualLeft.setBounds(52, 33, 36, 21);
		formToolkit.adapt(textManualLeft, true, true);
		
		textManualTop = new Text(composite_6, SWT.BORDER);
		textManualTop.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				if (verifyNumericString(e.text, false)) {
					e.doit = true;
				}
				else {
					e.doit = false;
				}
			}
		});
		textManualTop.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textManualTop.selectAll();
			}
		});
		textManualTop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == Settings.MOUSE_LEFT) {
					textManualTop.selectAll();
				}
			}
		});
		textManualTop.setEnabled(false);
		textManualTop.setTextLimit(4);
		textManualTop.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.ITALIC));
		textManualTop.setText("0");
		textManualTop.setBounds(132, 33, 36, 21);
		formToolkit.adapt(textManualTop, true, true);
		
		textManualRight = new Text(composite_6, SWT.BORDER);
		textManualRight.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				if (verifyNumericString(e.text, false)) {
					e.doit = true;
				}
				else {
					e.doit = false;
				}
			}
		});
		textManualRight.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textManualRight.selectAll();
			}
		});
		textManualRight.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == Settings.MOUSE_LEFT) {
					textManualRight.selectAll();
				}
			}
		});
		textManualRight.setEnabled(false);
		textManualRight.setTextLimit(4);
		textManualRight.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.ITALIC));
		textManualRight.setText("0");
		textManualRight.setBounds(52, 68, 36, 21);
		formToolkit.adapt(textManualRight, true, true);
		
		textManualBot = new Text(composite_6, SWT.BORDER);
		textManualBot.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				if (verifyNumericString(e.text, false)) {
					e.doit = true;
				}
				else {
					e.doit = false;
				}
			}
		});
		textManualBot.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textManualBot.selectAll();
			}
		});
		textManualBot.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == Settings.MOUSE_LEFT) {
					textManualBot.selectAll();
				}
			}
		});
		textManualBot.setEnabled(false);
		textManualBot.setTextLimit(4);
		textManualBot.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.ITALIC));
		textManualBot.setText("0");
		textManualBot.setBounds(132, 68, 36, 21);
		formToolkit.adapt(textManualBot, true, true);
		
		btnSetCrop = new Button(composite_6, SWT.NONE);
		btnSetCrop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CropSettings cs = imgpro.getImageCropSettings(imageList.getSelectionIndex());
				
				cs.setX(Integer.valueOf(textManualLeft.getText()));
				cs.setY(Integer.valueOf(textManualTop.getText()));
				cs.setWidth(Integer.valueOf(textManualRight.getText()) - Integer.valueOf(textManualLeft.getText()));
				cs.setHeight(Integer.valueOf(textManualBot.getText()) - Integer.valueOf(textManualTop.getText()));
			}
		});
		btnSetCrop.setEnabled(false);
		btnSetCrop.setFont(SWTResourceManager.getFont("Tahoma", 7, SWT.BOLD));
		btnSetCrop.setBounds(100, 95, 79, 17);
		formToolkit.adapt(btnSetCrop, true, true);
		btnSetCrop.setText("Set Crop");
		//No Crop
		btnNoCrop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnNoCrop.getSelection()) {
					noCrop(imageList.getSelectionIndex());
				}
			}
		});
		//Manual Crop
		btnManualCrop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				if (btnManualCrop.getSelection()) {
					manualCrop(imageList.getSelectionIndex());
				}
			}
		});
		//Automatic Crop
		btnAutomaticCrop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnAutomaticCrop.getSelection()) {
					automaticCrop(imageList.getSelectionIndex());
				}
			}
		});
		
		Composite composite_4 = new Composite(tabFolder_1, SWT.NONE);
		formToolkit.paintBordersFor(composite_4);
		
		TabItem tbtmGenSettings = new TabItem(tabFolder_1, SWT.NONE);
		tbtmGenSettings.setText("Generation Settings");
		
		Composite composite_10 = new Composite(tabFolder_1, SWT.NONE);
		tbtmGenSettings.setControl(composite_10);
		formToolkit.paintBordersFor(composite_10);
		composite_10.setLayout(new GridLayout(2, false));
		
		Group group_3 = new Group(composite_10, SWT.NONE);
		GridData gd_group_3 = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_group_3.heightHint = 126;
		gd_group_3.widthHint = 222;
		group_3.setLayoutData(gd_group_3);
		formToolkit.adapt(group_3);
		formToolkit.paintBordersFor(group_3);
		
		Label lblMaximumImageWidth = new Label(group_3, SWT.NONE);
		lblMaximumImageWidth.setText("Maximum Image Width:");
		lblMaximumImageWidth.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		lblMaximumImageWidth.setBounds(34, 130, 105, 15);
		formToolkit.adapt(lblMaximumImageWidth, true, true);
		
		Label lblNewLabel = new Label(group_3, SWT.NONE);
		lblNewLabel.setToolTipText("Location where the generated image compilation will be saved.");
		lblNewLabel.setFont(SWTResourceManager.getFont("Lucida Sans Unicode", 10, SWT.NORMAL));
		lblNewLabel.setBounds(4, 16, 59, 15);
		formToolkit.adapt(lblNewLabel, true, true);
		lblNewLabel.setText("Gen Path:");
		
		textGenPath = new Text(group_3, SWT.BORDER);
		textGenPath.setBounds(65, 13, 107, 21);
		formToolkit.adapt(textGenPath, true, true);
		
		Button btnBrowseGenPath = new Button(group_3, SWT.NONE);
		btnBrowseGenPath.setBounds(174, 12, 50, 23);
		formToolkit.adapt(btnBrowseGenPath, true, true);
		btnBrowseGenPath.setText("Browse");
		
		Group grpImageLabelindexing = new Group(group_3, SWT.NONE);
		grpImageLabelindexing.setFont(SWTResourceManager.getFont("Microsoft YaHei", 8, SWT.BOLD));
		grpImageLabelindexing.setToolTipText("Indexes every image by assigning it a number in the generation image. This can be useful when generating auction images (as seen on sites like d2jsp).");
		grpImageLabelindexing.setText("Image Label Indexing");
		grpImageLabelindexing.setBounds(4, 38, 214, 91);
		formToolkit.adapt(grpImageLabelindexing);
		formToolkit.paintBordersFor(grpImageLabelindexing);
		
		Button btnGenIndexingEnabled = new Button(grpImageLabelindexing, SWT.CHECK | SWT.CENTER);
		btnGenIndexingEnabled.setToolTipText("Enable/Disable");
		btnGenIndexingEnabled.setBounds(10, 17, 73, 16);
		formToolkit.adapt(btnGenIndexingEnabled, true, true);
		btnGenIndexingEnabled.setText("Enabled");
		
		Label lblXOffset = new Label(grpImageLabelindexing, SWT.NONE);
		lblXOffset.setFont(SWTResourceManager.getFont("Raavi", 8, SWT.NORMAL));
		lblXOffset.setBounds(10, 36, 41, 15);
		formToolkit.adapt(lblXOffset, true, true);
		lblXOffset.setText("X Offset:");
		
		text = new Text(grpImageLabelindexing, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.ITALIC));
		text.setText("999");
		text.setBounds(53, 36, 30, 16);
		formToolkit.adapt(text, true, true);
		
		Label lblYOffset = new Label(grpImageLabelindexing, SWT.NONE);
		lblYOffset.setText("Y Offset:");
		lblYOffset.setFont(SWTResourceManager.getFont("Raavi", 8, SWT.NORMAL));
		lblYOffset.setBounds(10, 54, 41, 15);
		formToolkit.adapt(lblYOffset, true, true);
		
		text_1 = new Text(grpImageLabelindexing, SWT.BORDER);
		text_1.setText("999");
		text_1.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.ITALIC));
		text_1.setBounds(53, 54, 30, 16);
		formToolkit.adapt(text_1, true, true);
		
		Label lblTextAColor = new Label(grpImageLabelindexing, SWT.NONE);
		lblTextAColor.setText("Text A Color:");
		lblTextAColor.setFont(SWTResourceManager.getFont("Raavi", 8, SWT.NORMAL));
		lblTextAColor.setBounds(89, 18, 60, 15);
		formToolkit.adapt(lblTextAColor, true, true);
		
		Canvas canvas = new Canvas(grpImageLabelindexing, SWT.NONE);
		canvas.setBounds(150, 16, 18, 18);
		formToolkit.adapt(canvas);
		formToolkit.paintBordersFor(canvas);
		
		Label lblTextBColor = new Label(grpImageLabelindexing, SWT.NONE);
		lblTextBColor.setText("Text B Color:");
		lblTextBColor.setFont(SWTResourceManager.getFont("Raavi", 8, SWT.NORMAL));
		lblTextBColor.setBounds(89, 36, 60, 15);
		formToolkit.adapt(lblTextBColor, true, true);
		
		Canvas canvas_1 = new Canvas(grpImageLabelindexing, SWT.NONE);
		canvas_1.setBounds(150, 35, 18, 18);
		formToolkit.adapt(canvas_1);
		formToolkit.paintBordersFor(canvas_1);
		
		Button btnNewButton = new Button(grpImageLabelindexing, SWT.NONE);
		btnNewButton.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		btnNewButton.setBounds(170, 16, 42, 18);
		formToolkit.adapt(btnNewButton, true, true);
		btnNewButton.setText("Choose");
		
		Button button = new Button(grpImageLabelindexing, SWT.NONE);
		button.setText("Choose");
		button.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		button.setBounds(170, 35, 42, 18);
		formToolkit.adapt(button, true, true);
		
		Label lblTextASize = new Label(grpImageLabelindexing, SWT.NONE);
		lblTextASize.setText("Text A Style:");
		lblTextASize.setFont(SWTResourceManager.getFont("Raavi", 8, SWT.NORMAL));
		lblTextASize.setBounds(89, 54, 60, 15);
		formToolkit.adapt(lblTextASize, true, true);
		
		Label lblTextBSize = new Label(grpImageLabelindexing, SWT.NONE);
		lblTextBSize.setText("Text B Style:");
		lblTextBSize.setFont(SWTResourceManager.getFont("Raavi", 8, SWT.NORMAL));
		lblTextBSize.setBounds(89, 72, 60, 15);
		formToolkit.adapt(lblTextBSize, true, true);
		
		Button btnCustomize = new Button(grpImageLabelindexing, SWT.NONE);
		btnCustomize.setText("Customize");
		btnCustomize.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		btnCustomize.setBounds(149, 53, 63, 18);
		formToolkit.adapt(btnCustomize, true, true);
		
		Button button_1 = new Button(grpImageLabelindexing, SWT.NONE);
		button_1.setText("Customize");
		button_1.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		button_1.setBounds(149, 70, 63, 18);
		formToolkit.adapt(button_1, true, true);
		
		Button btnGotoTextB = new Button(grpImageLabelindexing, SWT.NONE);
		btnGotoTextB.setText("Edit Text B");
		btnGotoTextB.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.ITALIC));
		btnGotoTextB.setBounds(10, 71, 73, 16);
		formToolkit.adapt(btnGotoTextB, true, true);
		
		text_2 = new Text(group_3, SWT.BORDER | SWT.CENTER);
		text_2.setText("1920");
		text_2.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.ITALIC));
		text_2.setBounds(141, 129, 36, 16);
		formToolkit.adapt(text_2, true, true);
		
		Group group_4 = new Group(composite_10, SWT.NONE);
		GridData gd_group_4 = new GridData(SWT.CENTER, SWT.FILL, true, true, 1, 1);
		gd_group_4.widthHint = 318;
		group_4.setLayoutData(gd_group_4);
		formToolkit.adapt(group_4);
		formToolkit.paintBordersFor(group_4);
		
		Group grpPreview = new Group(group_4, SWT.NONE);
		grpPreview.setFont(SWTResourceManager.getFont("Trajan Pro", 10, SWT.BOLD));
		grpPreview.setText("Preview");
		grpPreview.setBounds(10, 10, 304, 128);
		formToolkit.adapt(grpPreview);
		formToolkit.paintBordersFor(grpPreview);
		
		Button btnNewButton_1 = new Button(grpPreview, SWT.NONE);
		btnNewButton_1.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.BOLD));
		btnNewButton_1.setBounds(191, 0, 108, 18);
		formToolkit.adapt(btnNewButton_1, true, true);
		btnNewButton_1.setText("Customize Background");
		
		Canvas canvas_2 = new Canvas(grpPreview, SWT.NONE);
		canvas_2.setBounds(6, 19, 293, 99);
		formToolkit.adapt(canvas_2);
		formToolkit.paintBordersFor(canvas_2);
		
		Group grpScreenGrabSettings = new Group(composite, SWT.NONE);
		GridData gd_grpScreenGrabSettings = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_grpScreenGrabSettings.widthHint = 159;
		grpScreenGrabSettings.setLayoutData(gd_grpScreenGrabSettings);
		grpScreenGrabSettings.setText("Screen Grab Settings");
		formToolkit.adapt(grpScreenGrabSettings);
		formToolkit.paintBordersFor(grpScreenGrabSettings);
		
		Group grpScreenGrabber = new Group(grpScreenGrabSettings, SWT.NONE);
		grpScreenGrabber.setBounds(10, 16, 149, 158);
		formToolkit.adapt(grpScreenGrabber);
		formToolkit.paintBordersFor(grpScreenGrabber);
		
		txtPrtScrn = new Text(grpScreenGrabber, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
		txtPrtScrn.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				//Change keybinding if enabled and keyreleased
				KeyStroke keyStroke = KeyStroke.getInstance(e.stateMask, e.keyCode);
				txtPrtScrn.setText(keyStroke.toString());
				Settings.screengrab_hotkey_text = keyStroke.toString();
			}
		});
		txtPrtScrn.setEnabled(Settings.screengrab_hotkey_enabled);
		txtPrtScrn.setText(Settings.screengrab_hotkey_text);
		txtPrtScrn.setBounds(64, 54, 75, 18);
		formToolkit.adapt(txtPrtScrn, true, true);
		
		btnHotkeyEnabled = new Button(grpScreenGrabber, SWT.CHECK);
		btnHotkeyEnabled.setToolTipText("Enables/Disables the use of the screen grabber hotkey. ");
		btnHotkeyEnabled.setSelection(Settings.screengrab_hotkey_enabled);
		btnHotkeyEnabled.setBounds(10, 25, 108, 16);
		formToolkit.adapt(btnHotkeyEnabled, true, true);
		btnHotkeyEnabled.setText("Hotkey Enabled");
		
		Label lblHotkeyPrintScreen = new Label(grpScreenGrabber, SWT.NONE);
		lblHotkeyPrintScreen.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblHotkeyPrintScreen.setBounds(9, 53, 108, 25);
		formToolkit.adapt(lblHotkeyPrintScreen, true, true);
		lblHotkeyPrintScreen.setText("Hotkey:");
		
		final Button btnSaveHotkey = new Button(grpScreenGrabber, SWT.NONE);
		btnSaveHotkey.setToolTipText("Stops hotkey selection and saves the currently selected hotkey.");
		btnSaveHotkey.setFont(SWTResourceManager.getFont("Tahoma", 7, SWT.BOLD));
		btnSaveHotkey.setBounds(10, 92, 129, 25);
		btnSaveHotkey.setEnabled(Settings.screengrab_hotkey_enabled);
		formToolkit.adapt(btnSaveHotkey, true, true);
		btnSaveHotkey.setText("Save Hotkey");
		
		Button btnDefaultHotkey = new Button(grpScreenGrabber, SWT.NONE);
		btnDefaultHotkey.setToolTipText("Reverts the hotkey back to the default.");
		btnDefaultHotkey.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == Settings.MOUSE_LEFT) {
					screenGrabKeycode = DEFAULT_HOTKEY;
					Settings.screengrab_hotkey_text = DEFAULT_HOTKEY_TEXT;
					txtPrtScrn.setText(DEFAULT_HOTKEY_TEXT);
				}
			}
		});
		btnDefaultHotkey.setFont(SWTResourceManager.getFont("Tahoma", 7, SWT.BOLD));
		btnDefaultHotkey.setBounds(10, 123, 129, 25);
		formToolkit.adapt(btnDefaultHotkey, true, true);
		btnDefaultHotkey.setText("Default Hotkey");
		grpScreenGrabber.setTabList(new Control[]{btnHotkeyEnabled, btnSaveHotkey, btnDefaultHotkey});
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Run Tracker");
		
		TabItem tbtmAuctionTracker = new TabItem(tabFolder, SWT.NONE);
		tbtmAuctionTracker.setText("Auctioneer");
		
		TabItem tbtmCharacterSheet = new TabItem(tabFolder, SWT.NONE);
		tbtmCharacterSheet.setText("Character Sheet");
		m_bindingContext = initDataBindings();
		
		/*
		 * Listeners
		 */
		// GlobalKeyListener for unfocused events like printscreening
		new GlobalKeyListener().addKeyListener(new de.ksquared.system.keyboard.KeyAdapter() {
					@Override
					public void keyPressed(de.ksquared.system.keyboard.KeyEvent event) {
						final String eventString = event.toString();
						
						shell.getDisplay().syncExec(new Runnable() {
							public void run() {
								if (txtPrtScrn.isFocusControl()) {
									screenGrabKeycode = eventString;
									if (StringUtil.firstOccurenceBeforePos(screenGrabKeycode, ",", screenGrabKeycode.length() - 1) > 2) {
										String s1 = StringUtil.substring(screenGrabKeycode, 0, StringUtil.firstOccurenceBeforePos(screenGrabKeycode, "[", screenGrabKeycode.length() - 1) + 1, true);
										String s2 = StringUtil.substring(screenGrabKeycode, ",", true);
										screenGrabKeycode = s1 + s2;
									}
									else {
										String s1 = StringUtil.substring(screenGrabKeycode, 0, StringUtil.firstOccurenceBeforePos(screenGrabKeycode, "[", screenGrabKeycode.length() - 1) - 1, true);
										screenGrabKeycode = s1;
									}
									
									screenGrabKeycode = screenGrabKeycode.replace('[', '(');
									screenGrabKeycode = screenGrabKeycode.replace(']', ')');
									
									Settings.screengrab_hotkey = screenGrabKeycode;
								}
							}
						});
					}

					@Override
					public void keyReleased(de.ksquared.system.keyboard.KeyEvent event) {						
						final String eventString = event.toString();						
						
						//Ugliest code you will ever see, but only way I could map two different KeyEvent implementations the easy way
						shell.getDisplay().syncExec(new Runnable() {
							public void run() {
								String eventStringNew = eventString;
								if (StringUtil.firstOccurenceBeforePos(eventStringNew, ",", eventStringNew.length() - 1) > 2) {
									String s1 = StringUtil.substring(eventStringNew, 0, StringUtil.firstOccurenceBeforePos(eventStringNew, "[", eventStringNew.length() - 1) + 1, true);
									String s2 = StringUtil.substring(eventStringNew, ",", true);
									eventStringNew = s1 + s2;
								}
								else {
									String s1 = StringUtil.substring(eventStringNew, 0, StringUtil.firstOccurenceBeforePos(eventStringNew, "[", eventStringNew.length() - 1) - 1, true);
									eventStringNew = s1;
								}
								
								eventStringNew = eventStringNew.replace('[', '(');
								eventStringNew = eventStringNew.replace(']', ')');
								
								if (eventStringNew.equals(screenGrabKeycode) && !txtPrtScrn.isFocusControl()) {
									if (btnHotkeyEnabled.getSelection()) {
										System.out.println("SCREENGRABBED");

										imgpro.screenCapture();

										imageList.add(imgpro.getImageKey(imgpro.getImageListSize() - 1));

										// Select last image
										selectImageFromImagelist(Math.max(0, imageList.getItemCount() - 1));

										enableGeneration(true);
										enableCropSelection(true);
										
										setCropSelectionForImage(imageList.getSelectionIndex());
									}
								}						
							}
						});
					}
				});
		//Image List
		imageList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectImageFromImagelist(imageList.getSelectionIndex());				
				setCropSelectionForImage(imageList.getSelectionIndex());
			}
		});		
		//ScreenImageLabelDragMove
		screenImageLabel.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				if (imageRightDragging) {					
					int dx = (e.x - imageDragx);
					int dy = (e.y - imageDragy);
					if (dx != 0) dx = Math.abs(dx) / dx;
					if (dy != 0) dy = Math.abs(dy) / dy;
					
					ScrollBar hb = scrolledComposite.getHorizontalBar();
					ScrollBar vb = scrolledComposite.getVerticalBar();
					
					int amount = 8;
					
					scrolledComposite.setOrigin(hb.getSelection() + (dx * amount), vb.getSelection() + (dy * amount));
					
					imageDragx = e.x;
					imageDragy = e.y;
				}
				
				//Update cursor loc
				lblCursorLoc.setText("X: " + e.x);
				lblCursorLoc2.setText("Y: " + e.y);
			}
		});
		//ScreenImageLabelDrag
		screenImageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == Settings.MOUSE_RIGHT) {					
					if (!imageRightDragging) {
						if ((imageList.getItemCount() > 0) && (imageList.getSelectionIndex() >= 0)) {
							imageRightDragging = true;
							imageDragx = e.x;
							imageDragy = e.y;
							
							dragStartPointerLabel.setVisible(true);
							dragStartPointerLabel.setLocation(imageDragx - (dragStartPointer.getBounds().width / 2), imageDragy - (dragStartPointer.getBounds().height / 2));
						}
					}
				}
			}
			
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == Settings.MOUSE_RIGHT) {
					if (imageRightDragging) {
						imageRightDragging = false;
						
						dragStartPointerLabel.setVisible(false);
					}
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == Settings.MOUSE_LEFT) {
					if ((imageList.getItemCount() > 0) && (imageList.getSelectionIndex() >= 0)) {
						if (imgpro.getImageCropSettings(imageList.getSelectionIndex()).getType() == CropSettings.CROP_MANUAL) {
							//Open up AWT window to do crop selection
							shell.getDisplay().syncExec(new Runnable() {
								public void run() {
									try {
										CropEditor frame = new CropEditor(shell, imgpro, imageList.getSelectionIndex(), textManualLeft, textManualTop, textManualRight, textManualBot);
										frame.setVisible(true);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						}
					}
				}
			}
		});
		//Button hotkey enabled selected
		btnHotkeyEnabled.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Screen Grabber Settings
				txtPrtScrn.setEnabled(btnHotkeyEnabled.getSelection());
				btnSaveHotkey.setEnabled(btnHotkeyEnabled.getSelection());
				Settings.screengrab_hotkey_enabled = btnHotkeyEnabled.getSelection();
			}
		});
		//Shell Closed
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try {
					/*Offload Settings */
					//Display Settings
					WINDOW_WIDTH = shell.getBounds().width;
					WINDOW_HEIGHT = shell.getBounds().height;
					MAXIMIZED = shell.getMaximized();
					/* Other settings are set throughout the program */					
					Config.get().createNewConfig(null);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Displays the image selected from an imagelist, a negative index sets it to display no image
	 */
	private void selectImageFromImagelist(int index) {
		if (index >= 0) {
			imageList.select(index);
			Image img = imgpro.getImage(imageList.getItem(index));
			scrolledComposite.setMinSize(img.getBounds().width,	img.getBounds().height);
			screenImageLabel.setBounds(img.getBounds());
			screenImageLabel.setImage(img);
		}
		else {
			scrolledComposite.setMinSize(0, 0);
			screenImageLabel.setBounds(0, 0, 0, 0);
			screenImageLabel.setImage(null);
		}
	}
	
	/**
	 * Enables/Disables the selection of crop setting choices none, manual, automatic
	 */
	private void enableCropSelection(boolean enable) {
		if (enable) {
			btnNoCrop.setEnabled(true);
			btnManualCrop.setEnabled(true);
			btnAutomaticCrop.setEnabled(true);
			btnSetAll.setEnabled(true);
			cropInfoText.setEnabled(true);
		}
		else {
			btnNoCrop.setEnabled(false);
			btnManualCrop.setEnabled(false);
			btnAutomaticCrop.setEnabled(false);
			btnSetAll.setEnabled(false);
			cropInfoText.setEnabled(false);
		}
	}
	
	/**
	 * Enables/Disables the Generation button
	 */
	private void enableGeneration(boolean enable) {
		if (enable) {
			btnGenerate.setEnabled(true);
		}
		else {
			btnGenerate.setEnabled(false);
		}
	}
	
	/**
	 * Either enables or disables all elements having to do with manual crop settings
	 */
	private void enableManualCropSettings(boolean enable) {
		if (enable) {
			textManualLeft.setEnabled(true);
			textManualRight.setEnabled(true);
			textManualTop.setEnabled(true);
			textManualBot.setEnabled(true);
			btnSetCrop.setEnabled(true);
		}
		else {
			textManualLeft.setEnabled(false);
			textManualRight.setEnabled(false);
			textManualTop.setEnabled(false);
			textManualBot.setEnabled(false);
			btnSetCrop.setEnabled(false);
		}
	}
	
	/**
	 * Either enables or disables all elements having to do with manual crop settings
	 */
	private void enableAutomaticCropSettings(boolean enable) {
		if (enable) {
			txtAutocropInfo.setEnabled(true);
		}
		else {
			txtAutocropInfo.setEnabled(false);
		}
	}
	
	/**
	 * Toggles the crop selection radio based on type, type fields found in CropSettings
	 */
	private void toggleCropRadioButton(int type) {
		btnNoCrop.setSelection(false);
		btnManualCrop.setSelection(false);
		btnAutomaticCrop.setSelection(false);
		
		switch (type) {
			case CropSettings.CROP_NONE: {
				btnNoCrop.setSelection(true);
				break;
			}
			case CropSettings.CROP_MANUAL: {
				btnManualCrop.setSelection(true);
				break;
			}
			case CropSettings.CROP_AUTOMATIC: {
				btnAutomaticCrop.setSelection(true);
				break;
			}
		}
	}
	
	private void setImageDimensionInfo(int index) {
		//Get Dimensions
		Rectangle imgbounds = imgpro.getImage(imgpro.getImageKey(index)).getBounds();
		lblImgDimensions.setText("W: " + imgbounds.width + " H: " + imgbounds.height);
	}
	
	/**
	 * Sets all no crop settings
	 */
	private void noCrop(int index) {
		toggleCropRadioButton(CropSettings.CROP_NONE);
		
		cropInfoText.setText("Uses the whole Image.");
		
		//Enable/Disable and get settings
		if (imageList.getSelectionCount() > 0) {
			CropSettings cs = imgpro.getImageCropSettings(index);
			cs.setType(CropSettings.CROP_NONE);
			cs.setX(0);
			cs.setY(0);
			cs.setWidth(imgpro.getImage(imgpro.getImageKey(index)).getBounds().width);
			cs.setHeight(imgpro.getImage(imgpro.getImageKey(index)).getBounds().height);
			
			//Get Dimensions
			setImageDimensionInfo(index);
		}
		
		//Disable Auto and Manual
		enableManualCropSettings(false);
		enableAutomaticCropSettings(false);
	}
	
	/**
	 * Sets all manual crop settings
	 */
	private void manualCrop(int index) {
		toggleCropRadioButton(CropSettings.CROP_MANUAL);
		
		cropInfoText.setText("Manually set crop boundaries for the Image.");
		
		//Enable/Disable and get settings
		if (imageList.getSelectionCount() > 0) {
			CropSettings cs = imgpro.getImageCropSettings(index);
			cs.setType(CropSettings.CROP_MANUAL);
			
			//Disable Auto and Manual
			enableManualCropSettings(true);
			enableAutomaticCropSettings(false);
			
			//Get settings
			textManualLeft.setText(String.valueOf(cs.getX()));
			textManualRight.setText(String.valueOf(cs.getX() + cs.getWidth()));
			textManualTop.setText(String.valueOf(cs.getY()));
			textManualBot.setText(String.valueOf(cs.getY() + cs.getHeight()));
			
			//Get Dimensions
			setImageDimensionInfo(index);
		}
	}
	
	/**
	 * Sets all automatic crop settings
	 */
	private void automaticCrop(int index) {
		toggleCropRadioButton(CropSettings.CROP_AUTOMATIC);
		
		cropInfoText.setText("Sets crop around Item Tooltip. (1920x1080 Resolution Screenshots Only)");
		
		//Enable/Disable and get settings
		if (imageList.getSelectionCount() > 0) {
			CropSettings cs = imgpro.getImageCropSettings(index);
			cs.setType(CropSettings.CROP_AUTOMATIC);
			
			//Disable Auto and Manual
			enableManualCropSettings(false);
			enableAutomaticCropSettings(true);
			
			//Get settings
			textManualLeft.setText(String.valueOf(cs.getX()));
			textManualRight.setText(String.valueOf(cs.getX() + cs.getWidth()));
			textManualTop.setText(String.valueOf(cs.getY()));
			textManualBot.setText(String.valueOf(cs.getY() + cs.getHeight()));
			
			//Get Dimensions
			setImageDimensionInfo(index);
		}
	}
	
	/**
	 * Sets the proper crop settings for the image, given the selection index, a negative index indicates no image and just sets it to no crop
	 */
	private void setCropSelectionForImage(int index) {
		//Select proper crop settings
		if (index >= 0) {
			int selectionType = imgpro.getImageCropSettings(index).getType();
			switch (selectionType) {
				case CropSettings.CROP_NONE: {
					noCrop(index);
					break;
				}
				case CropSettings.CROP_MANUAL: {
					manualCrop(index);
					break;
				}
				case CropSettings.CROP_AUTOMATIC: {
					automaticCrop(index);				
					break;
				}
			}
		}
		else {
			noCrop(index);
		}
	}
	
	/**
	 * Verifies that the string is numeric, allowNegative sets whether or not a prefix of '-' is allowed;
	 */
	private boolean verifyNumericString(String s, boolean allowNegative) {
		for (int i = 0; i < s.length(); i++) {
			if ((i == 0) && allowNegative) {
				if ((s.charAt(i) != '-') && !isNumericCharacter(s.charAt(i))) {
					return false;
				}
			}
			else if (!isNumericCharacter(s.charAt(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the character is between '0' and '9'
	 */
	private boolean isNumericCharacter(char c) {
		if ((c >= '0') && (c <= '9')) return true;
		return false;
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}
