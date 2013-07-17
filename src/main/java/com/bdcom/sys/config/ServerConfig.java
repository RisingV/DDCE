package com.bdcom.sys.config;

import com.bdcom.util.XmlUtil;
import com.bdcom.util.log.ErrorLogger;
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
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-15    <br/>
 * Time: 10:36  <br/>
 */
public class ServerConfig implements ServerInfoConstants {

    private static final String CONFIG_DIR = "Server" + File.separator;

    private static final String DEFAULT_NAME = "server.xml";

    private InetAddress inetAddr;

    private String ipAddrStr;

    private int port;

    private boolean hasInit = false;

    private File serverConfigFile;

    private SAXReader saxReader;

    private String defaultIP = DEFAULT_IP;

    private int defaultPort = Integer.parseInt( DEFAULT_PORT );

    private final PathConfig pathConfig;

    public ServerConfig(PathConfig pathConfig) {
        this.pathConfig = pathConfig;
    }

    public void setDefaultIP(String ip) {
        this.defaultIP = ip;
    }

    public void setDefaultPort(int port) {
        this.defaultPort = port;
    }

    private void init() {
        if ( !hasInit)  {
            serverConfigFile = new File( getConfigFileDir() +  DEFAULT_NAME);
            saxReader = new SAXReader();
        }
    }

    private void loadConfigFile() {
        init();
        String ipStr = null;
        String portStr = null;

        if ( serverConfigFile.exists() ) {
            String[] ipElemChain = {_IP};
            String[] portElemChain = {_PORT};
            ipStr = XmlUtil.getElemValue(ipElemChain, serverConfigFile);
            portStr = XmlUtil.getElemValue(portElemChain, serverConfigFile);
        } else {
            ipStr = DEFAULT_IP;
            portStr = DEFAULT_PORT;
            try {
                serverConfigFile.createNewFile();
            } catch (IOException e) {
                ErrorLogger.log(e.getMessage());
            } finally {
                if ( null != serverConfigFile && serverConfigFile.exists() ) {
                    String[] parentElemChain = { _SERVER };
                    String[] ipElemChain = { _IP };
                    String[] portElemChain = {_PORT};
                    String[] ipValueChain = { DEFAULT_IP };
                    String[] portValueChain = { DEFAULT_PORT };

                    XmlUtil.addElem(parentElemChain, ipElemChain, ipValueChain, serverConfigFile);
                    XmlUtil.addElem(parentElemChain, portElemChain, portValueChain, serverConfigFile);
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
            String msg = "com.bdcom.datadispacher.ServerInfo" +
                    " throws UnknownHostException when calling InetAddress.getByAddress(byte[] btyes)";
            ErrorLogger.log(msg);
        }
        port = Integer.parseInt(portStr);

        hasInit = true;
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

    public String getIpAddrStr() {
        if ( !hasInit) {
            loadConfigFile();
        }
        return ipAddrStr;
    }

    public InetAddress getInetAddr() {
        if ( !hasInit) {
            loadConfigFile();
        }
        return inetAddr;
    }

    public void writeToConfigFile(String ip, String port) {
        try {
            inetAddr = InetAddress.getByAddress( getIPBytes(ip) );
        } catch (UnknownHostException e) {
        }
        this.port = Integer.parseInt(port);

        Document document = null;
        try {
            document = saxReader.read(serverConfigFile);
        } catch (final DocumentException e) {
            final String msg = "com.bdcom.datadispacher.ServerInfo" +
                    " throws DocumentException when parsing xmlfile server.xml: "
                    + e.getMessage();

            ErrorLogger.log(msg);
        }

        final Element root = document.getRootElement();
        final Element ipElem = root.element("ip");
        final Element portElem = root.element("port");

        ipElem.setText(ip);
        portElem.setText(port);

        XMLWriter output = null;
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            output = new XMLWriter(new FileWriter(serverConfigFile), format);
            output.write(document);
            output.close();
        } catch (IOException e) {
            ErrorLogger.log(e.getMessage());
        }
    }

    public void setInetAddr(InetAddress ia) {
        inetAddr = ia;
        hasInit = false;
    }

    public int getPort() {
        if ( !hasInit) {
            loadConfigFile();
        }
        return port;
    }

    public void setPort(int _port) {
        port = _port;
        hasInit = false;
    }

    private byte[] getIPBytes(String str) {
        String [] ipStr = str.split("\\.");
        byte [] ipBuf = new byte[4];
        for( int i = 0 ; i < 4; i++ ){
            ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xFF);
        }
        return ipBuf;
    }

    private String getConfigFileDir() {
        StringBuffer sb = new StringBuffer();
        sb.append( pathConfig.getLocalConfDir() )
                .append(CONFIG_DIR);
        File sceDir = new File(sb.toString());
        if ( !sceDir.exists() ) {
            sceDir.mkdirs();
        }

        return sb.toString();
    }

}
