package com.pushtechnology.diffusion.demos;

import java.util.HashSet;

public class Car implements Comparable<Car> {
    private final int id;
    private final int teamId;
    private final String driverName;
    private final double maxSpeed;
    private final double cornering;
    private final double acceleration;
    private final double deceleration;
    private final HashSet<Double> lapTimes;

    private int lap = 1;
    private int position = 0;
    private double location = 0.0;
    private double currentSpeed = 0.0;
    private double previousLapTime = 0.0;
    private double currentLapTime = 0.0;
    private double lapDifference = 0.0;
    private double reactionTime = 0.0;
    private double elapsedReactionTime = 0.0;
    private int partId = -1;

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

        this.lapTimes = new HashSet<>();
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

    void setReactionTime(int partId, double reactionTime) {
        if ( this.reactionTime > 0.0 || partId == this.partId ) {
            return;
        }
        if ( teamId == 0 && id == 0 ) {
            System.out.println("Set reaction time: " + reactionTime);
        }
        this.partId = partId;
        this.elapsedReactionTime = 0.0;
        this.reactionTime = reactionTime;
    }

    boolean canReact() {
        return reactionTime == 0.0 || elapsedReactionTime >= reactionTime;
    }

    void updateReaction(double elapsedSeconds) {
        if ( reactionTime > 0.0 ) {
            elapsedReactionTime += elapsedSeconds;
            reactionTime = 0.0;
        }
    }

    void accelerate(double elapsedSeconds) {
        currentSpeed += acceleration * elapsedSeconds;
        if (currentSpeed > maxSpeed) {
            // Cap to max speed
            currentSpeed = maxSpeed;
        }
    }

    void decelerate(double elapsedSeconds) {
        currentSpeed -= deceleration * elapsedSeconds;
        if (currentSpeed < cornering ) {
            // Cap to cornering speed
            currentSpeed = cornering;
        }
    }

    void move(double trackLength, double elapsedSeconds) {
        location += ( currentSpeed * elapsedSeconds ) / trackLength;
        currentLapTime += elapsedSeconds;

        if ( location >= 1.0 ) {
            location -= 1.0;
            lap += 1;

            // Account for overshoot in lap times
            double overhead = ( ( location * trackLength ) / currentSpeed );
            currentLapTime -= overhead;
            if ( previousLapTime != 0.0 ) {
                lapDifference = currentLapTime - previousLapTime;
            }
            previousLapTime = currentLapTime;
            currentLapTime = overhead;

            lapTimes.add(previousLapTime);
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
                .append(",\"t\":")
                .append(currentLapTime)
                .append(",\"pt\":")
                .append(previousLapTime)
                .append(",\"td\":")
                .append(lapDifference)
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
