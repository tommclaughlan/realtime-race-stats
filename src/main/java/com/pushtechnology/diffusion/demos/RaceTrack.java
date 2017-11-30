package com.pushtechnology.diffusion.demos;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class RaceTrack {
    private static Part loadPart( JsonParser parser, double location ) throws IOException {
        if (parser.nextToken() != JsonToken.FIELD_NAME) {
            throw new IllegalArgumentException(); // TODO: throw something else
        }
        if (!Objects.equals(parser.getCurrentName(), "length")) {
            throw new IllegalArgumentException(); // TODO: throw something else
        }

        parser.nextToken();
        double length = parser.getDoubleValue();

        if (parser.nextToken() != JsonToken.FIELD_NAME) {
            throw new IllegalArgumentException(); // TODO: throw something else
        }
        if (!Objects.equals(parser.getCurrentName(), "type")) {
            throw new IllegalArgumentException(); // TODO: throw something else
        }

        parser.nextToken();
        String val = parser.getText();
        Part.TYPE type;
        if (Objects.equals(val, "s")) {
            type = Part.TYPE.STRAIGHT;
        } else if (Objects.equals(val, "c")) {
            type = Part.TYPE.CURVED;
        } else {
            throw new IllegalArgumentException(); // TODO: throw something else
        }
        if (parser.nextToken() != JsonToken.END_OBJECT) {
            throw new IllegalArgumentException(); // TODO: throw something else
        }

        return new Part(type, length, location);
    }

    private final String trackFile;
    private final ArrayList<Part> parts = new ArrayList<>();
    private final double length;

    RaceTrack( String filename ) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        double len = 0.0;
        try (JsonParser parser = new JsonFactory().createParser(classLoader.getResource(filename))) {
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                if (Objects.equals(parser.getCurrentName(), "parts")) {
                    // We found parts so load them
                    if (parser.nextToken() != JsonToken.START_ARRAY) {
                        throw new IllegalArgumentException(); // TODO: throw something else
                    }
                    while (parser.nextToken() == JsonToken.START_OBJECT) {
                        Part part = loadPart(parser, len);
                        len += part.length;
                        parts.add(part);
                    }
                    break;
                }
            }
        }

        System.out.println("Track length: " + len);
        length = len;
        trackFile = filename;
    }

    String getFileName() {
        return trackFile;
    }

    double getLength() {
        return length;
    }

    boolean inCorner( Car car ) {
        final double location = car.getLocation() * length;

        // Find segment this car is in
        for ( Part part : parts ) {
            if ( location >= part.location
                    && location <= part.location + part.length ) {

                return part.type == Part.TYPE.CURVED;
            }
        }
        // TODO: THROW because the car is outside of the track
        return false;
    }

    private static class Part {
        private enum TYPE {
            STRAIGHT,
            CURVED,
        }

        private final double location;
        private final double length;
        private final TYPE type;

        Part(TYPE type, double length, double location) {
            this.type = type;
            this.length = length;
            this.location = location;
        }

        public TYPE getType() {
            return type;
        }

        public double getLength() {
            return length;
        }

        public double getLocation() {
            return location;
        }
    }
}
