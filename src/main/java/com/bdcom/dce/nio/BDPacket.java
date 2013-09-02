package com.bdcom.dce.nio;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-4
 * Time: 上午11:45
 */
public class BDPacket {

    private static int currentVersion;

    public static void setCurrentVersion(int ver) {
        currentVersion = ver;
    }

    private final int version;
    private int requestID;
    private int dataType;
    private byte[] data;

    private BDPacket(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void writeStringArray(String[] strArray) throws IOException {
        int num = 0;
        if ( null != strArray ) {
            num = strArray.length;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteArrayOutputStream);

        out.writeInt( num );
        for (int i = 0; i < num; i++) {
            String s = strArray[i];
            int len = s.length();
            byte[] bytes = s.getBytes();
            out.writeInt(len);
            out.write(bytes);
        }

        byte[] totalBytes = byteArrayOutputStream.toByteArray();
        setData( totalBytes );
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteArrayOutputStream);

        out.writeByte( this.version );
        out.writeByte( this.requestID );
        out.writeByte( this.dataType );
        out.writeInt( null == this.data ? 0 : data.length );
        out.write( null == this.data ? new byte[0] : this.data );

        return byteArrayOutputStream.toByteArray();
    }

    public static BDPacket parse(byte[] raw) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(raw));

        int version = (int) in.readByte();
        int requestID = (int) in.readByte();
        int dataType = (int) in.readByte();
        int dataLen = in.readInt();
        byte[] data = new byte[dataLen];
        in.read(data);
        in.close();

        BDPacket packet = new BDPacket( version );
        packet.setRequestID(requestID);
        packet.setDataType(dataType);
        packet.setData(data);

        return packet;
    }

    public static BDPacket newPacket(int requestID) {
        return newPacket( requestID, currentVersion );
    }


    public static BDPacket newPacket(int requestID, int version) {
        BDPacket newPack = new BDPacket( version );
        newPack.setRequestID( requestID );

        return newPack;
    }

    public static BDPacket clone(BDPacket pack) {
        if ( null == pack ) {
            return null;
        }
        BDPacket cloned = newPacket( pack.getRequestID(), pack.getVersion() );
        cloned.setDataType( pack.getDataType() );
        cloned.setData( pack.getData() );

        return cloned;
    }
}
