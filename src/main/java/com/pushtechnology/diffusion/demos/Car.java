package com.pushtechnology.diffusion.demos;

public class Car implements Comparable<Car> {
    private final int id;
    private final int teamId;
    private final String driverName;
    private final double maxSpeed;
    private final double cornering;
    private final double acceleration;
    private final double deceleration;

    private int lap = 1;
    private int position = 0;
    private double location = 0.0;
    private double currentSpeed = 0.0;

    public Car(
            int id,
            int teamId,
            String driverName,
            double maxSpeed,
            double cornering,
            double acceleration,
            double deceleration ) {

        this.id = id;
        this.teamId = teamId;
        this.driverName = driverName;
        this.maxSpeed = maxSpeed / 3.6; // km/h to m/s
        this.cornering = cornering / 3.6; // km/h to m/s
        this.acceleration = acceleration;
        this.deceleration = deceleration;
    }

    String getDriverName() {
        return driverName;
    }

    int getId() {
        return id;
    }

    double getLocation() { return location; }

    double getCornering() { return cornering; }

    double getMaxSpeed() { return maxSpeed; }

    double getCurrentSpeed() { return currentSpeed; }

    double getAcceleration() { return acceleration; }

    double getDeceleration() { return deceleration; }

    void setPosition(int position) {
        this.position = position;
    }

    void accelerate(double deltaSpeed, double elapsedSeconds) {
        currentSpeed += deltaSpeed * elapsedSeconds;
        if (currentSpeed > maxSpeed) {
            // Cap to max speed
            currentSpeed = maxSpeed;
        }
    }

    void move(double trackLength, double elapsedSeconds) {
        location += ( currentSpeed * elapsedSeconds ) / trackLength;
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
                .append(",\"pos\":")
                .append(position)
                .append(",\"speed\":")
                .append((int)(currentSpeed * 3.6)) //m/s to km/h
                .append('}');
    }

    @Override
    public int compareTo(Car o) {
        int result = Integer.compare(lap, o.lap);
        if ( result == 0 ) {
            return Double.compare(location, o.location);
        }
        return result;
    }
}
