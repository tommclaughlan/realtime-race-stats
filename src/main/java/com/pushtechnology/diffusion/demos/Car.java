package com.pushtechnology.diffusion.demos;

public class Car {
    private final int id;
    private final int teamId;
    private final String driverName;

    private int lap = 1;
    private double location = 0.0;

    public Car(int id, int teamId, String driverName) {
        this.id = id;
        this.teamId = teamId;
        this.driverName = driverName;
    }

    String getDriverName() {
        return driverName;
    }

    int getId() {
        return id;
    }

    int getTeamId() {
        return teamId;
    }

    double getLocation() {
        return location;
    }

    int getLap() {
        return lap;
    }

    void move(double amount) {
        location += amount;
        if ( location >= 1.0 ) {
            location -= 1.0;
            lap += 1;
        }
    }

    void buildJSON(StringBuilder sb) {
        sb.append("{\"id\":")
                .append(id)
                .append(",\"team\":")
                .append(teamId)
                .append(",\"lap\":")
                .append(lap)
                .append(",\"loc\":")
                .append(location)
                .append('}');
    }
}
