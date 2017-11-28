package com.pushtechnology.diffusion.demos;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.features.TimeSeries;
import com.pushtechnology.diffusion.client.features.control.topics.TopicControl;
import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;
import com.pushtechnology.diffusion.client.topics.details.TopicType;
import com.pushtechnology.diffusion.datatype.json.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl.*;
import static com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl.Updater.*;
import static com.pushtechnology.diffusion.datatype.DataTypes.INT64_DATATYPE_NAME;
import static com.pushtechnology.diffusion.datatype.DataTypes.JSON_DATATYPE_NAME;
import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.init;
import static spark.Spark.port;

/**
 * Main class to handle initialisation and stuff.
 * Here we also start the web server to serve our JS bits.
 *
 */
public class Main {
    public static void main(String[] args) {
        // Load properties
        RaceProperties properties = loadProperties();
        if (properties == null) {
            System.out.println("ERROR: No properties loaded!");
            System.exit(42);
            return;
        }

        // Create the initial race snapshot
        Race race;
        try {
            race = new RaceBuilder()
                    .withTrack(properties.getTrack())
                    .withTeams(properties.getTeamCount())
                    .withTeamCars(properties.getCarCount())
                    .Build();

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(42);
            return;
        }

        // Connect to Diffusion
        Session session = Diffusion.sessions().principal("control")
                .credentials(Diffusion.credentials().password("password"))
                .open("ws://localhost:8080");
        final TopicControl topicControl = session.feature(TopicControl.class);
        final TopicUpdateControl topicUpdateControl = session.feature(TopicUpdateControl.class);
        final TimeSeries timeSeries = session.feature(TimeSeries.class);

        // Create topics
        if (!createTopics(topicControl, topicUpdateControl, timeSeries, race)) {
            System.out.println("ERROR: Failed to create topics.");
            System.exit(42);
            return;
        }

        // Start web server
        startWebServer();

        startSimulation(timeSeries, race);
    }

    private static void startSimulation(final TimeSeries timeSeries, Race race) {
        final Random random = new Random(Instant.now().toEpochMilli());
        long current = System.nanoTime();
        long previous = current;
        long fastTick = 0; // 1ms
        long normalTick = 0; // 20ms
        long slowTick = 0; // 1000ms

        double min = 0.001;
        double max = 0.01;

        while (true) {
            previous = current;
            current = System.nanoTime();

            fastTick += current - previous;
            normalTick += current - previous;
            slowTick += current - previous;

            if ( fastTick >= 1000000 ) {
                fastTick -= 1000000;

                // TODO: Update fast bits
            }

            if ( normalTick >= 20000000 ) {
                normalTick -= 20000000;

                for (Race.Team team : race.getTeams()) {
                    for (Race.Team.Car car : team.getCars()) {
                        car.move(min + (max - min) * random.nextDouble());
                    }
                }

                timeSeries.append("race/updates/fast", JSON.class, race.getFastUpdates());
            }

            if ( slowTick >= 100000000 ) {
                slowTick -= 100000000;

                // TODO: Update slow bits
            }
        }
    }

    private static boolean createTopics(
            final TopicControl topicControl,
            final TopicUpdateControl topicUpdateControl,
            final TimeSeries timeSeries,
            final Race race) {

        final String topicPrefix = "race";
        final UpdateCallback callback = new UpdateCallback.Default();
        final ValueUpdater<Long> longUpdater = topicUpdateControl.updater().valueUpdater(Long.class);
        final ValueUpdater<String> stringUpdater = topicUpdateControl.updater().valueUpdater(String.class);

        try {
            // Add track filename
            topicControl.addTopic(topicPrefix, TopicType.STRING)
                    .thenAccept(result -> stringUpdater.update(topicPrefix, race.getTrackFilename(), callback))
                    .get(5, TimeUnit.SECONDS);

            // Add team count to teams topic
            final String teamsTopic = topicPrefix + "/teams";
            topicControl.addTopic(teamsTopic, TopicType.INT64)
                    .thenAccept(result -> longUpdater.update(teamsTopic, (long) race.getTeamCount(), callback))
                    .get(5, TimeUnit.SECONDS);

            int teamID = 0;
            for (Race.Team team : race.getTeams()) {
                // Add team name topic
                final String teamTopic = topicPrefix + "/teams/" + teamID;
                topicControl.addTopic(teamTopic, TopicType.STRING)
                        .thenAccept(result -> stringUpdater.update(teamTopic, team.getName(), callback))
                        .get(5, TimeUnit.SECONDS);

                // Add car count to cars topic
                final String carsTopic = topicPrefix + "/teams/" + teamID + "/cars";
                topicControl.addTopic(carsTopic, TopicType.INT64)
                        .thenAccept(result -> longUpdater.update(carsTopic, (long) team.getCarCount(), callback))
                        .get(5, TimeUnit.SECONDS);

                int carID = 0;
                for (Race.Team.Car car : team.getCars()) {
                    // Add car name topic
                    final String carTopic = topicPrefix + "/teams/" + teamID + "/cars/" + carID;
                    topicControl.addTopic(carTopic, TopicType.STRING)
                            .thenAccept(result -> stringUpdater.update(carTopic, car.getDriver(), callback))
                            .get(5, TimeUnit.SECONDS);

                    carID += 1;
                }

                teamID += 1;
            }

            // Add time series topic for high-frequency car updates
            final TopicSpecification specification = topicControl.newSpecification(TopicType.TIME_SERIES)
                    .withProperty(TopicSpecification.TIME_SERIES_EVENT_VALUE_TYPE, JSON_DATATYPE_NAME);

            final String timeSeriesTopicName = "race/updates/fast";
            topicControl.addTopic(timeSeriesTopicName, specification)
                    .thenAccept(result -> timeSeries.append(timeSeriesTopicName, JSON.class, race.getFastUpdates()))
                    .get(5, TimeUnit.SECONDS);

            return true;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void startWebServer() {
        port(3142);
        externalStaticFileLocation("src/main/resources/html");
        init();
    }

    private static class RaceProperties {
        private final String track;
        private final int teamCount;
        private final int carCount;

        public RaceProperties(String track, int teamCount, int carCount) {
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

            return new RaceProperties(track, Integer.parseUnsignedInt(teams), Integer.parseUnsignedInt(cars));
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
}
