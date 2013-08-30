package com.bdcom.dce.datadispacher;

import com.bdcom.dce.sys.configure.ServerInfoConstants;
import com.bdcom.dce.util.logger.ErrorLogger;
import com.bdcom.dce.sys.ConfigPath;
import com.bdcom.dce.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-10-31 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public abstract class ServerInfo implements ServerInfoConstants {

	private static final String CONFIG_DIR = "Server" + ConfigPath._SPT;
	
	private static final String DEFAULT_NAME = "server.xml";
	
	private static InetAddress inetAddr;

    private static String ipAddrStr;
	
	private static int port;
	
	private static boolean inited = false;
	
	private static File _confFile;
	
	private static SAXReader saxReader;

    private static void init() {
        if ( !inited )  {
            _confFile = new File( getConfigFileDir() +  DEFAULT_NAME);
            saxReader = new SAXReader();
        }
    }
	
	private static void getServerInfo() {
        init();
		String ipStr = null;
		String portStr = null;
//		String hostStr = null;
		
//		final InputStream in = getXmlStream();
		
		if ( _confFile.exists() ) {
			String[] ipElemChain = {_IP};
			String[] portElemChain = {_PORT};
			ipStr = XmlUtil.getElemValue(ipElemChain, _confFile);
			portStr = XmlUtil.getElemValue(portElemChain, _confFile);
		} else {
			ipStr = DEFAULT_IP;
			portStr = DEFAULT_PORT;
			try {
				_confFile.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e.getMessage());
			} finally {
				if ( null != _confFile && _confFile.exists() ) {
					String[] parentElemChain = { _SERVER };
					String[] ipElemChain = { _IP };
					String[] portElemChain = {_PORT};
					String[] ipValueChain = { DEFAULT_IP };
					String[] portValueChain = { DEFAULT_PORT };
					
					XmlUtil.addElem(parentElemChain, ipElemChain, ipValueChain, _confFile);
					XmlUtil.addElem(parentElemChain, portElemChain, portValueChain, _confFile);
				}
			}
		}

        ipAddrStr = ipStr;
		try {
			if ( null != ipStr ) {
				inetAddr = InetAddress.getByAddress( getIPBytes(ipStr) );
			} 
//			else if ( null != hostStr ) {
//				inetAddr = InetAddress.getByName(hostStr);
//			}
			else {
				inetAddr = InetAddress.getLocalHost();
			}
		} catch (UnknownHostException e) {
			String msg = "com.bdcom.dce.datadispacher.ServerInfo" +
					" throws UnknownHostException when calling InetAddress.getByAddress(byte[] btyes)";
			ErrorLogger.log(msg);
		}
		port = Integer.parseInt(portStr);
		
		inited = true;
	}

//	private static InputStream getXmlStream() {
//		String packageDirName = "com/bdcom/datadispacher";
//		Enumeration<URL> dirs;
//		try {
//			dirs = Thread.currentThread().getContextClassLoader().getResources(
//					packageDirName);
//			while (dirs.hasMoreElements()) {
//				URL url = dirs.nextElement();
//				String protocol = url.getProtocol();
//				if ("file".equals(protocol)) {
//					InputStream in = ServerInfo.class
//							.getResourceAsStream("/com/bdcom/datadispacher/server.xml");
//					if (null != in) {
//						return in;
//					}
//				} else if ("jar".equals(protocol)) {
//					JarFile jar;
//					jar = ((JarURLConnection) url.openConnection())
//							.getJarFile();
//					Enumeration<JarEntry> entries = jar.entries();
//					while (entries.hasMoreElements()) {
//						JarEntry entry = entries.nextElement();
//						String name = entry.getName();
//						if (name.endsWith("server.xml")) {
//							InputStream in = jar.getInputStream(entry);
//							if (null != in) {
//								return in;
//							}
//						}
//					}
//				}
//			}
//		} catch (IOException e) {
//		}
//		return null;
//	}

    public static String getIpAddrStr() {
        return ipAddrStr;
    }

	public static InetAddress getInetAddr() {
		if ( !inited ) {
			getServerInfo();
		}
		return inetAddr;
	}
	
	public static void setIpAndPort(String ip, String _port) {
		try {
			inetAddr = InetAddress.getByAddress( getIPBytes(ip) );
		} catch (UnknownHostException e) {
		}
		port = Integer.parseInt(_port);
		
		Document document = null;
		try {
			document = saxReader.read(_confFile);
		} catch (final DocumentException e) {
			final String msg = "com.bdcom.dce.datadispacher.ServerInfo" +
					" throws DocumentException when parsing xmlfile server.xml: "
                    + e.getMessage();

			ErrorLogger.log(msg);
		}
		
		final Element root = document.getRootElement();
		final Element ipElem = root.element("ip");
		final Element portElem = root.element("port");
		
		ipElem.setText(ip);
		portElem.setText(_port);
		
		XMLWriter output = null;   
	    OutputFormat format = OutputFormat.createPrettyPrint();  
	    try {
			output = new XMLWriter(new FileWriter(_confFile), format);
			output.write(document);
			output.close();
		} catch (IOException e) {
			ErrorLogger.log(e.getMessage());
		}
	}

	public static void setInetAddr(InetAddress ia) {
		inetAddr = ia;
		inited = false;
	}

	public static int getPort() {
		if ( !inited ) {
			getServerInfo();
		}
		return port;
	}

	public static void setPort(int _port) {
		port = _port;
		inited = false;
	}
	
	private static byte[] getIPBytes(String str) {
		String [] ipStr = str.split("\\.");
	    byte [] ipBuf = new byte[4];
	    for( int i = 0 ; i < 4; i++ ){
	        ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xFF);
	    }
	    return ipBuf;
	}
	
	private static String getConfigFileDir() {
		StringBuffer sb = new StringBuffer();
		sb.append(ConfigPath.getLocalConfDir())
		  .append(CONFIG_DIR);
		File sceDir = new File(sb.toString());
		if ( !sceDir.exists() ) {
			sceDir.mkdirs();
		}
		
		return sb.toString();
	}
}
