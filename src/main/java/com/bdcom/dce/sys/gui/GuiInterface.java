package com.bdcom.dce.sys.gui;

import com.bdcom.dce.view.AbstractFrame;
import com.bdcom.dce.sys.Applicable;

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
