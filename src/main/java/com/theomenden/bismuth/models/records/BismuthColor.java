package com.theomenden.bismuth.models.records;

public record BismuthColor(int rgb) {
    public BismuthColor(String hex) {
        this(Integer.parseInt(hex, 16));
    }

    public BismuthColor {
        rgb |= 0xff000000;
    }
}
