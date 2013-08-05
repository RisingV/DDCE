package com.bdcom.itester.rpc;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-29
 * Time: 下午4:21
 * To change this template use File | Settings | File Templates.
 */
public interface RpcID {

    public static final int CONNECT_TO_SERVER = 1;

    public static final int DISCONNECT_TO_SERVER = 2;

    public static final int GET_CHASSIS_INFO = 3;

    public static final int GET_CARD_INFO = 4;

    public static final int GET_ETHER_PHYSICAL = 5;

    public static final int CLEAR_STAT_RELIABLY = 6;

    public static final int SET_HEADER = 7;

    public static final int SET_PAYLOAD = 8;

    public static final int SET_DELAY_COUNT = 9;

    public static final int SET_TX_MODE = 10;

    public static final int START_PORT = 11;

    public static final int STOP_PORT = 12;

    public static final int GET_PORT_ALL_STATS = 13;

    public static final int GET_LINK_STATUS = 14;

    public static final int GET_WORK_INFO = 15;

    public static final int SET_USED_STATE = 16;

    public static final int GET_USED_STATE = 17;

    public static final int SET_STREAM_ID = 18;

    public static final int SET_ETHERNET_PHY_4_ATT = 19;

    public static final int SET_FRAM_LENGTH_CHANGE = 20;

    public static final int LOAD_FPGA = 21;

    public static final int RESET_FPGA  = 22;

    public static final int GET_STREAM_SEND_INFO = 23;

    public static final int GET_STREAM_REC_INFO = 24;

    public static final int START_CAPTURE = 25;

    public static final int STOP_CAPTURE = 26;

    public static final int SET_STREAM_LENGTH = 27;

}
