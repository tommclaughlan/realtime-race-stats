package com.pushtechnology.diffusion.demos;

public class Car {
    private final int id;
    private final String driverName;

    private int lap = 1;
    private double position = 0.0;

    public Car(int id, String driverName) {
        this.id = id;
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }

    public int getID() {
        return id;
    }

    public double getPosition() {
        return position;
    }

    public int getLap() {
        return lap;
    }

    public void move( double amount ) {
        position += amount;
        if ( position >= 1.0 ) {
            position -= 1.0;
            lap += 1;
        }
    }
}
