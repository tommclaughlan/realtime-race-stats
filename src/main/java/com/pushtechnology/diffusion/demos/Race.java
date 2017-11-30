package com.pushtechnology.diffusion.demos;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.features.TimeSeries;
import com.pushtechnology.diffusion.client.features.control.topics.TopicControl;
import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;
import com.pushtechnology.diffusion.client.topics.details.TopicType;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.datatype.json.JSONDataType;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.pushtechnology.diffusion.datatype.DataTypes.JSON_DATATYPE_NAME;

public class Race {
    private static final Random RANDOM = new Random(Instant.now().toEpochMilli());
    private static final JSONDataType JSON_DATA_TYPE = Diffusion.dataTypes().json();

    private static double getRandomFromFrange(DoubleRange range) {
        return RANDOM.doubles(1, range.getMin(), range.getMax()).toArray()[0];
    }

    private final ArrayList<Team> teams;
    private final ArrayList<Car> cars;
    private final ArrayList<Car> sorted;
    private final RaceTrack raceTrack;
    private final long updateFrequency;
    private final Session session;
    private final String topic;
    private final TimeSeries timeSeries;
    private final DoubleRange reactionRange;

    private boolean isStartOfRace = true;

    public Race(
            long updateFrequency,
            Session session,
            DoubleRange reactionRange,
            RaceTrack racetrack,
            String topic,
            String retainedRange,
            ArrayList<Team> teams) throws InterruptedException, ExecutionException, TimeoutException {

        this.reactionRange = reactionRange;
        this.updateFrequency = updateFrequency;
        this.raceTrack = racetrack;
        this.session = session;
        this.teams = teams;
        this.topic = topic;

        cars = new ArrayList<>();
        sorted = new ArrayList<>();
        for(Team team : teams ) {
            cars.addAll(team.getCars());
            sorted.addAll(team.getCars());
        }

        timeSeries = session.feature(TimeSeries.class);
        createTopics(retainedRange);
    }

    void start() {
        final long nanoFrequency = updateFrequency * 1000000;

        long current = System.nanoTime();
        long previous = current;
        long tick = 0;
        long elapsed;

        while (true) {
            previous = current;
            current = System.nanoTime();
            elapsed = current - previous;
            tick += elapsed;

            update(elapsed);
            if ( isStartOfRace ) {
                isStartOfRace = false;
            }

            if ( tick >= nanoFrequency ) {
                tick -= nanoFrequency;

                // Update positions
                Collections.sort(sorted);

                int position = sorted.size();
                for (Car car : sorted ) {
                    car.setPosition( position );
                    position -= 1;
                }

                // Send snapshot to Diffusion
                timeSeries.append(topic + "/updates", JSON.class, createJSON());
            }
        }
    }

    private void update(final long elapsed) {
        final double elapsedSeconds = ((double)elapsed / 1000000000.0);

        double carPos;
        double deltaSpeed;
        double speedCap;
        RaceTrack.Part currentPart;
        RaceTrack.Part nextPart;

        for (Car car : cars) {
            currentPart = raceTrack.getPart(car);
            nextPart = raceTrack.getNextPart(currentPart.getId());
            carPos = car.getLocation() * raceTrack.getLength();

            if (nextPart.isCurved() && carPos >= nextPart.getLocation() - 30) {
                car.decelerate(elapsedSeconds);
            } else if ( currentPart.isCurved() && carPos <= nextPart.getLocation() - 30 ) {
                car.decelerate(elapsedSeconds);
            } else {
                car.accelerate(elapsedSeconds);
            }

            car.move(raceTrack.getLength(), elapsedSeconds);



//            currentPart = raceTrack.getPart(car);
//            nextPart = raceTrack.getNextPart(currentPart.getId());
//
//            carPos = car.getLocation() * raceTrack.getLength();
//            if ( !isStartOfRace ) {
//                if (carPos >= nextPart.getLocation() - 10) {
//                    car.setReactionTime(nextPart.getId(), getRandomFromFrange(reactionRange));
//                } else {
//                    car.updateReaction(elapsedSeconds);
//                }
//            } else {
//                car.setReactionTime(nextPart.getId(), getRandomFromFrange(reactionRange));
//            }
//
//            // Is car about to go into a corner?
//            if (nextPart.isCurved() && !isStartOfRace) {
//                if (carPos >= nextPart.getLocation() - 10) {
//                    speedCap = car.getCornering();
//                } else {
//                    speedCap = car.getMaxSpeed();
//                }
//            } else {
//                speedCap = car.getMaxSpeed();
//            }
//
//            if (car.canReact()) {
//                // Is car accelerating or decelerating?
//                if ( speedCap - car.getCurrentSpeed() < 0.0 ) {
//                    deltaSpeed = -car.getDeceleration();
//                } else {
//                    deltaSpeed = car.getAcceleration();
//                }
//                car.accelerate(deltaSpeed, elapsedSeconds);
//            } else {
//                car.updateReaction(elapsedSeconds);
//            }
//
//            car.move(raceTrack.getLength(), elapsedSeconds);
        }
    }

    private JSON createJSON() {
        StringBuilder sb = new StringBuilder(cars.size() * 1024);
        sb.append('[');

        boolean first = true;
        for (Car car : cars) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            car.buildJSON(sb);
        }

        sb.append(']');
        return JSON_DATA_TYPE.fromJsonString(sb.toString());
    }

    private void createTopics(String retainedRange) throws InterruptedException, ExecutionException, TimeoutException {
        final TopicControl topicControl = session.feature(TopicControl.class);
        final TopicUpdateControl topicUpdateControl = session.feature(TopicUpdateControl.class);
        final TopicUpdateControl.Updater.UpdateCallback callback = new TopicUpdateControl.Updater.UpdateCallback.Default();
        final TopicUpdateControl.ValueUpdater<Long> longUpdater = topicUpdateControl.updater().valueUpdater(Long.class);
        final TopicUpdateControl.ValueUpdater<String> stringUpdater = topicUpdateControl.updater().valueUpdater(String.class);

        // Add track filename
        // Note: we remove the html prefix to not confuse the webserver
        final String trackFile;
        if ( raceTrack.getFileName().startsWith( "html/" ) ) {
            trackFile = raceTrack.getFileName().substring(5);
        } else {
            trackFile = raceTrack.getFileName();
        }

        topicControl.addTopic(topic, TopicType.STRING)
                .thenAccept(result -> stringUpdater.update(topic, trackFile, callback))
                .get(5, TimeUnit.SECONDS);

        // Add team count to teams topic
        final String teamsTopic = topic + "/teams";
        topicControl.addTopic(teamsTopic, TopicType.INT64)
                .thenAccept(result -> longUpdater.update(teamsTopic, (long)teams.size(), callback))
                .get(5, TimeUnit.SECONDS);

        for (Team team : teams) {
            // Add team name topic
            final String teamTopic = topic + "/teams/" + team.getID();
            topicControl.addTopic(teamTopic, TopicType.STRING)
                    .thenAccept(result -> stringUpdater.update(teamTopic, team.getName(), callback))
                    .get(5, TimeUnit.SECONDS);

            // Add car count to cars topic
            final String carsTopic = topic + "/teams/" + team.getID() + "/cars";
            topicControl.addTopic(carsTopic, TopicType.INT64)
                    .thenAccept(result -> longUpdater.update(carsTopic, (long) team.getCarCount(), callback))
                    .get(5, TimeUnit.SECONDS);

            for (Car car : team.getCars()) {
                // Add car name topic
                final String carTopic = topic + "/teams/" + team.getID() + "/cars/" + car.getId();
                topicControl.addTopic(carTopic, TopicType.STRING)
                        .thenAccept(result -> stringUpdater.update(carTopic, car.getDriverName(), callback))
                        .get(5, TimeUnit.SECONDS);
            }
        }

        // Add time series topic for high-frequency car updates
        final TopicSpecification specification = topicControl.newSpecification(TopicType.TIME_SERIES)
                .withProperty(TopicSpecification.TIME_SERIES_EVENT_VALUE_TYPE, JSON_DATATYPE_NAME)
                .withProperty(TopicSpecification.TIME_SERIES_RETAINED_RANGE, retainedRange);

        final String timeSeriesTopicName = topic + "/updates";
        topicControl.addTopic(timeSeriesTopicName, specification)
                .thenAccept(result -> timeSeries.append(timeSeriesTopicName, JSON.class, createJSON()))
                .get(5, TimeUnit.SECONDS);
    }
}
