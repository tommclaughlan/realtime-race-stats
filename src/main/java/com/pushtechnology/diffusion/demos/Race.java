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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.pushtechnology.diffusion.datatype.DataTypes.JSON_DATATYPE_NAME;

public class Race {
    private static final JSONDataType JSON_DATA_TYPE = Diffusion.dataTypes().json();
    private final ArrayList<Team> teams;
    private final ArrayList<Car> cars;
    private final RaceTrack raceTrack;
    private final long updateFrequency;
    private final Session session;
    private final String topic;
    private final TopicUpdateControl topicUpdateControl;
    private final TimeSeries timeSeries;

    public Race(long updateFrequency, Session session, RaceTrack racetrack, String topic, ArrayList<Team> teams) {
        this.updateFrequency = updateFrequency;
        this.raceTrack = racetrack;
        this.session = session;
        this.teams = teams;
        this.topic = topic;

        cars = new ArrayList<>();
        for(Team team : teams ) {
            cars.addAll(team.getCars());
        }

        topicUpdateControl = session.feature(TopicUpdateControl.class);
        timeSeries = session.feature(TimeSeries.class);
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

    public void start() {
        if ( !createTopics() ) {
            System.out.println("Failed to create topics");
            System.exit(42);
            return;
        }

        final Random random = new Random(Instant.now().toEpochMilli());
        final long nanoFrequency = updateFrequency * 1000000;

        long current = System.nanoTime();
        long previous = current;
        long tick = 0;

        double min = 0.001;
        double max = 0.01;

        while (true) {
            previous = current;
            current = System.nanoTime();

            tick += current - previous;

            if ( tick >= nanoFrequency ) {
                tick -= nanoFrequency;

                for (Car car : cars) {
                    car.move(min + (max - min) * random.nextDouble());
                }

                timeSeries.append(topic + "/updates", JSON.class, createJSON());
            }
        }
    }

    private boolean createTopics() {
        final TopicControl topicControl = session.feature(TopicControl.class);
        final TopicUpdateControl.Updater.UpdateCallback callback = new TopicUpdateControl.Updater.UpdateCallback.Default();
        final TopicUpdateControl.ValueUpdater<Long> longUpdater = topicUpdateControl.updater().valueUpdater(Long.class);
        final TopicUpdateControl.ValueUpdater<String> stringUpdater = topicUpdateControl.updater().valueUpdater(String.class);

        try {
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
                    .withProperty(TopicSpecification.TIME_SERIES_EVENT_VALUE_TYPE, JSON_DATATYPE_NAME);

            final String timeSeriesTopicName = topic + "/updates";
            topicControl.addTopic(timeSeriesTopicName, specification)
                    .thenAccept(result -> timeSeries.append(timeSeriesTopicName, JSON.class, createJSON()))
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









    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public int getTeamCount() {
        return teams.size();
    }

    public RaceTrack getTrack() {
        return raceTrack;
    }


}
