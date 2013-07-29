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

    public static final int GET_CHASSIS_INFO = 2;

    public static final int GET_CARD_INFO = 3;

    public static final int GET_ETHER_PHYSICAL = 4;

    public static final int CLEAR_STAT_RELIABLY = 5;

    public static final int SET_HEADER = 6;

    public static final int SET_PAYLOAD = 7;

    public static final int SET_DELAY_COUNT = 8;

    public static final int SET_TX_MODE = 9;

    public static final int START_PORT = 10;

    public static final int STOP_PORT = 11;

    public static final int GET_PORT_ALL_STATS = 12;

    public static final int GET_LINK_STATUS = 13;

    public static final int GET_WORK_INFO = 14;

    public static final int SET_USED_STATE = 15;

    public static final int GET_USED_STATE = 16;

    public static final int SET_STREAM_ID = 17;

    public static final int SET_ETHERNET_PHY_4_ATT = 18;

    public static final int SET_FRAM_LENGTH_CHANGE = 19;

    public static final int LOAD_FPGA = 20;

    public static final int RESET_FPGA  = 21;

    public static final int GET_STREAM_SEND_INFO = 22;

    public static final int GET_STREAM_REC_INFO = 23;

    public static final int START_CAPTURE = 24;

    public static final int STOP_CAPTURE = 25;

    public static final int SET_STREAM_LENGTH = 26;

}
