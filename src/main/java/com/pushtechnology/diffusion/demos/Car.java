package com.pushtechnology.diffusion.demos;

import java.util.HashSet;

public class Car implements Comparable<Car> {
    private final int id;
    private final int teamId;
    private final String driverName;
    private final double acceleration;
    private final double deceleration;
    private final HashSet<Double> lapTimes;
    private final DoubleRange speedRange;
    private final DoubleRange corneringRange;

    private double maxSpeed;
    private double cornering;
    private int lap = 1;
    private int position = 0;
    private double location = 0.0;
    private double currentSpeed = 0.0;
    private double previousLapTime = 0.0;
    private double currentLapTime = 0.0;
    private double lapDifference = 0.0;
    private double accelerationTime = -1;
    private double decelerationTime = -2;
    private double elapsedReactionTime = 0.0;


    private double reactionTime = -1;
    private int previousPart = -1;

    public Car(
            int id,
            int teamId,
            String driverName,
            DoubleRange speedRange,
            DoubleRange corneringRange,
            double acceleration,
            double deceleration ) {

        this.id = id;
        this.teamId = teamId;
        this.driverName = driverName;
        this.speedRange = speedRange;
        this.corneringRange = corneringRange;
        this.maxSpeed = speedRange.getRandom() / 3.6; // km/h to m/s
        this.cornering = corneringRange.getRandom() / 3.6; // km/h to m/s
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
        this.previousPart = partId;
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

    void accelerate(double elapsedSeconds, double reactionTime) {
        if ( accelerationTime == -1 && decelerationTime == -2 ) {
            accelerationTime = reactionTime;
            elapsedReactionTime = 0.0;
            maxSpeed = speedRange.getRandom() / 3.6;
            print( "MaxSpeed: " + maxSpeed);
        } else if ( accelerationTime > -1 ){
            elapsedReactionTime += elapsedSeconds;
        }

        if ( accelerationTime == -2 || elapsedReactionTime >= accelerationTime ) {
            currentSpeed += acceleration * elapsedSeconds;
            if (currentSpeed > maxSpeed) {
                // Cap to max speed
                currentSpeed = maxSpeed;
            }
            elapsedReactionTime = accelerationTime;
            accelerationTime = -2;
            decelerationTime = -1;
        }
    }

    void decelerate(double elapsedSeconds, double reactionTime) {
        if ( decelerationTime == -1 && accelerationTime == -2 ) {
            decelerationTime = reactionTime;
            elapsedReactionTime = 0.0;
            cornering = corneringRange.getRandom() / 3.6;
            print( "Cornering: " + cornering);
        } else if ( decelerationTime > -1 ) {
            elapsedReactionTime += elapsedSeconds;
        }

        if ( decelerationTime == -2 || elapsedSeconds >= accelerationTime ) {
            currentSpeed -= deceleration * elapsedSeconds;
            if (currentSpeed < cornering) {
                // Cap to cornering speed
                currentSpeed = cornering;
            }
            elapsedReactionTime = accelerationTime;
            decelerationTime = -2;
            accelerationTime = -1;
        }
    }

    private void print(String val) {
        if ( teamId == 0 && id == 0 ) {
            System.out.println(val);
        }
    }

    void move(double trackLength, double elapsedSeconds) {
        location += ( currentSpeed * elapsedSeconds ) / trackLength;
        currentLapTime += elapsedSeconds;

        if ( location >= 1.0 ) {
            location -= 1.0;
            lap += 1;

            print("Current lap time: " + currentLapTime);
            // Account for overshoot in lap times
            double overhead = ( ( location * trackLength ) / currentSpeed );
            currentLapTime -= overhead;

            print("Current lap time - overhead: " + currentLapTime);
            print("Overhead: " + overhead);
            print("Previous lap time: " + previousLapTime);

            if ( previousLapTime != 0.0 ) {
                lapDifference = currentLapTime - previousLapTime;
                print("difference in lap times: " + lapDifference);
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

    String buildLapTimeJSON() {
        StringBuilder sb = new StringBuilder(lapTimes.size() * 10);
        sb.append("[");
        boolean first = true;
        for (double time : lapTimes) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(time);
        }
        sb.append("]");
        return sb.toString();
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
