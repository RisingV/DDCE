package com.bdcom.dce.view.scripttest;

import com.bdcom.dce.sys.AppContent;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.sys.configure.ScriptEnvConfig;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.StringUtil;
import com.bdcom.dce.view.util.GBC;
import com.bdcom.dce.view.util.MsgDialogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-11    <br/>
 * Time: 14:52  <br/>
 */
public class ScriptEnvConfigDialog extends JDialog
        implements ApplicationConstants {

    private static final String BLANK_PATH = "path is blank!";
    private static final String FILE_NOT_FOUND = "file not found";

    private static final String CANCEL = "cancel";
    private static final String CONFIRM = "confirm";

    private final JComponent fatherFrame;
    private final AppContent app;

    private JDialog dialog = this;

    private JLabel crtPathLabel;
    private JTextField crtPathField;

    private JButton confirmBt;
    private JButton cancelBt;

    private JPanel contentPane;
    private JPanel buttonPane;

    public ScriptEnvConfigDialog(AppContent app, JComponent fatherFrame) {
        this.app = app;
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

        initContentPane();
        initButtonPane();

        setLayout( new GridBagLayout() );
        add( contentPane, new GBC(0, 0) );
        add( buttonPane, new GBC(0, 1) );
    }

    private void initContentPane() {
        String crtPath = LocaleUtil.getLocalName( SCRIPT_IPT_PATH  );

        crtPathLabel = new JLabel( crtPath );
        crtPathField = new JTextField();
        crtPathField.setPreferredSize( new Dimension( 320, 20 ) );

        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        contentPane.add( crtPathLabel, new GBC(0, 0).setInsets(5, 10, 5, 10) );
        contentPane.add( crtPathField, new GBC(1, 0).setInsets(5, 10, 5, 10) );

    }

    private void initButtonPane() {
        String cancel = LocaleUtil.getLocalName( CANCEL );
        String confirm = LocaleUtil.getLocalName( CONFIRM );

        confirmBt = new JButton( confirm );
        cancelBt = new JButton( cancel );
        confirmBt.setPreferredSize( new Dimension(70, 30) );
        cancelBt.setPreferredSize( new Dimension(70, 30) );

        confirmBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( pathValidation() ) {
                    ScriptEnvConfig config = (ScriptEnvConfig)
                            app.getAttribute( CONFIG.SCRIPT_ENV_CONFIG );
                    String path = crtPathField.getText();
                    config.setSecureCrtPath( path );
                    config.saveToFile();
                    close();
                }
            }
        });

        cancelBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        buttonPane = new JPanel();
        buttonPane.setLayout( new GridBagLayout() );
        buttonPane.add( confirmBt, new GBC(0, 0).setInsets(5, 10, 5, 10) );
        buttonPane.add( cancelBt, new GBC(1, 0).setInsets( 5, 10, 5, 10) );

    }

    private boolean pathValidation() {
        String path = crtPathField.getText();
        if ( !StringUtil.isNotBlank( path ) ) {
            String msg = LocaleUtil.getLocalName( BLANK_PATH  );
            MsgDialogUtil.showMsgDialog( dialog , msg );
            return false;
        }

        File f = new File( path );
        if ( !f.exists() ) {
            String msg = LocaleUtil.getLocalName( FILE_NOT_FOUND );
            MsgDialogUtil.showMsgDialog( dialog , msg + path );
            return false;
        }

        return true;
    }

    private void display0() {
        if ( null != fatherFrame ) {
            fatherFrame.setEnabled( false );
        }
        dialog.setAlwaysOnTop( true );
        dialog.pack();
        dialog.setResizable( false );
        dialog.setLocationRelativeTo( null );
        dialog.setVisible( true );
    }

    private void update() {
        ScriptEnvConfig config = (ScriptEnvConfig)
                app.getAttribute( CONFIG.SCRIPT_ENV_CONFIG );
        String oldPath = crtPathField.getText();
        String newPath = config.getSecureCrtPath();
        if ( !oldPath.equals( newPath ) ) {
            crtPathField.setText( newPath );
        }
    }

    public void display() {
        update();
        display0();
    }

    public void close() {
        if ( null != fatherFrame ) {
            fatherFrame.setEnabled( true );
        }
        dialog.setVisible( false );
    }

}
