package com.bdcom.view;

import com.bdcom.sys.gui.GuiInterface;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-12    <br/>
 * Time: 11:13  <br/>
 */
public abstract class TopLevelFrame extends JFrame implements AbstractFrame {

    abstract public void display0();

    private final GuiInterface gui;

    public TopLevelFrame(GuiInterface gui) {
        this.gui = gui;
    }

    @Override
    public void display() {
        gui.registerCurrentDisplay( this );
        display0();
    }
}
