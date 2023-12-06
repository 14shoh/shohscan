package com.example.shohscan;

public class ScannedData {
    private double value;
    private String time;
    private String date;

    public ScannedData() {
        // Пустой конструктор требуется для Firebase
    }

    public ScannedData(double value, String time, String date) {
        this.value = value;
        this.time = time;
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
