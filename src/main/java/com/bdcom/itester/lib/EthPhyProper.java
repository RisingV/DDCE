package com.bdcom.itester.lib;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2013-6-24 <br>
 * Auto-Generated by eclipse Kepler <br>
 */

public class EthPhyProper extends ConnectStatus {

	private boolean linked;
	
	private int nego;
	
	private int speed;
	
	private int fullDuplex;
	
	private int loopback;

	public boolean isLinked() {
		return linked;
	}

	public void setLinked(boolean linked) {
		this.linked = linked;
	}

	public int getNego() {
		return nego;
	}

	public void setNego(int nego) {
		this.nego = nego;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getFullDuplex() {
		return fullDuplex;
	}

	public void setFullDuplex(int fullDuplex) {
		this.fullDuplex = fullDuplex;
	}

	public int getLoopback() {
		return loopback;
	}

	public void setLoopback(int loopback) {
		this.loopback = loopback;
	}
	
}
