package com.fizzikgames.d3b;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

import com.fizzikgames.d3b.utility.StringUtil;

/**
 * The Configuration class loads the configuration file and then offers static methods for getting its contents.
 * @author Maxim Tiourin
 */
public class Config {
	Config() {
		configString = new ArrayList<ArrayList<String>>();
		sectionLabels = new ArrayList<String>();
	}
	
	public void init() {
		File cfile = new File(path);
		File ocfile = new File(backup);
		try {
			if (cfile.exists()) {
				readConfig(cfile);
				if (!isSameVersion()) {
					if (ocfile.exists()) ocfile.delete();
					cfile.renameTo(ocfile);
					cfile = new File(path);
					
					//Create new config file
					createNewConfigFromBackup(cfile, ocfile);
					readConfig(cfile);
				}
			}
			else {
				createNewConfig(cfile);
				readConfig(cfile);
			}
		}
		catch (IOException e) {}
	}
	
	/**
	 * Creates a brand new config file with default values
	 */
	public void createNewConfig(File file) throws IOException {
		configString = new ArrayList<ArrayList<String>>();
		sectionLabels = new ArrayList<String>();
		
		if (file == null) {
			file = new File(path);
		}
		
		file.createNewFile();
		
		FileOutputStream fout = new FileOutputStream(file);
		PrintStream out = new PrintStream(fout);
		
		//Default Config file
		out.println("[Information]");
		out.println("version=" + D3B.VERSION);
		out.println("[Display Settings]");
		out.println("width=" + D3B.WINDOW_WIDTH);
		out.println("height=" + D3B.WINDOW_HEIGHT);
		out.println("maximized=" + D3B.MAXIMIZED);
		out.println("[Screen Grabber Settings]");
		out.println("hotkey_enabled=" + Settings.screengrab_hotkey_enabled);
		out.println("hotkey=" + Settings.screengrab_hotkey);
		out.println("hotkey_text=" + Settings.screengrab_hotkey_text);
		
		out.close();
	}
	
	/**
	 * Creates a brand new config file while using some values from the backup
	 */
	public void createNewConfigFromBackup(File file, File ocfile) throws IOException {
		configString = new ArrayList<ArrayList<String>>();
		sectionLabels = new ArrayList<String>();
		
		readConfig(ocfile);
		
		if (file == null) {
			file = new File(path);
		}
		
		file.createNewFile();
		
		FileOutputStream fout = new FileOutputStream(file);
		PrintStream out = new PrintStream(fout);
		
		//Default Config file
		out.println("[Information]");
		out.println("version=" + D3B.VERSION);
		out.println("[Display Settings]");
		out.println("width=" + integerAt("Display Settings", "width"));
		out.println("height=" + integerAt("Display Settings", "height"));
		out.println("maximized=" + booleanAt("Display Settings", "maximized"));
		out.println("[Screen Grabber Settings]");
		out.println("hotkey_enabled=" + booleanAt("Screen Grabber Settings", "hotkey_enabled"));
		out.println("hotkey=" + stringAt("Screen Grabber Settings", "hotkey"));
		out.println("hotkey_text=" + stringAt("Screen Grabber Settings", "hotkey_text"));
		
		out.close();
		
		ocfile.delete();
		configString = new ArrayList<ArrayList<String>>();
		sectionLabels = new ArrayList<String>();
	}
	
	/**
	 * Reads configuration file into data structeres
	 */
	private void readConfig(File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fin);
		BufferedReader in = new BufferedReader(isr);
		
		String line = "";
		while ((line = in.readLine()) != null) {
			if (line.contains("[")) {
				configString.add(new ArrayList<String>());
				sectionLabels.add(StringUtil.substring(line, "[", "]", true));
			}
			else {
				configString.get(Math.max(configString.size() - 1, 0)).add(line);
			}
		}
		
		in.close();
	}
	
	/**
	 * Checks if config versions match
	 */
	public boolean isSameVersion() {
		String current = "" + D3B.VERSION;
		String old = valueAt("Information", "version");
		
		if (current.equals(old)) return true;
		
		return false;
	}
	
	public ArrayList<ArrayList<String>> getEntireStringConfig() {
		return configString;
	}
	
	private String valueAt(String section, String property) {
		for (int i = 0; i < configString.size(); i++) {
			if (sectionLabels.get(i).equals(section)) {
				for (String e : configString.get(i)) {
					if (e.contains(property)) {
						String cmp[] = e.split("=");
						if (cmp[0].equals(property)) {
							return StringUtil.substring(e, "=", true);
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public String stringAt(String section, String property) {
		return valueAt(section, property);
	}
	
	public int integerAt(String section, String property) {
		return Integer.valueOf(valueAt(section, property));
	}
	
	public boolean booleanAt(String section, String property) {
		return Boolean.parseBoolean(valueAt(section, property));
	}
	
	public float floatAt(String section, String property) {
		return Float.valueOf(valueAt(section, property));
	}
	
	public static Config get() {
		return config;
	}
	
	private static final Config config = new Config();
	private ArrayList<ArrayList<String>> configString;
	private ArrayList<String> sectionLabels;
	private String path = "config.ini";
	private String backup = "config_old.ini";
}
