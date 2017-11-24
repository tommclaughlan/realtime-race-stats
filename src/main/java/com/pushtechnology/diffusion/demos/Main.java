package com.pushtechnology.diffusion.demos;

import static spark.Spark.init;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

/**
 * Main class to handle initialisation and stuff.
 * Here we also start the web server to serve our JS bits.
 *
 */
public class Main
{
    private static void startWebServer() {
        port(3142);
        staticFiles.location("/html");
        init();
    }

    public static void main( String[] args )
    {
        startWebServer();
    }
}
