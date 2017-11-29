package com.pushtechnology.diffusion.demos;

public class Car implements Comparable<Car> {
    private final int id;
    private final int teamId;
    private final String driverName;
    private final double maxSpeed;
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
            double acceleration,
            double deceleration ) {

        this.id = id;
        this.teamId = teamId;
        this.driverName = driverName;
        this.maxSpeed = maxSpeed / 3.6; // hm/h to m/s
        this.acceleration = acceleration;
        this.deceleration = deceleration;
    }

    String getDriverName() {
        return driverName;
    }

    int getId() {
        return id;
    }

    void setPosition(int position) {
        this.position = position;
    }

    void move(RaceTrack track, long elapsed) {
        double elapsedSeconds = ((double)elapsed / 1000000000.0);

        // Accelerate
        currentSpeed += acceleration * elapsedSeconds;
        if ( currentSpeed > maxSpeed ) {
            // Cap to max speed
            currentSpeed = maxSpeed;
        }

        // Move ahead
        location += ( currentSpeed * elapsedSeconds ) / track.getLength();
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
        if (lap > o.lap) {
            return 1;
        }
        if (lap < o.lap) {
            return -1;
        }
        // laps are equal so compare location
        if (location > o.location) {
            return 1;
        }
        if (location < o.location) {
            return -1;
        }
        return 0; // Both are at the same position
    }
}
