package com.bdcom.dce.itester.api.wrapper;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-20    <br/>
 * Time: 17:40  <br/>
 */

public class TestCaseConfig {

    private final String ip;
    private final int srcCardId;
    private final int srcPortId;
    private final int dstCardId;
    private final int dstPortId;
    private final int seconds;
    private final int percent;

    TestCaseConfig(String ip, int srcCardId, int srcPortId,
               int dstCardId, int dstPortId, int seconds, int percent) {
        this.ip = ip;
        this.srcCardId = srcCardId;
        this.srcPortId = srcPortId;
        this.dstCardId = dstCardId;
        this.dstPortId = dstPortId;
        this.seconds = seconds;
        this.percent = percent;
    }

    public String getIp() {
        return ip;
    }

    public int getSrcCardId() {
        return srcCardId;
    }

    public int getSrcPortId() {
        return srcPortId;
    }

    public int getDstCardId() {
        return dstCardId;
    }

    public int getDstPortId() {
        return dstPortId;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getPercent() {
        return percent;
    }

    public static class Builder {
        private String ip;
        private int srcCardId;
        private int srcPortId;
        private int dstCardId;
        private int dstPortId;
        private int seconds;
        private int percent = 100;

        public Builder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder srcCardId(int srcCardId) {
            this.srcCardId = srcCardId;
            return this;
        }

        public Builder srcPortId(int srcPortId) {
            this.srcPortId = srcPortId;
            return this;
        }

        public Builder dstCardId(int dstCardId) {
            this.dstCardId = dstCardId;
            return this;
        }

        public Builder dstPortId(int dstPortId) {
            this.dstPortId = dstPortId;
            return this;
        }

        public Builder seconds(int seconds) {
            this.seconds = seconds;
            return this;
        }

        public Builder percent(int percent) {
            this.percent = percent;
            return this;
        }

        public TestCaseConfig build() {
            return new TestCaseConfig(
                    this.ip,
                    this.srcCardId,
                    this.srcPortId,
                    this.dstCardId,
                    this.dstPortId,
                    this.seconds,
                    this.percent
            );
        }

    }

}
