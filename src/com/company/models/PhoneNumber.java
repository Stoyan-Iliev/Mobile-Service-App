package com.company.models;

public class PhoneNumber {
    private long id;
    private String number;
    private long clientId;

    public PhoneNumber() {
    }

    public PhoneNumber(String number, long clientId) {
        this(0, number, clientId);
    }

    public PhoneNumber(long id, String number, long clientId) {
        this.id = id;
        this.number = number;
        this.clientId = clientId;
    }

    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }
}
