package com.bdcom.dce.view.util;

import com.bdcom.dce.util.log.ErrorLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-11-27  <br>
 * Auto-Generated by eclipse Juno <br>
 */

public abstract class ViewUtil {
	
	public static void centerWindow(Component component) { 
        Toolkit kit = Toolkit.getDefaultToolkit();
	    Dimension screenSize = kit.getScreenSize();
	    int screenWidth = screenSize.width/2;
	    int screenHeight = screenSize.height/2;
	    int height = component.getHeight();
	    int width = component.getWidth();
	    
	    component.setLocation(screenWidth-width/2, screenHeight-height/2);
    } 
	
	public static Image getImage(String path) {
		Image im = null;
		try {
			im = ImageIO.read(ViewUtil.class.getResource(path));
		} catch (IOException e) {
			ErrorLogger.log(e.getMessage());
		}
		return im;
	}
	
}