package com.pushtechnology.diffusion.demos;

import com.pushtechnology.diffusion.client.session.Session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

class RaceBuilder {
    public static RaceBuilder create() {
        final Randomiser randomiser;
        try {
            randomiser = new Randomiser();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return new RaceBuilder(randomiser);
    }

    private final Randomiser randomiser;

    private long updateFrequency = 0;
    private int teamCount = 0;
    private int carCount = 0;
    private String trackFilename = null;
    private Session session = null;
    private String topic = null;
    private String retainedRange = null;

    private RaceBuilder(Randomiser randomiser) {
        this.randomiser = randomiser;
    }

    RaceBuilder setRaceTrack(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("No track file specified.");
        }
        trackFilename = filename;
        return this;
    }

    RaceBuilder setTeamCount(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Need at least 1 team.");
        }
        if (count > randomiser.getTeamNameCount()) {
            throw new IllegalArgumentException("Not enough team names provided.");
        }
        teamCount = count;
        return this;
    }

    RaceBuilder setCarCount(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Need at least 1 car per team.");
        }
        carCount = count;
        return this;
    }

    RaceBuilder setUpdateFrequency(long frequency) {
        if (frequency < 1) {
            throw new IllegalArgumentException("Minimum update frequency is 1ms.");
        }
        updateFrequency = frequency;
        return this;
    }

    RaceBuilder setDiffusionSession(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session can't be null");
        }
        this.session = session;
        return this;
    }

    RaceBuilder setTopic(String topic) {
        if (topic == null) {
            throw new IllegalArgumentException("Topic can't be null");
        }
        this.topic = topic;
        return this;
    }

    RaceBuilder setRetainedRange(String retainedRange) {
        if (retainedRange == null) {
            throw new IllegalArgumentException("Retained Range can't be null.");
        }
        this.retainedRange = retainedRange;
        return this;
    }

    public Race Build() {
        if (updateFrequency <= 0
                || teamCount <= 0
                || carCount <= 0
                || trackFilename == null
                || session == null
                || topic == null
                || retainedRange == null) {
            return null;
        }

        ArrayList<Team> teams = new ArrayList<>(teamCount);
        for (int iTeam = 0; iTeam < teamCount; iTeam += 1) {

            ArrayList<Car> cars = new ArrayList<>(carCount);
            for (int iCar = 0; iCar < carCount; iCar += 1) {
                cars.add(new Car(iCar, iTeam, randomiser.getNextDriverName()));
            }

            teams.add(new Team(iTeam, randomiser.getNextTeamName(), cars));
        }

        RaceTrack track = null;
        try {
            track = new RaceTrack(trackFilename);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return new Race(updateFrequency, session, track, topic, retainedRange, teams);
        } catch (InterruptedException
                | ExecutionException
                | TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class Randomiser {
        private void readNames(String filename, ArrayList<String> list) throws IOException {
            ClassLoader classLoader = getClass().getClassLoader();

            try (BufferedReader br = new BufferedReader(new FileReader(classLoader.getResource(filename).getFile()))) {
                String line = br.readLine();

                while (line != null) {
                    list.add(line);

                    line = br.readLine();
                }
            }
        }

        private final ArrayList<String> firstNames = new ArrayList<>();
        private final ArrayList<String> lastNames = new ArrayList<>();
        private final ArrayList<String> teamNames = new ArrayList<>();
        private final Random random = new Random(Instant.now().toEpochMilli());

        private Randomiser() throws IOException {
            readNames("names/first.names", firstNames);
            readNames("names/last.names", lastNames);
            readNames("names/team.names", teamNames);
        }

        public String getNextDriverName() {
            return firstNames.get(random.nextInt(firstNames.size())) +
                    ' ' +
                    lastNames.get(random.nextInt(lastNames.size()));
        }

        public String getNextTeamName() {
            if (teamNames.size() == 0) {
                return null;
            }

            // Make sure team names get only used once
            int index = random.nextInt(teamNames.size());
            String name = teamNames.get(index);
            teamNames.remove(index);
            return name;
        }

        int getTeamNameCount() {
            return teamNames.size();
        }
    }
}
