package it.unipi.largescale.pixelindex.utils;

public class AnsiColor {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    public static String ansiReset()
    {
        return ANSI_RESET;
    }
    public static String ansiYellow()
    {
        return ANSI_YELLOW;
    }
}
