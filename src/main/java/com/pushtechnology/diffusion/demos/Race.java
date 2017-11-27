package com.pushtechnology.diffusion.demos;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Race {
    private final ArrayList<Team> teams;

    public Race( ArrayList<Team> teams ) {
        this.teams = teams;
    }

    public static class Team {
        private final ArrayList<Car> cars;
        private final String name;

        public Team(String name, ArrayList<Car> cars ) {
            this.name = name;
            this.cars = cars;
        }

        public static class Car {
            private final String driver;

            public Car( String driver ) {
                this.driver = driver;
            }
        }
    }
}
