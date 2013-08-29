package com.bdcom.itester.lib;

import com.bdcom.sys.config.PathConfig;
import com.bdcom.util.log.ErrorLogger;

import java.io.*;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2013-6-24 <br>
 * Auto-Generated by eclipse Kepler <br>
 */

public class ITesterLibLoader {
	
	private static final String DLL_EXT = ".dll";
	
	private static native void loadITesterDllLib(String path);
	
	public static void registerNatives(PathConfig pathConfig) {
		String itesterDllPath = extractLibFromJar( pathConfig, "/","iTesterLib" );
		if ( null != itesterDllPath ) {
			String navtiveLibPath = extractLibFromJar( pathConfig, "/", "iTestLibLoader" );
			if ( null != navtiveLibPath && platformCheck() ) {
				System.load( navtiveLibPath );
				loadITesterDllLib( itesterDllPath );
			}
		}
	}

    private static boolean platformCheck() {
        String os = System.getProperty( "os.name" );
        if ( os.indexOf( "Windows") > 0 || os.indexOf( "windows") > 0 ) {
            return true;
        }
        return false;
    }

	private static String extractLibFromJar(PathConfig pathConfig, String pathInJar, String libName) {
		if ( libName.indexOf(DLL_EXT) < 0 ) {
			libName = libName + DLL_EXT;
		}
		File dllFile = new File( pathConfig.getDllLibPath() + libName  );
		boolean extractSuccess = dllFile.exists();
		if ( !extractSuccess ) {
			InputStream in = ITesterLibLoader.class.getResourceAsStream( pathInJar + libName );
			BufferedInputStream reader = new BufferedInputStream(in);  
			FileOutputStream writer = null;
			int bufferSize = 1024;
			
			try {
				extractSuccess = dllFile.createNewFile();
				if ( extractSuccess ) {
					writer = new FileOutputStream( dllFile );
		            byte[] buffer = new byte[bufferSize];  
		              
		            while (reader.read(buffer) > 0){  
		                writer.write(buffer);  
		                buffer = new byte[bufferSize];  
		            }  
				}
			} catch (FileNotFoundException e) {
				ErrorLogger.log(e.getMessage());
				extractSuccess = false;
			} catch (IOException e) {
				ErrorLogger.log(e.getMessage());
				extractSuccess = false;
			}  finally {
				try {
					if ( null != in ) {
						in.close();
					}
					if ( null != writer ) {
						writer.close();
					}
				} catch (IOException e) {
					ErrorLogger.log(e.getMessage());
				}
			}
		}
		
		if ( extractSuccess ) {
			return dllFile.getAbsolutePath();
		} else {
			return null;
		}
	}

//    static {
//        String currDir = ApplicationConstants.RUN_TIME.CURRENT_DIR;
//        PathConfig pathConfig = new PathConfig(currDir);
//        registerNatives(pathConfig);
//    }

	/**
	 * @param ipAddr iTesterServer IP Address 
	 * */
	public final native CommuStatus connectToServer(String ipAddr);

    /**
     * @param socketId return by <code>connectToServer(String ipAddr)</code>
     * */
    public final native int disconnectToServer(int socketId);

 	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * */
	public final native ChassisInfo getChassisInfo(int socketId);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * */
	public final native CardInfo getCardInfo(int socketId, int cardId);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * */
	public final native EthPhyProper getEthernetPhysical(int socketId, int cardId, int portId);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * */
	public final native int clearStatReliably(int socketId, int cardId, int portId);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int setHeader(int socketId, int cardId, int portId, 
			int validStreamCount, int length, int[] strHead);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int setPayload(int socketId, int cardId, int portId,
			int length, int data, int payloadType);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int setDelayCount(int socketId, int cardId, int portId, int delayCount);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int setTxMode(int socketId, int cardId, int portId, int mode, int burstNum);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int startPort(int socketId, int cardId, int portId);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int stopPort(int socketId, int cardId, int portId);

	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native PortStats getPortAllStats(int socketId, int cardId, int portId, int length);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * */
	public final native LinkStatus getLinkStatus(int socketId, int cardId, int portId); 
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * */
	public final native WorkInfo getWorkInfo(int socketId, int cardId, int portId );
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int setUsedState(int socketId, int cardId, int portId, int usedState);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native UsedState getUsedState(int socketId, int cardId, int portId); 
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int setStreamId(int socketId, int cardId, int portId, int iStartId, int iIdNum);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int setEthernetPhysicalForATT(int socketId, int cardId, int portId,
			int nego, int ethPhySpeed, int fullDuplex, int loopback );
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int setFramLengthChange(int socketId, int cardId, int portId, int isChange);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int loadFPGA(int socketId, int cardId, int ethPhySpeed);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int resetFPGA(int socketId, int cardId);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * */
	public final native StreamInfo getStreamSendInfo(int socketId, int cardId, int portId, int streamId);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * */
	public final native StreamInfo getStreamRecInfo(int socketId, int cardId, int portId, int streamId);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * 
	 * @return 0: connect success <br>
	 *		    1: connect fail
	 * */
	public final native int startCapture(int socketId, int cardId, int portId);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * */
	public final native CaptureResult stopCapture(int socketId, int cardId, int portId);
	
	/**
	 * @param socketId return by <code>connectToServer(String ipAddr)</code>
	 * */
	public final native int setStreamLength(int socketId, int cardId, int portId, int streamId, int length);
	
}






















