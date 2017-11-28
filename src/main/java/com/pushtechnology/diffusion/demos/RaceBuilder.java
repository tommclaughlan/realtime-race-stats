package com.pushtechnology.diffusion.demos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public class RaceBuilder {
    private final NameGenerator names;

    private long updateFrequency;
    private int teamCount;
    private int carCount;
    private String trackFilename;

    public RaceBuilder() throws IOException {
        names = new NameGenerator();
    }

    public RaceBuilder withTrack(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("No track file specified.");
        }
        trackFilename = filename;
        return this;
    }

    public RaceBuilder withTeams(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Need at least 1 team.");
        }
        if (count > names.getTeamNameCount()) {
            throw new IllegalArgumentException("Not enough team names provided.");
        }
        teamCount = count;
        return this;
    }

    public RaceBuilder withTeamCars(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Need at least 1 car per team.");
        }
        carCount = count;
        return this;
    }

    public Race Build() throws IOException {
        if (teamCount <= 0 || carCount <= 0 || trackFilename == null) {
            // TODO: Throw exception in here...
        }

        ArrayList<Team> teams = new ArrayList<>(teamCount);
        for (int iTeam = 0; iTeam < teamCount; iTeam += 1) {

            ArrayList<Car> cars = new ArrayList<>(carCount);
            for (int iCar = 0; iCar < carCount; iCar += 1) {
                cars.add(new Car(iCar, names.getNextDriverName()));
            }

            teams.add(new Team(iTeam, names.getNextTeamName(), cars));
        }

        RaceTrack track = new RaceTrack(trackFilename);
        return new Race(track, teams);
    }

    private class NameGenerator {
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

        private NameGenerator() throws IOException {
            readNames("names/first.names", firstNames);
            readNames("names/last.names", lastNames);
            readNames("names/team.names", teamNames);
        }

        public String getNextDriverName() {
            StringBuilder sb = new StringBuilder();
            sb.append(firstNames.get(random.nextInt(firstNames.size())));
            sb.append(' ');
            sb.append(lastNames.get(random.nextInt(lastNames.size())));
            return sb.toString();
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

        public int getTeamNameCount() {
            return teamNames.size();
        }
    }
}
