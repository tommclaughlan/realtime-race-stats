package com.pushtechnology.diffusion.demos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class NameGenerator {
    private static void readNames(String filename, ArrayList<String> list) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
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

    public String getNextFirstName() {
        return firstNames.get(random.nextInt(firstNames.size()));
    }

    public String getNextLastName() {
        return lastNames.get(random.nextInt(lastNames.size()));
    }

    public String getNextTeamName() {
        if ( teamNames.size() == 0 ) {
            return null;
        }

        // Make sure team names get only used once
        int index = random.nextInt(teamNames.size());
        String name = teamNames.get(index);
        teamNames.remove(index);
        return name;
    }

    public int getFirstNameCount() {
        return firstNames.size();
    }

    public int getLastNameCount() {
        return lastNames.size();
    }

    public int getTeamNameCount() {
        return teamNames.size();
    }
}
