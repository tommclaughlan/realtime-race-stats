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
        RaceProperties properties = RaceProperties.load();
        if (properties == null) {
            System.out.println("ERROR: No properties loaded!");
            System.exit(42);
            return;
        }

        // Start web server
        startWebServer();

        // Connect to Diffusion
        Session session = Diffusion.sessions().principal("control")
                .credentials(Diffusion.credentials().password("password"))
                .open("ws://localhost:8080");

        // Load the race
        Race race = RaceBuilder.create()
                .setDiffusionSession(session)
                .setTopic(properties.getTopic())
                .setRetainedRange(properties.getRetainedRange())
                .setRaceTrack(properties.getTrack())
                .setTeamCount(properties.getTeamCount())
                .setCarCount(properties.getCarCount())
                .setUpdateFrequency(properties.getUpdateFrequency())
                .setSpeedRange(properties.getSpeed())
                .setCorneringRange(properties.getCornering())
                .setAccelerationRange(properties.getAcceleration())
                .setDecelerationRange(properties.getDeceleration())
                .Build();
        if (race == null) {
            System.out.println("ERROR: Failed to create race!");
            System.exit(42);
            return;
        }

        // Start the race
        race.start();
    }

    private static void startWebServer() {
        port(3142);
        externalStaticFileLocation("src/main/resources/html");
        init();
    }
}
