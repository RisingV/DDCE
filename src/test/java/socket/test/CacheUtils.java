package socket.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-12-4<br>
 * Auto-Generated by eclipse Juno <br>
 */

public abstract class CacheUtils {
	public static void saveContentCache(Object tobeCache, String cacheFilePath) {
//		long ms = System.currentTimeMillis();
		File cacheFile = new File(cacheFilePath);
		if (!cacheFile.exists()) {
			try {
				cacheFile.createNewFile();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(cacheFile);
			ObjectOutputStream oos =  new ObjectOutputStream(fos);   
            oos.writeObject(tobeCache);
            oos.flush();
            oos.close();   
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
//		ms = System.currentTimeMillis() - ms;
//		System.out.println("Serializing file: "+ cacheFilePath 
//				+ " takes "+ ms + "ms");
	}
	
	public static Object getContentCache(String cacheFilePath) {
		File cacheFile = new File(cacheFilePath);
		
		return getContentCache(cacheFile);
	}
	
	public static Object getContentCache(File cacheFile) {
//		long ms = System.currentTimeMillis();
		Object cachedObj = null;
		if (!cacheFile.exists()) {
			return null;
		}
		
		try {
			FileInputStream fis = new FileInputStream(cacheFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			cachedObj = ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}
//		ms = System.currentTimeMillis() - ms;
//		System.out.println("Deserializing file: "+ cacheFilePath 
//				+ " takes "+ ms + "ms");
		
		return cachedObj;
	}
	
	public static void clearCache(String cacheFilePath) {
		File cacheFile = new File(cacheFilePath);
		if (cacheFile.exists()) {
			cacheFile.delete();
		}
	}
}