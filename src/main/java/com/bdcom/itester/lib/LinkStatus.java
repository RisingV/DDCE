package com.bdcom.itester.lib;

import java.io.Serializable;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2013-6-24 <br>
 * Auto-Generated by eclipse Kepler <br>
 */

public class LinkStatus extends ConnectStatus implements Serializable {

    private static final long serialVersionUID = 4919590216836601088L;

    private boolean linked;
	
	public boolean isLinked() {
		return linked;
	}

	public void setLinked(boolean linked) {
		this.linked = linked;
	}

}
