package com.bdcom.dce.biz.storage;

import com.bdcom.dce.sys.configure.PathConfig;
import com.bdcom.dce.util.SerializeUtil;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-6    <br/>
 * Time: 18:00  <br/>
 */
public class StoreMgr implements StorableMgr {

    private static final String STORAGE_DIR = "Storage";
    private static final String STORAGE_FILE = "store.dat";

    private Map<String, Set<Item>> remarkNameIndex = new HashMap<String, Set<Item>>();
    private Map<String, Storable> fullSerialIndex = new HashMap<String, Storable>(); //to serialize to file

    private final PathConfig pathConfig;
    private boolean storageLoaded = false;

    public StoreMgr(PathConfig pathConfig) {
        this.pathConfig = pathConfig;
    }

    @Override
    public Set<Item> getByRemarkName(String remark) {
        if ( null == remarkNameIndex ) {
            return null;
        }
        return remarkNameIndex.get( remark );
    }

    @Override
    public boolean isRemarkNameUsed(String remark) {
        if ( null == remarkNameIndex ) {
            return false;
        }
        Set<Item> itemSet = remarkNameIndex.get( remark );
        return ( null != itemSet ) && ( !itemSet.isEmpty() );
    }

    @Override
    public Item getByFullSerial(String serial) {
        if ( null == fullSerialIndex ) {
            return null;
        }
        return fullSerialIndex.get( serial );
    }

    @Override
    public boolean isSerialUsed(String serial) {
        if ( null == fullSerialIndex ) {
            return false;
        }
        return fullSerialIndex.containsKey( serial );
    }

    @Override
    public Set<Item> getBySerialMatching(String fullSerial) {
        if ( null != fullSerialIndex && !fullSerialIndex.isEmpty() ) {
            return null;
        }
        Set<Item> itemSet = new HashSet<Item>();
        Set<String> serialNumSet = fullSerialIndex.keySet();
        for (String subSerial : serialNumSet) {
            Item i = fullSerialIndex.get( subSerial );
            int beginIndex = i.getBeginIndex();
            if ( beginIndex + subSerial.length() > fullSerial.length() ) {
                continue;
            } else {
                String matchingSerial = fullSerial
                        .substring(beginIndex, beginIndex + subSerial.length());
                if (matchingSerial.equals(subSerial)) {
                    itemSet.add( i );
                }
            }
        }
        return itemSet;
    }

    @Override
    public void addItem(Item i) {
        if ( null == i ) {
            return;
        }
        if ( null == remarkNameIndex ) {
            remarkNameIndex = new HashMap<String, Set<Item>>();
        }
        if ( null == fullSerialIndex ) {
            fullSerialIndex = new HashMap<String, Storable>();
        }

        String remark = i.getRemarkName();
        String serial = i.getSerial();

        Set<Item> itemSet = remarkNameIndex.get( remark );
        if ( null == itemSet ) {
            itemSet = new HashSet<Item>();
            remarkNameIndex.put( remark, itemSet );
        }
        itemSet.add(i);

        fullSerialIndex.put( serial, (Storable) i);
    }

    @Override
    public void removeItem(Item i) {
        if ( null == i ) {
            return;
        }
        String remark = i.getRemarkName();
        String serial = i.getSerial();

        if ( null != remarkNameIndex ) {
            Set<Item> itemSet = remarkNameIndex.get( remark );
            if ( null != itemSet ) {
                itemSet.remove( i );
                if ( itemSet.isEmpty() ) {
                    remarkNameIndex.remove( itemSet );
                }
            }
        }
        if ( null != fullSerialIndex ) {
            fullSerialIndex.remove( serial );
        }
    }

    @Override
    public Item[] getAll() {
        if ( null == fullSerialIndex || fullSerialIndex.isEmpty() )  {
            return new Item[0];
        }
        int len = fullSerialIndex.size();
        Item[] items = new Item[len];

        int count = 0;
        for ( Item i : fullSerialIndex.values() ) {
            items[count++] = i;
        }

        return items;
    }

    @Override
    public boolean isStorageLoaded() {
        return storageLoaded;
    }

    @Override
    public void loadStorage() {
        File f = new File( getStorageFilePath() );
        Object serializedObj = SerializeUtil.deserializeFromFile( f );
        if ( serializedObj instanceof Map ) {
            Map<String, Storable> loadedMap = (Map<String, Storable>) serializedObj;
            if ( null == fullSerialIndex ) {
                fullSerialIndex = loadedMap;
            } else {
                fullSerialIndex.putAll( loadedMap );
            }
            buildRemarkNameIndex( fullSerialIndex );
        } else {
            if ( f.exists() ) {
                f.delete();
            }
            remarkNameIndex = new HashMap<String, Set<Item>>();
            fullSerialIndex = new HashMap<String, Storable>();
        }
        storageLoaded = true;
    }

    @Override
    public void saveToLocalStorage() {
        if ( null == fullSerialIndex ) {
            return;
        }
        String path = getStorageFilePath();
        SerializeUtil.serializeToFile( fullSerialIndex, path );
    }

    private void buildRemarkNameIndex(Map<String, Storable> serialIndex) {
        if ( null == serialIndex ) {
            return;
        }
        if ( null == remarkNameIndex ) {
            remarkNameIndex = new HashMap<String, Set<Item>>();
        }
        for ( Item i : serialIndex.values() ) {
            if ( null == i ) {
                continue;
            }
            String remark = i.getRemarkName();
            Set<Item> itemSet = remarkNameIndex.get( remark );
            if ( null == itemSet ) {
                itemSet = new HashSet<Item>();
                remarkNameIndex.put( remark, itemSet );
            }
            itemSet.add( i );
        }
    }

    private String getStorageFilePath() {
        StringBuilder sb = new StringBuilder();
        sb.append( getStorageDir() )
          .append( File.separator )
          .append( STORAGE_FILE );

        return sb.toString();
    }

    private String getStorageDir() {
        StringBuilder sb = new StringBuilder();
        sb.append( pathConfig.getConfDir() )
                .append( STORAGE_DIR );
        String dirPath = sb.toString();
        File dir = new File(dirPath);
        if ( !dir.exists() ) {
            dir.mkdirs();
        }
        return  dirPath;
    }

}
