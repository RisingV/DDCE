package com.bdcom.dce.view.resource;

import com.bdcom.dce.biz.pojo.Script;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.StringUtil;
import com.bdcom.dce.view.util.GBC;
import com.bdcom.dce.view.util.LimitedDocument;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-10    <br/>
 * Time: 14:11  <br/>
 */
public class ScriptEditDialog extends JDialog implements ApplicationConstants {

    private static final long serialVersionUID = -1452979166462120131L;

    private static final String SCRIPT_NAME = "Script Name";
    private static final String SCRIPT_PATH = "First Script Path";
    private static final String SECOND_SCRIPT_PATH = "Second Script Path";
    private static final String CANCEL = "cancel";
    private static final String CONFIRM = "confirm";
    private static final String BASIC_INFO = "Basic Info";
    private static final String CONTENT = "Content";

    private final JComponent fatherFrame;

    private JDialog dialog = this;
    private JButton confirmBt;
    private JButton cancelBt;

    private JLabel nameLabel;
    private JLabel serialLabel;
    private JLabel beginIndexLabel;

    private JTextField nameField;
    private JTextField serialField;
    private JTextField beginIndexField;

    private JLabel pathLabel;
    private JLabel secondPathLabel;
    private JTextField pathField;
    private JTextField secondPathField;

    private JPanel basicPane;
    private JPanel contentPane;
    private JPanel buttonPane;

    private ActionListener confirmAction;

    public ScriptEditDialog(JComponent fatherFrame) {
        this.fatherFrame = fatherFrame;
        initUI();
    }

    private void initUI() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (null != fatherFrame) {
                    fatherFrame.setEnabled(true);
                }
            }
        });

        initBasicPane();
        initContentPane();
        initButtonPane();

        setLayout( new GridBagLayout() );
        add( basicPane, new GBC(0, 0) );
        add( contentPane, new GBC(0, 1) );
        add( buttonPane, new GBC(0, 2) );

    }

    private void initContentPane() {
        String path = LocaleUtil.getLocalName( SCRIPT_PATH );
        String secondPath = LocaleUtil.getLocalName( SECOND_SCRIPT_PATH );
        String content = LocaleUtil.getLocalName( CONTENT );

        pathLabel = new JLabel( path );
        secondPathLabel = new JLabel( secondPath );
        pathLabel.setPreferredSize(new Dimension(120, 20));
        secondPathLabel.setPreferredSize(new Dimension(120, 20));

        pathField = new JTextField();
        secondPathField = new JTextField();
        pathField.setPreferredSize(new Dimension(380, 20));
        secondPathField.setPreferredSize(new Dimension(380, 20));

        Border titledBorder = BorderFactory.createTitledBorder( content );
        contentPane = new JPanel();
        contentPane.setBorder(  titledBorder );
        contentPane.setLayout(new GridBagLayout());
        contentPane.setPreferredSize(new Dimension(555, 100));
        contentPane.add(pathLabel, new GBC(0, 0).setInsets(5));
        contentPane.add(pathField, new GBC(1, 0).setInsets(5));
        contentPane.add(secondPathLabel, new GBC(0, 1).setInsets( 5 ) );
        contentPane.add(secondPathField, new GBC(1, 1).setInsets( 5 ) );
    }

    private void initBasicPane() {
        String name = LocaleUtil.getLocalName( SCRIPT_NAME );
        String serial = LocaleUtil.getLocalName( SERIAL_NUM );
        String beginIndex = LocaleUtil.getLocalName( BEGIN_INDEX );
        String basicInfo = LocaleUtil.getLocalName( BASIC_INFO  );

        nameLabel = newLabel( name );
        serialLabel = newLabel( serial );
        beginIndexLabel = newLabel( beginIndex );

        nameField = newField();
        serialField = newField();
        beginIndexField = newField();

        beginIndexField.setDocument( new LimitedDocument( LimitedDocument.NUMBER, 2) );

        Border titledBorder = BorderFactory.createTitledBorder( basicInfo );
        basicPane = new JPanel();
        basicPane.setBorder( titledBorder );
        basicPane.setLayout( new GridBagLayout() );
        basicPane.add( nameLabel, new GBC(0, 0).setAnchor( GBC.WEST )
                .setInsets( 4, 8, 4, 8)
                .setFill( GBC.HORIZONTAL ) );
        basicPane.add( nameField, new GBC(1, 0).setAnchor( GBC.WEST )
                .setInsets( 4, 8, 4, 8) );
        basicPane.add( serialLabel, new GBC(2, 0).setAnchor( GBC.WEST )
                .setInsets( 4, 8, 4, 8)
                .setFill( GBC.HORIZONTAL ) );
        basicPane.add(serialField, new GBC(3, 0).setAnchor(GBC.WEST)
                .setInsets(4, 8, 4, 8));

        basicPane.add(beginIndexLabel, new GBC(0, 1).setAnchor(GBC.WEST)
                .setInsets(4, 8, 4, 8)
                .setFill(GBC.HORIZONTAL));
        basicPane.add(beginIndexField, new GBC(1, 1).setAnchor(GBC.WEST)
                .setInsets(4, 8, 4, 8));
    }

    private void initButtonPane() {

        String confirm = LocaleUtil.getLocalName( CONFIRM );
        String cancel = LocaleUtil.getLocalName(CANCEL);

        confirmBt = new JButton( confirm );
        cancelBt = new JButton( cancel );
        confirmBt.setPreferredSize( new Dimension(70, 30) );
        cancelBt.setPreferredSize( new Dimension(70, 30) );

        cancelBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        buttonPane = new JPanel();
        buttonPane.setLayout( new GridBagLayout() );
        buttonPane.add(confirmBt, new GBC(0, 0).setInsets(10));
        buttonPane.add( cancelBt, new GBC(1, 0).setInsets( 10 ) );

    }

    private void updateBasicPane(Script script) {
        if ( null == script ) {
            return;
        }
        String name = script.getRemarkName();
        String serial = script.getSerial();
        String beginIndex = String.valueOf( script.getBeginIndex() );

        resetTextField( nameField, name );
        resetTextField( serialField, serial );
        resetTextField( beginIndexField, beginIndex );
    }

    public Script gatherAsScript() {
        return gatherAsScript( new Script() );
    }

    public Script gatherAsScript(Script script) {
        if ( null == script ) {
            script = new Script();
        }

        String name = nameField.getText();
        String serial = serialField.getText();
        String indexStr = beginIndexField.getText();

        int beginIndex = 0;
        if ( StringUtil.isNotBlank(indexStr) ) {
            beginIndex = Integer.parseInt( indexStr );
        }
        String path = pathField.getText();
        String secondPath = secondPathField.getText();

        script.setRemarkName( name );
        script.setSerial( serial );
        script.setBeginIndex( beginIndex );
        script.setPath( path );
        script.setSecondPath( secondPath );

        return script;
    }

    public void setConfirmAction(ActionListener al) {
        if ( null != confirmAction ) {
            confirmBt.removeActionListener( confirmAction );
        }
        confirmAction = al;
        confirmBt.addActionListener( al );
    }

    public void display(Script script) {
        update( script );
        display();
    }

    public void update(Script script) {
        updateBasicPane( script );
        resetTextField( pathField, script.getPath() );
    }

    public void display() {
        if ( null != fatherFrame ) {
            fatherFrame.setEnabled( false );
        }
        dialog.setAlwaysOnTop(true);
        dialog.pack();
        dialog.setResizable( false );
        dialog.setLocationRelativeTo( null );
        dialog.setVisible( true );
    }

    public void clear() {
        resetTextField( nameField, "" );
        resetTextField( serialField, "" );
        resetTextField( beginIndexField, "0" );
        resetTextField( pathField, "" );
    }

    public void close() {
        if ( null != fatherFrame ) {
            fatherFrame.setEnabled( true );
        }
        dialog.setVisible( false );
    }

    private void resetTextField( JTextField jtf, String text ) {
        if ( null != text && null != jtf ) {
            if ( !text.equals( jtf.getText() ) ) {
                jtf.setText( text );
            }
        }
    }

    private JLabel newLabel(String name) {
        JLabel label = new JLabel( name );
        label.setPreferredSize( new Dimension( 80, 20 ) );
        return label;
    }

    private JTextField newField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(160, 20));
        return field;
    }

}
