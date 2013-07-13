package com.bdcom.clientview;

import com.bdcom.service.Application;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-12    <br/>
 * Time: 11:13  <br/>
 */
public abstract class TopLevelFrame extends JFrame implements AbstractFrame {

    abstract public void display0();

    @Override
    public void display() {
        Application.registerCurrentDisplay( this );
        display0();
    }
}
