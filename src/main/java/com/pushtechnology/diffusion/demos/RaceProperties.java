package com.pushtechnology.diffusion.demos;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RaceProperties {
    public static RaceProperties load() {
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

            return new RaceProperties(
                    track,
                    Integer.parseUnsignedInt(teams),
                    Integer.parseUnsignedInt(cars),
                    Long.parseUnsignedLong(freq));
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

    private final String track;
    private final int teamCount;
    private final int carCount;
    private final long updateFrequency;

    private RaceProperties(
            String track,
            int teamCount,
            int carCount,
            long updateFrequency ) {

        this.track = track;
        this.teamCount = teamCount;
        this.carCount = carCount;
        this.updateFrequency = updateFrequency;
    }

    public String getTrack() {
        return track;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public int getCarCount() {
        return carCount;
    }

    public long getUpdateFrequency() {
        return updateFrequency;
    }
}
