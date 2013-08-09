package com.bdcom.itester;

import com.bdcom.itester.lib.CaptureResult;
import com.bdcom.itester.lib.CardInfo;
import com.bdcom.itester.lib.ChassisInfo;
import com.bdcom.itester.lib.CommuStatus;
import com.bdcom.itester.lib.EthPhyProper;
import com.bdcom.itester.lib.ITesterLibLoader;
import com.bdcom.itester.lib.LinkStatus;
import com.bdcom.itester.lib.PortStats;
import com.bdcom.itester.lib.StreamInfo;
import com.bdcom.itester.lib.UsedState;
import com.bdcom.itester.lib.WorkInfo;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2013-6-25 <br>
 * Auto-Generated by eclipse Kepler <br>
 */

public class LibTest {

	public static void main(String[] s) {
		testing();
	}
	
	public static void testing() {
		ITesterLibLoader itl = new ITesterLibLoader();
		LibTest test = new LibTest();
		CommuStatus cs = test.connectToServerTest( itl );
		test.connectToServerTest( itl );
		test.connectToServerTest( itl );
		if ( !cs.isConnected() ) {
			test.getChassisInfoTest( itl, cs.getSocketId() );
			test.getCardInfoTest( itl, cs.getSocketId(), 1 );
			test.getEthernetPhysicalTest( itl, cs.getSocketId(), 1, 1 );
			test.clearStatReliablyTest( itl, cs.getSocketId(), 1, 1 );
			test.setHeaderTest( itl, cs.getSocketId(), 1, 1, 2, 2 * 12, 
					"aaaaaaaaaaaabbbbbbbbbbbb".getBytes() );
			test.setPayloadTest( itl, cs.getSocketId(), 1, 1, 0, "FF".getBytes(), 0 );
			test.setDelayCountTest( itl, cs.getSocketId(), 1, 1, 10 );
			test.setTxModeTest(itl, cs.getSocketId(), 1, 1, 1, 10);
			test.startPortTest(itl, cs.getSocketId(), 1, 1);
			test.stopPortTest(itl, cs.getSocketId(), 1, 1);
			test.getPortAllStatsTest(itl, cs.getSocketId(), 1, 1, 8 );
			test.getLinkStatusTest(itl, cs.getSocketId(), 1, 1);
			test.setUsedStateTest(itl, cs.getSocketId(), 1, 1, 1);
			test.getUsedStateTest(itl, cs.getSocketId(), 1, 1);
			test.setStreamIdTest(itl, cs.getSocketId(), 1, 1, 1, 1);
			test.setEthernetPhysicalForATTTest(itl, cs.getSocketId(), 1, 1,
					1, 2, 1, 0);
			test.setFramLengthChangeTest(itl, cs.getSocketId(), 1, 1, 0 );
			test.loadFPGATest(itl, cs.getSocketId(), 1, 1 );
			test.resetFPGATest(itl, cs.getSocketId(), 1 );
			test.getStreamSendInfoTest(itl, cs.getSocketId(), 1, 1, 1);
			test.getStreamRecInfoTest(itl, cs.getSocketId(), 1, 1, 1);
			test.startCaptureTest(itl, cs.getSocketId(), 1, 1 );
			test.stopCaptureTest(itl, cs.getSocketId(), 1, 1 );
			test.setStreamLengthTest(itl, cs.getSocketId(), 1, 1, 1, 10 );
			
			test.setStreamLengthTest(itl, -1, 1, 1, 1, 10 );
		}
	}
	
	public CommuStatus connectToServerTest( ITesterLibLoader itl ) {
		CommuStatus cs = itl.connectToServer("127.0.0.1");
		
		System.out.println( "socketId: " + cs.getSocketId() );
		System.out.println( "connected: " + cs.isConnected() );
		
		return cs;
	}
	
	public ChassisInfo getChassisInfoTest( ITesterLibLoader itl , int socketId) {
		ChassisInfo ci = itl.getChassisInfo( socketId );
		
//		System.out.println( "connected: " + ci.isConnected() );
		System.out.println( "chassis chassisType: " + ci.getChassisType() );
		System.out.println( "chassis cardNum: " + ci.getCardNum() );
		System.out.println( "chassis description: " + ci.getDescription() );
		
		return ci;
	}
	
	public CardInfo getCardInfoTest( ITesterLibLoader itl, int socketId, int cardId ) {
		CardInfo ci = itl.getCardInfo(socketId, cardId);
		
		System.out.println( "card cardId: " + ci.getCardId() );
		System.out.println( "card cardType: " + ci.getCardType() );
		System.out.println( "card portNumber: " + ci.getPortNumber() );
		System.out.println( "card description: " + ci.getDescription() );
		
		return ci;
	}
	
	public EthPhyProper getEthernetPhysicalTest( ITesterLibLoader itl, int socketId, int cardId, int portId) {
		EthPhyProper epp = itl.getEthernetPhysical(socketId, cardId, portId);
		
		System.out.println( "EthPhyProper nego: " + epp.getNego() );
		System.out.println( "EthPhyProper speed: " + epp.getSpeed() );
		System.out.println( "EthPhyProper fullDuplex: " + epp.getFullDuplex() );
		System.out.println( "EthPhyProper loopBack: " + epp.getLoopback() );
		
		return epp;
	}
	
	public int clearStatReliablyTest( ITesterLibLoader itl, int socketId, int cardId, int portId ) {
		int status = itl.clearStatReliably(socketId, cardId, portId);
		
		System.out.println( "clearStatReliably status: " + status );
		
		return status;
	}
	
	public int setHeaderTest( ITesterLibLoader itl, int socketId, int cardId, int portId,
			int validStreamCount, int length, byte[] strHead ) {
		int status = itl.setHeader(socketId, cardId, portId, validStreamCount, length, strHead);
		
		System.out.println( "setHeader status: " + status );
		
		return status;
	}
	
	public int setPayloadTest( ITesterLibLoader itl, int socketId, int cardId, int portId,
			int length, byte[] data, int type) {
		int status = itl.setPayload(socketId, cardId, portId, length, data, type);
		
		System.out.println( "setPayloadTest status: " + status );
		
		return status;
	}
	
	public int setDelayCountTest( ITesterLibLoader itl, int socketId, int cardId, int portId, int delayCount ) {
		int status = itl.setDelayCount(socketId, cardId, portId, delayCount);
		
		System.out.println( "setDelayCount status: " + status );
		
		return status;
	}
	
	public int setTxModeTest( ITesterLibLoader itl, int socketId, int cardId, int portId, int mode, int burstNum ) {
		int status = itl.setTxMode( socketId, cardId, portId, mode, burstNum );
		
		System.out.println( "setTxModel status: " + status );
		
		return status;
	}
	
	public int startPortTest( ITesterLibLoader itl, int socketId, int cardId, int portId ) {
		int status = itl.startPort(socketId, cardId, portId);
		
		System.out.println( "startPort status: " + status );
		
		return status;
	}
	
	public int stopPortTest( ITesterLibLoader itl, int socketId, int cardId, int portId ) {
		int status = itl.stopPort(socketId, cardId, portId);
		
		System.out.println( "stopPort status: " + status );
		
		return status;
	}
	
	public PortStats getPortAllStatsTest( ITesterLibLoader itl, int socketId, int cardId, int portId, int length ) {
		PortStats ps = itl.getPortAllStats(socketId, cardId, portId, length);
		
		long[] stats = ps.getStats();
		
		StringBuilder sb = new StringBuilder();
		sb.append("PortStats: [ ");
		for ( int i = 0; i < stats.length; i++ ) {
			sb.append( stats[i] );
			if ( i != stats.length - 1 ) {
				sb.append(", ");
			}
		}
		sb.append( " ]");
		System.out.println( sb.toString() );
		
		return ps;
	}
	
	public LinkStatus getLinkStatusTest( ITesterLibLoader itl, int socketId, int cardId, int portId ) {
		LinkStatus ls = itl.getLinkStatus( socketId, cardId, portId );
		
		System.out.println( "LinkStatus isLinked: " + ls.isLinked() );
		
		return ls;
	}
	
	
	public WorkInfo getWorkInfoTest( ITesterLibLoader itl, int socketId, int cardId, int portId ) {
		WorkInfo wi = itl.getWorkInfo(socketId, cardId, portId);
		
		System.out.println( "WorkInfo isWorkNow: " + wi.isWorkNow() );
		
		return wi;
	}
	
	public int setUsedStateTest( ITesterLibLoader itl, int socketId, int cardId, int portId, int usedState ) {
		int status = itl.setUsedState( socketId, cardId, portId, usedState );
		
		System.out.println( "setUsedState status: " + status );
		
		return status;
	}
	
	public UsedState getUsedStateTest( ITesterLibLoader itl, int socketId, int cardId, int portId) {
		UsedState us = itl.getUsedState(socketId, cardId, portId);
		
		System.out.println( "UsedState isUsed: " + us.isUsed() );
		
		return us;
	}
	
	public int setStreamIdTest( ITesterLibLoader itl, int socketId, int cardId, int portId, int iStartId, int iIdNum ) {
		int status = itl.setStreamId(socketId, cardId, portId, iStartId, iIdNum);
		
		System.out.println( "setStreamId status: " + status );
		
		return status;
	}
	
	public int setEthernetPhysicalForATTTest( ITesterLibLoader itl, int socketId, int cardId, int portId, 
			int nego, int ethPhySpeed, int fullDuplex, int loopback ) {
		int status = itl.setEthernetPhysicalForATT(socketId, cardId, portId, nego, ethPhySpeed, fullDuplex, loopback);
		
		System.out.println( "setEthernetPhysicalForATT status: " + status );
		
		return status;
	}
	
	public int setFramLengthChangeTest( ITesterLibLoader itl, int socketId, int cardId, int portId, int isChange ) {
		int status = itl.setFramLengthChange(socketId, cardId, portId, isChange);
		
		System.out.println( "setFramLengthChange status: " + status );
		
		return status;
	}
	
	public int loadFPGATest( ITesterLibLoader itl, int socketId, int cardId, int ethPhySpeed ) {
		int status = itl.loadFPGA(socketId, cardId, ethPhySpeed);
				
		System.out.println( "loadFPGA status: " + status );
		
		return status;
	}
	
	public int resetFPGATest( ITesterLibLoader itl, int socketId, int cardId) {
		int status = itl.resetFPGA(socketId, cardId);
		
		System.out.println( "resetFPGA status: " + status );
		
		return status;
	} 
	
	public StreamInfo getStreamSendInfoTest( ITesterLibLoader itl, int socketId, int cardId, int portId, int streamId ) {
		StreamInfo si = itl.getStreamRecInfo(socketId, cardId, portId, streamId);
		
		System.out.println( "SendStreamInfo packetCount: " + si.getPacketCount() );
		
		return si;
	}
	
	public StreamInfo getStreamRecInfoTest( ITesterLibLoader itl, int socketId, int cardId, int portId, int streamId ) {
		StreamInfo si = itl.getStreamRecInfo(socketId, cardId, portId, streamId);
		
		System.out.println( "RecStreamInfo packetCount: " + si.getPacketCount() );
		
		return si;
	}
	
	public int startCaptureTest( ITesterLibLoader itl, int socketId, int cardId, int portId) {
		int status = itl.startCapture(socketId, cardId, portId);
		
		System.out.println( "StartCaptureReq status: " + status );
		
		return status;
	}
	
	public CaptureResult stopCaptureTest( ITesterLibLoader itl, int socketId, int cardId, int portId) {
		CaptureResult cr = itl.stopCapture(socketId, cardId, portId);
		
		System.out.println( "CaptureResult Frames: " + cr.getFrames() );
		
		return cr;
	}
	
	public int setStreamLengthTest( ITesterLibLoader itl, int socketId, int cardId, int portId, int streamId, int length ) {
		int status = itl.setStreamLength(socketId, cardId, portId, streamId, length);
		
		System.out.println( "setStreamLength status: " + status );
		
		return status;
	}
	
}