package com.pushtechnology.diffusion.demos;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class RaceTrack {
    private static Part loadPart( JsonParser parser ) throws IOException {
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

        return new Part(type, length);
    }

    private final String trackFile;

    public RaceTrack( String filename ) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        final ArrayList<Part> parts = new ArrayList<>();
        try( JsonParser parser = new JsonFactory().createParser( classLoader.getResource(filename) )) {
            while ( parser.nextToken() != JsonToken.END_OBJECT ) {
                if (Objects.equals(parser.getCurrentName(), "parts")) {
                    // We found parts so load them
                    if ( parser.nextToken() != JsonToken.START_ARRAY  ) {
                        throw new IllegalArgumentException(); // TODO: throw something else
                    }
                    while ( parser.nextToken() == JsonToken.START_OBJECT ) {
                        parts.add(loadPart( parser ));
                    }
                    break;
                }
            }
        }

        trackFile = filename;
    }

    public String getFileName() {
        return trackFile;
    }

    private static class Part {
        private enum TYPE {
            STRAIGHT,
            CURVED,
        }

        private final double length;
        private final TYPE type;

        public Part( TYPE type, double length ) {
            this.type = type;
            this.length = length;
        }

        public TYPE getType() {
            return type;
        }

        public double getLength() {
            return length;
        }
    }
}
