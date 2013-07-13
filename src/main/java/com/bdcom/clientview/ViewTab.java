package com.bdcom.clientview;

import java.awt.Component;

import javax.swing.Icon;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-11-23 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public interface ViewTab {
	
	public void setTabTitle(String tabTitle);

	public String getTabTitle();
	
	public void setTabIcon(Icon tabIcon);
		
	public Icon getTabIcon();
	
	public Component getTabComponent();
	
	public void setTabTip(String tabTip);
	
	public String getTabTip();
	
}
