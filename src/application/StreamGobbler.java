/*
 * Created on Jun 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 * Copyright Kelly Wiles 2005, 2006, 2007, 2008
 */
package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class StreamGobbler extends Thread {
	InputStream is = null;
	StringBuilder sb =new StringBuilder();
	Object obj = false;
	boolean allTextMode = false;
    
    StreamGobbler(InputStream is, Object obj) {
        this.is = is;
        this.obj = obj;
    }
    
    public void run() {
    	
    	try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
			String line = null;
			
            while ( (line = br.readLine()) != null) {
        		if (obj != null && obj instanceof TextArea) {
        			final String txt = line;
        			Platform.runLater(new Runnable() {
        				@Override
        				public void run() {
//        					OutputInterface oi = (OutputInterface)obj;
//                			oi.addText(txt + "\n", true);
        					TextArea ta = (TextArea)obj;
        					ta.appendText(txt + "\n");
                			System.out.println(">> " + txt);
        				}
        			});
        		} else {
            		sb.append(" " + line + "\n");
//            		System.out.println(line);
        		}
        		try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
            br.close();
        } catch (IOException ioe) {
                ioe.printStackTrace();  
        }
    }
    
    public String getOutput() {
    	return sb.toString();
    }
}
