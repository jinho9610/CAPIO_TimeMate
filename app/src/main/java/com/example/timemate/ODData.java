package com.example.timemate;

public class ODData {
    private int iconType;
    private long time;

    public ODData(int iconType, long time) {
        this.iconType = iconType;
        this.time = time;
    }

    public int getIconType() {
        return iconType;
    }

    public void setIconType(int iconType) {
        this.iconType = iconType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
