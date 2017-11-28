package com.pushtechnology.diffusion.demos;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.datatype.json.JSONDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Race {
    private final ArrayList<Team> teams;
    private final JSONDataType jsonDataType = Diffusion.dataTypes().json();
    private final RaceTrack raceTrack;

    public Race(RaceTrack racetrack, ArrayList<Team> teams) {
        this.raceTrack = racetrack;
        this.teams = teams;
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

    public JSON getFastUpdates() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');

        boolean firstTeam = true;
        for (Team team : teams) {
            if ( firstTeam ) {
                firstTeam = false;
            } else {
                sb.append(',');
            }
            sb.append('[');
            boolean firstCar = true;
            for (Car car : team.cars) {
                if (firstCar) {
                    firstCar = false;
                } else {
                    sb.append(',');
                }
                sb.append("{\"pos\":")
                        .append(car.getPosition())
                        .append(",\"lap\":")
                        .append(car.getLap())
                        .append("}");
            }
            sb.append(']');
        }

        sb.append(']');
        return jsonDataType.fromJsonString(sb.toString());
    }

    public static class Team {
        private final ArrayList<Car> cars;
        private final String name;
        private final int id;

        public Team(int id, String name, ArrayList<Car> cars) {
            this.id = id;
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

        public int getID() {
            return id;
        }
    }
}
