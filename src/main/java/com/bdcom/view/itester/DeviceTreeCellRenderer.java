package com.bdcom.view.itester;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-19    <br/>
 * Time: 15:14  <br/>
 */
public class DeviceTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, Object value,
                                                  boolean sel, boolean expanded,
                                                  boolean leaf, int row,
                                                  boolean hasFocus){
        JLabel label = (JLabel)super.getTreeCellRendererComponent(tree,value,
                sel,expanded,leaf,row,hasFocus);

        if ( value instanceof CustomizedNode ) {
            CustomizedNode cn = (CustomizedNode) value;

            label.setIcon( cn.getCustomizedIcon() );
            String desc = label.getText();
            String newDesc = cn.getProperDescription();

            if ( !desc.equals( newDesc ) ) {
                label.setText( newDesc );
            }
        }

        return label;
    }

}
