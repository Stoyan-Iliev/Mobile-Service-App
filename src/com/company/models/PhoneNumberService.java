package com.company.models;

import java.time.LocalDate;

public class PhoneNumberService {
    private long phoneNumberId;
    private long serviceId;
    private String name;
    private double remainingValue;
    private LocalDate deactivationDate;
    private LocalDate activationDate;
    private boolean isPaid;
    private boolean isActivated;

    public PhoneNumberService() {
    }

    public PhoneNumberService(String name, double remainingValue, LocalDate deactivationDate) {
        this.name = name;
        this.remainingValue = remainingValue;
        this.deactivationDate = deactivationDate;
    }

    public PhoneNumberService(long phoneNumberId, long serviceId, double remainingValue, LocalDate deactivationDate, LocalDate activationDate, boolean isPaid, boolean isActivated) {
        this.phoneNumberId = phoneNumberId;
        this.serviceId = serviceId;
        this.remainingValue = remainingValue;
        this.deactivationDate = deactivationDate;
        this.activationDate = activationDate;
        this.isPaid = isPaid;
        this.isActivated = isActivated;
    }

    public long getPhoneNumberId() {
        return phoneNumberId;
    }

    public long getServiceId() {
        return serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRemainingValue() {
        return remainingValue;
    }

    public void setRemainingValue(double remainingValue) {
        this.remainingValue = remainingValue;
    }

    public LocalDate getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(LocalDate deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public LocalDate getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDate activationDate) {
        this.activationDate = activationDate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }
}
