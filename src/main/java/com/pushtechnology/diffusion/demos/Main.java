package com.pushtechnology.diffusion.demos;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;

import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.init;
import static spark.Spark.port;

/**
 * Main class to handle initialisation and stuff.
 * Here we also start the web server to serve our JS bits.
 *
 */
public class Main
{
    public static void main( String[] args ) {
        // Load properties
        RaceProperties properties = loadProperties();
        if ( properties == null ) {
            System.out.println( "ERROR: No properties loaded!" );
            System.exit( 42 );
        }

        // TODO: Create Teams and Cars
        // TODO: Connect to Diffusion
        // TODO: Populate topics
        // TODO: Init simulation

        // Start web server
        startWebServer();

        // TODO: Start Simulation
    }

    private static void startWebServer() {
        port(3142);
        externalStaticFileLocation("src/main/resources/html");
        init();
    }

    private static class Car {
        public Car() {
        }
    }

    private static class Team {
        private final HashSet<Car> cars = new HashSet<>();

        public Team() {
        }
    }

    private static class Race {
        private final HashSet<Team> teams = new HashSet<>();

        public Race() {
        }
    }

    private static class RaceProperties {
        private final String track;
        private final int teamCount;
        private final int carCount;

        public RaceProperties( String track, int teamCount, int carCount ) {
            this.track = track;
            this.teamCount = teamCount;
            this.carCount = carCount;
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
    }

    private static RaceProperties loadProperties() {
        Properties properties = new Properties();
        InputStream inputStream = null;

        try {
            String filename = "config/startup.properties";
            inputStream = Main.class.getClassLoader().getResourceAsStream( filename );
            if ( inputStream == null ) {
                System.out.println( "Unable to find " + filename );
                return null;
            }
            properties.load( inputStream );

            // Read race track file
            String track = properties.getProperty( "track" );
            if ( track == null ) {
                System.out.println( "No race track defined!" );
                return null;
            } else {
                System.out.println( "Race track: " + track );
            }

            // Read number of teams
            String teams = properties.getProperty( "teams" );
            if ( teams == null ) {
                System.out.println( "No teams defined!" );
                return null;
            } else {
                System.out.println( "Teams: " + teams );
            }

            // Read number of cars per team
            String cars = properties.getProperty( "cars" );
            if ( cars == null ) {
                System.out.println( "No cars defined!" );
                return null;
            } else {
                System.out.println( "Cars: " + cars );
            }

            return new RaceProperties( track, Integer.parseUnsignedInt( teams ), Integer.parseUnsignedInt( cars ) );
        } catch ( IOException ex ) {
            ex.printStackTrace();
            return null;

        } finally {
            if ( inputStream != null ) {
                try {
                    inputStream.close();
                } catch ( IOException ex ) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
