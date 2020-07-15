package com.company.models;

public class Service {
    private long id;
    private String name;
    private double value;
    private double price;
    private int durationDays;

    public Service() {
    }

    public Service(String name, double value, double price, int durationDays) {
        this(0, name, value, price, durationDays);
    }

    public Service(long id, String name, double value, double price, int durationDays) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.price = price;
        this.durationDays = durationDays;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }
}
