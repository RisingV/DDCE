package com.bdcom.sys.gui;

import com.bdcom.view.AbstractFrame;
import com.bdcom.sys.Applicable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-17    <br/>
 * Time: 11:04  <br/>
 */
public interface GuiInterface extends Applicable {

    public void registerCurrentDisplay(AbstractFrame frame);
    public AbstractFrame getCurrentDisplay();
    public AbstractFrame getFrame(String name);

}
