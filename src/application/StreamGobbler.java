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

public class StreamGobbler extends Thread {
	InputStream is = null;
	StringBuilder sb =new StringBuilder();
	boolean saveOutput = false;
	boolean allTextMode = false;
    
    StreamGobbler(InputStream is, boolean saveOutput) {
        this.is = is;
        this.saveOutput = saveOutput;
    }
    
    public void run() {
    	
    	try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
			String line = null;
			
            while ( (line = br.readLine()) != null) {
            	if (saveOutput) {
            		sb.append(line);
//            		System.out.println(line);
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
