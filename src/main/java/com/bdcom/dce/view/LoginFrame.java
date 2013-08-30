package com.bdcom.dce.view;

import com.bdcom.dce.biz.pojo.LoginAuth;
import com.bdcom.dce.nio.client.ClientProxy;
import com.bdcom.dce.nio.exception.GlobalException;
import com.bdcom.dce.nio.exception.ResponseException;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.sys.configure.ServerConfig;
import com.bdcom.dce.sys.gui.GuiInterface;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.StringUtil;
import com.bdcom.dce.util.logger.ErrorLogger;
import com.bdcom.dce.view.util.GBC;
import com.bdcom.dce.view.util.MessageUtil;
import com.bdcom.dce.view.util.MsgDialogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.bdcom.dce.view.util.Messages.BLANK_PASSWD;
import static com.bdcom.dce.view.util.Messages.BLANK_USR_NAME;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-11-14 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class LoginFrame extends TopLevelFrame implements ApplicationConstants {

	private static final long serialVersionUID = 2496780458803597888L;
	
	private JFrame thisFrame = this;
	
	private JLabel usrLabel;
	
	private JLabel pwdLabel;
	
	private JLabel ipLabel;
	
	private JLabel portLabel;
	
	private JTextField usrTextField;
	
	private JTextField pwdTextField;
	
	private JTextField ipTextField;
	
	private JTextField portTextField;
	
	private JButton loginBt;
	
	private JButton exitBt;
	
	private Image image;

    private final ClientProxy clientProxy;

    private final GuiInterface app;

    private ServerConfig serverConfig;

    private AbstractFrame frameAfterLogin;


	public LoginFrame(ClientProxy clientProxy, GuiInterface app) {
        super(app);
        this.app = app;

        this.clientProxy = clientProxy;
        this.serverConfig = clientProxy.getServerConfig();

		initCompos();
		initLayout();
	}

    public void setFrameAfterLogin(AbstractFrame frame) {
        frameAfterLogin = frame;
    }

	public void setImage(Image image) {
		this.image = image;
	}
	
	public void display0() {
		this.setIconImage(image);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		
		usrTextField.setText("");
		pwdTextField.setText("");
		this.setVisible(true);
	}
	
	public void hideFrame() {
		thisFrame.setVisible(false);
	}
	
	public Component getSelfFrame() {
		return thisFrame;
	}
	
	private void initLayout() {
		Container con = this.getContentPane();
		con.setLayout(new GridBagLayout());
		
		JPanel textPanel = new JPanel();
		JPanel btPanel = new JPanel();
		textPanel.setLayout(new GridBagLayout()); 
		btPanel.setLayout(new GridBagLayout()); 
		
		textPanel.add(usrLabel, new GBC(0, 0)
					.setInsets(20, 50, 10, 10) 
					.setFill(GBC.BOTH)
					);
		textPanel.add(usrTextField, new GBC(1,0) 
					.setInsets(20, 10, 10, 50) 
					.setFill(GBC.BOTH)
					);
		textPanel.add(pwdLabel, new GBC(0, 1) 
					.setInsets(12, 50, 20, 10) 
					.setFill(GBC.BOTH)
					);
		textPanel.add(pwdTextField, new GBC(1, 1) 
					.setInsets(12, 10, 20, 50) 
					.setFill(GBC.BOTH)
					);
		textPanel.add(ipLabel, new GBC(0, 2)
					.setInsets(20, 50, 10, 10) 
					.setFill(GBC.BOTH)
					);
		textPanel.add(ipTextField, new GBC(1, 2)
					.setInsets(20, 10, 10, 50) 
					.setFill(GBC.BOTH)
					);
		textPanel.add(portLabel, new GBC(0, 3)
					.setInsets(20, 50, 10, 10) 
					.setFill(GBC.BOTH)
					);
		textPanel.add(portTextField, new GBC(1, 3)
					.setInsets(20, 10, 10, 50) 
					.setFill(GBC.BOTH)
					);
		btPanel.add(loginBt, new GBC(0, 2) 
					.setInsets(10, 55, 10, 14) 
					.setFill(GBC.BOTH)
					);
		btPanel.add(exitBt, new GBC(1, 2) 
					.setInsets(10, 14, 10, 55) 
					.setFill(GBC.BOTH)
					);
		
		con.add(textPanel, new GBC(0, 0) ); 
		con.add(btPanel, new GBC(0, 1) );
	}
	
	private void initCompos() {
		this.setTitle(
				LocaleUtil.getLocalName(SYS_NAME)
				);
		
		usrLabel = new JLabel();
		pwdLabel = new JLabel();
		ipLabel = new JLabel();
		portLabel = new JLabel();
		usrLabel.setPreferredSize( new Dimension(80,20) );
		pwdLabel.setPreferredSize( new Dimension(80,20) );
		ipLabel.setPreferredSize( new Dimension(80, 20) );
		portLabel.setPreferredSize( new Dimension(80, 20) );
		usrLabel.setText(
				getLocalName(USER_NAME)
				);
		pwdLabel.setText(
				getLocalName(PASS_WORD)
				);
		ipLabel.setText(
				getLocalName(IP_ADDR)
				);
		portLabel.setText(
				getLocalName(_PORT)
				);
		
		
		usrTextField = new JTextField();
		pwdTextField = new JPasswordField();
		ipTextField = new JTextField();
		portTextField = new JTextField();
		ipTextField.setPreferredSize( new Dimension(160, 20) );
		portTextField.setPreferredSize( new Dimension(160, 20) );
		usrTextField.setPreferredSize( new Dimension(160, 20) );
		pwdTextField.setPreferredSize( new Dimension(160, 20) );

		ipTextField.setText(
                serverConfig.getInetAddr().getHostAddress()
				);
		portTextField.setText(
				String.valueOf( serverConfig.getPort() )
				);
		
		loginBt = new JButton();
		exitBt = new JButton();
		loginBt.setText(
				getLocalName(LOG_IN)
				); 
		exitBt.setText(
				getLocalName(_EXIT)
				);
		
		loginBt.setPreferredSize(new Dimension(80, 25));
		exitBt.setPreferredSize(new Dimension(80, 25));
		
		loginBt.addActionListener(
					new ActionListener() {

						public void actionPerformed(ActionEvent e) {
							doLogin();
						}
						
					}
				);
		exitBt.addActionListener(
					new ActionListener() {

						public void actionPerformed(ActionEvent e) {
                            app.terminal();
						}
						
					}
				);
        this.getRootPane().setDefaultButton( loginBt );
	}
	
	private String getLocalName(String name) {
		return LocaleUtil.getLocalName(name);
	}
	
	private void doLogin() {
		boolean nullError = false;
		String usrName = usrTextField.getText();
		String passwd = pwdTextField.getText();
		String ip = ipTextField.getText();
		String port = portTextField.getText();
		
		if ( !StringUtil.isNotBlank(usrName) ) {
			MsgDialogUtil.showErrorDialog(
					MessageUtil.getLocalisedMessage(BLANK_USR_NAME)
					);
			nullError = true;
		}
		
		if ( !StringUtil.isNotBlank(passwd) ) {
			MsgDialogUtil.showErrorDialog(
					MessageUtil.getLocalisedMessage(BLANK_PASSWD)
					);
			nullError = true;
		}
		
		if ( !StringUtil.isNotBlank(ip) ) {
			MsgDialogUtil.showErrorDialog(
					MessageUtil.getLocalisedMessage(NULL_IP)
					);
			nullError = true;
		}
		
		if ( !StringUtil.isNotBlank(port) ) {
			MsgDialogUtil.showErrorDialog(
					MessageUtil.getLocalisedMessage(NULL_PORT)
					);
			nullError = true;
		}
		
		if ( !StringUtil.isValidIp(ip) ) {
			MsgDialogUtil.showErrorDialog(
					MessageUtil.getLocalisedMessage(INVAILD_IP)
					);
			nullError = true;
		}
		
		if ( !StringUtil.isValidNumber(port) ) {
			MsgDialogUtil.showErrorDialog(
					MessageUtil.getLocalisedMessage(INVAILD_PORT)
					);
			nullError = true;
		}
		
		if ( nullError ) {
			return;
		}
		
		LoginAuth auth = new LoginAuth();
		auth.setUserName(usrName);
		auth.setUserPasswd(passwd);

        clientProxy.getServerConfig()
                .writeToConfigFile(ip, port);

        boolean isSuccess = true;
		int status = -1;

        try {
            status = clientProxy.sendLoginAuth( auth );
        } catch (TimeoutException e) {
            isSuccess = false;
            String msg = "Login Request Time Out!";
            MsgDialogUtil.showErrorDialog( msg );
            ErrorLogger.log(msg);
        } catch (IOException e) {
            isSuccess = false;
            String msg = e.getMessage();
            MsgDialogUtil.showErrorDialog(msg);
            ErrorLogger.log(msg);
            isSuccess = false;
        } catch (ResponseException e) {
            isSuccess = false;
            String msg = e.getMessage();
            MsgDialogUtil.showMsgDialog( msg );
            ErrorLogger.log(msg);
        } catch (GlobalException e) {
            isSuccess = false;
            MsgDialogUtil.reportGlobalException( e );
            ErrorLogger.log(e.getMessage());
        } finally {
            if ( !isSuccess ) {
                clientProxy.shutdown();
            }
        }

        if ( isSuccess ) {
            registerUser(usrName, status);
            showFrameAfterLogin();
        }

//        if ( status <= 0 ) {
//			MsgDialogUtil.showErrorDialog(
//					MessageUtil.getMessageByStatusCode(status)
//					);
//		} else {
//		}
	}

	private void showFrameAfterLogin() {
		thisFrame.setVisible(false);

        frameAfterLogin.display();
        frameAfterLogin.refresh();
	}
	
	private void registerUser(String userName, int status) {
        app.addAttribute( USER.USER_NUM, userName );
        if ( LOGIN.ROOT == status ) {
            app.addAttribute( USER.USER_RANK, USER.ROOT );
            app.addAttribute( USER.SUPERVISOR, Boolean.TRUE );
        } else {
            app.addAttribute( USER.USER_RANK, USER.COMMON_USER );
            app.addAttribute( USER.SUPERVISOR, Boolean.FALSE );
        }

		String loginMsg = LocaleUtil.getLocalName(LOGIN_MSG);
        MsgTable msgTable = (MsgTable) app.getAttribute(COMPONENT.MSG_TABLE);
        msgTable.addSysMsg( userName + loginMsg );
	}


	@Override
	public void refresh() {
		// nothing to do
	}
	
}
