package com.pushtechnology.diffusion.demos;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class RaceProperties {
    static RaceProperties load() {
        Properties properties = new Properties();
        InputStream inputStream = null;

        try {
            String filename = "config/startup.properties";
            inputStream = Main.class.getClassLoader().getResourceAsStream(filename);
            if (inputStream == null) {
                System.out.println("Unable to find " + filename);
                return null;
            }
            properties.load(inputStream);

            // Read race track file
            String track = properties.getProperty("track");
            if (track == null) {
                System.out.println("No race track defined!");
                return null;
            } else {
                System.out.println("Race track: " + track);
            }

            // Read number of teams
            String teams = properties.getProperty("teams");
            if (teams == null) {
                System.out.println("No teams defined!");
                return null;
            } else {
                System.out.println("Teams: " + teams);
            }

            // Read number of cars per team
            String cars = properties.getProperty("cars");
            if (cars == null) {
                System.out.println("No cars defined!");
                return null;
            } else {
                System.out.println("Cars: " + cars);
            }

            // Read update frequency in milliseconds
            String freq = properties.getProperty("updatefreq");
            if (freq == null) {
                System.out.println("No update frequency defined!");
                return null;
            } else {
                System.out.println("Update frequency: " + freq + "ms");
            }

            // Read topic
            String topic = properties.getProperty("topic");
            if (topic == null) {
                System.out.println("No topic defined!");
                return null;
            } else {
                System.out.println("Topic: " + topic);
            }

            // Read retained range for time series topics
            String retained = properties.getProperty("retainedrange");
            if (retained == null) {
                System.out.println("No retained range for time series defined!");
                return null;
            } else {
                System.out.println("Time Series Retained DoubleRange: " + retained);
            }

            // Read speed range
            String minspeed = properties.getProperty("minspeed");
            String maxspeed = properties.getProperty("maxspeed");
            if (minspeed == null || maxspeed == null) {
                System.out.println("Speed range not specified!");
                return null;
            } else {
                System.out.println("Speed range: " + minspeed + "km/h to " + maxspeed + "km/h");
            }

            // Read speed range
            String mincornering = properties.getProperty("mincornering");
            String maxcornering = properties.getProperty("maxcornering");
            if (mincornering == null || maxcornering == null) {
                System.out.println("Cornering range not specified!");
                return null;
            } else {
                System.out.println("Cornering range: " + mincornering + "km/h to " + maxcornering + "km/h");
            }

            // Read acceleration range
            String minacceleration = properties.getProperty("minacceleration");
            String maxacceleration = properties.getProperty("maxacceleration");
            if (minacceleration == null || maxacceleration == null) {
                System.out.println("Acceleration range not specified!");
                return null;
            } else {
                System.out.println("Acceleration range: " + minacceleration + "m/s^2 to " + maxacceleration + "m/s^2");
            }

            // Read deceleration range
            String mindeceleration = properties.getProperty("mindeceleration");
            String maxdeceleration = properties.getProperty("maxdeceleration");
            if (mindeceleration == null || maxdeceleration == null) {
                System.out.println("Deceleration range not specified!");
                return null;
            } else {
                System.out.println("Deceleration range: " + mindeceleration + "m/s^2 to " + maxdeceleration + "m/s^2");
            }

            return new RaceProperties(
                    track,
                    topic,
                    Integer.parseUnsignedInt(teams),
                    Integer.parseUnsignedInt(cars),
                    Long.parseUnsignedLong(freq),
                    retained,
                    new DoubleRange(Double.parseDouble(minspeed),Double.parseDouble(maxspeed)),
                    new DoubleRange(Double.parseDouble(mincornering), Double.parseDouble(maxcornering)),
                    new DoubleRange(Double.parseDouble(minacceleration),Double.parseDouble(maxacceleration)),
                    new DoubleRange(Double.parseDouble(mindeceleration),Double.parseDouble(maxdeceleration)));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private final String topic;
    private final String track;
    private final int teamCount;
    private final int carCount;
    private final long updateFrequency;
    private final String retainedRange;
    private final DoubleRange speed;
    private final DoubleRange cornering;
    private final DoubleRange acceleration;
    private final DoubleRange deceleration;

    private RaceProperties(
            String track,
            String topic,
            int teamCount,
            int carCount,
            long updateFrequency,
            String retainedRange,
            DoubleRange speed,
            DoubleRange cornering,
            DoubleRange acceleration,
            DoubleRange deceleration) {

        this.topic = topic;
        this.track = track;
        this.teamCount = teamCount;
        this.carCount = carCount;
        this.updateFrequency = updateFrequency;
        this.retainedRange = retainedRange;
        this.speed = speed;
        this.cornering = cornering;
        this.acceleration = acceleration;
        this.deceleration = deceleration;
    }

    String getTopic() {
        return topic;
    }

    String getTrack() {
        return track;
    }

    int getTeamCount() {
        return teamCount;
    }

    int getCarCount() {
        return carCount;
    }

    long getUpdateFrequency() {
        return updateFrequency;
    }

    String getRetainedRange() { return retainedRange; }

    DoubleRange getSpeed() { return speed; }

    DoubleRange getCornering() { return cornering; }

    DoubleRange getAcceleration() { return acceleration; }

    DoubleRange getDeceleration() { return deceleration; }
}

