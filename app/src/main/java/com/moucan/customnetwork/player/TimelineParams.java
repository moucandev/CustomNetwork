package com.moucan.customnetwork.player;

public class TimelineParams {
    float scaleCount = 2.5f; //刻度数量
    int scaleWidth = 0;
    int scaleMillis = 10*60 * 1000; //一个刻度表示的时间（秒）
    long totalMillis = (long)(scaleMillis * scaleCount); //时间轴可以显示的时间总时差

    private final float initScaleCount;
    private final int initScaleMillis;
    private final long initTotalMillis;
    private int initScaleWidth = scaleWidth;
    public final float initMaxScale = 2.2f;
    public final float initMinScale = 0.3f;

    private TimelineParams biggerParams; //单位刻度显示时间更多的（scaleMillis）
    private TimelineParams smallerParams; // 单位刻度显示时间更少的（scaleMillis)

    public String name = "";

    public TimelineParams() {
        initScaleCount = scaleCount;
        initScaleMillis = scaleMillis;
        initTotalMillis = totalMillis;
    }

    public TimelineParams(int scaleMillis, float scaleCount) {
        this.scaleMillis = scaleMillis;
        this.scaleCount = scaleCount;
        totalMillis = (int)(scaleMillis * scaleCount);

        initScaleCount = scaleCount;
        initScaleMillis = scaleMillis;
        initTotalMillis = totalMillis;
    }

    public void init(int scaleWidth) {
        initScaleWidth = scaleWidth;
        this.scaleWidth = scaleWidth;
    }

    public void reset() {
        scaleCount = initScaleCount;
        scaleMillis = initScaleMillis;
        totalMillis = initTotalMillis;
        scaleWidth = initScaleWidth;
    }

    public boolean scale(int viewWidth, float scale) {
        int tempWidth = (int)(scaleWidth * scale);
        float s = tempWidth / (float)initScaleWidth;
        if (s<=initMaxScale && s >= initMinScale) {
            scaleWidth = tempWidth;
            totalMillis = (long)(viewWidth/(float)scaleWidth *scaleMillis);
            return true;
        }
        return false;
    }

    public TimelineParams getBiggerParams() {
        if (biggerParams == null) {
            return this;
        }
        reset();
        return biggerParams;
    }

    public TimelineParams getRealBiggerParams() {
        return biggerParams;
    }

    public TimelineParams getSmallerParams() {
        if (smallerParams == null) {
            return this;
        }
        reset();
        return smallerParams;
    }

    public TimelineParams setBiggerParams(TimelineParams biggerParams) {
        this.smallerParams = biggerParams;
        return biggerParams;
    }

    public TimelineParams setSmallerParams(TimelineParams smallerParams) {
        this.biggerParams = smallerParams;
        return smallerParams;
    }
}

