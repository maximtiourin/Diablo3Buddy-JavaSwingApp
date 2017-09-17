package com.fizzikgames.d3b.utility;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * The updater handles the checking of and updating of D3B versions
 * @author Maxim Tiourin
 * @version 1.00
 */
public class Updater {
	protected static final Updater updater = new Updater();
	
	public Updater() {
		//saveFile("Fraps.exe", "http://www.fizzikgames.com/fraps.exe");
	}
	
	/**
	 * Saves the file at the specifed URL to the specified Path
	 */
	public void saveFile(String outpath, String url) {
		try {
        	BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        	FileOutputStream fout = new FileOutputStream(outpath);

            byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1)
            {
                    fout.write(data, 0, count);
            }
            
            in.close();
            fout.close();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }      
    }
	
	public static Updater get() {
		return updater;
	}
}
