package com.fizzikgames.d3b;

/**
 * Holds Settings for the user
 * @author Maxim Tiourin
 */
public class Settings {
	public Settings() {
		
	}
	
	public void init() {
		screengrab_hotkey_enabled = Config.get().booleanAt("Screen Grabber Settings", "hotkey_enabled");
		screengrab_hotkey = Config.get().stringAt("Screen Grabber Settings", "hotkey");
		screengrab_hotkey_text = Config.get().stringAt("Screen Grabber Settings", "hotkey_text");
	}
	
	public static Settings get() {
		return settings;
	}
	
	private static final Settings settings = new Settings();
	
	//Non-Modifiable
	public static int MOUSE_LEFT = 1;
	public static int MOUSE_RIGHT = 3;
	public static int MOUSE_MIDDLE = 2;
	
	//Modifiable
	public static boolean screengrab_hotkey_enabled = false;
	public static String screengrab_hotkey = "121"; //F10
	public static String screengrab_hotkey_text = "F10"; //F10
}
