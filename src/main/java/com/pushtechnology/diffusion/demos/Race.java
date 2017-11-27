package com.pushtechnology.diffusion.demos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Race {
    private final ArrayList<Team> teams;
    private final String trackFilename;

    public Race(String trackFileName, ArrayList<Team> teams) {
        this.trackFilename = trackFileName;
        this.teams = teams;
    }

    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public int getTeamCount() {
        return teams.size();
    }

    public String getTrackFilename() {
        return trackFilename;
    }

    public static class Team {
        private final ArrayList<Car> cars;
        private final String name;

        public Team(String name, ArrayList<Car> cars) {
            this.name = name;
            this.cars = cars;
        }

        public List<Car> getCars() {
            return Collections.unmodifiableList(cars);
        }

        public int getCarCount() {
            return cars.size();
        }

        public String getName() {
            return name;
        }

        public static class Car {
            private final String driver;

            public Car(String driver) {
                this.driver = driver;
            }

            public String getDriver() {
                return driver;
            }
        }
    }
}
