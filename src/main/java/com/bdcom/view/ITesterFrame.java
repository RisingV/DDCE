package com.bdcom.view;

import com.bdcom.itester.api.ITesterAPI;
import com.bdcom.sys.ApplicationConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-14    <br/>
 * Time: 15:45  <br/>
 */
public class ITesterFrame extends JPanel
        implements ViewTab, ApplicationConstants {

    private ITesterAPI iTesterAPI;

    @Override
    public void setTabTitle(String tabTitle) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getTabTitle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTabIcon(Icon tabIcon) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Icon getTabIcon() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Component getTabComponent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTabTip(String tabTip) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getTabTip() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
