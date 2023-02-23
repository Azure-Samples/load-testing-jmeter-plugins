package com.microsoft;

import java.util.Random;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DataGenerator {
    private Random random = new Random();

    private int randomInt;

    public int getRandomInt() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    private int randomNaturalNumber;

    public int getRandomNaturalNumber() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    private float randomFloat;

    public float getRandomFloat() {
        return getRandomFloat(Float.MAX_VALUE);
    }

    private String now;

    public String getNow() {
        return now(0);
    }

    private String nowPlus1000;

    public String getNowPlus1000() {
        return now(1000);
    }

    private int generateRandomInt(int max) {
        return random.nextInt(max);
    }

    private float getRandomFloat(float max) {
        return random.nextFloat() * max;
    }

    private String now(int milliseconds) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).plus(milliseconds, ChronoUnit.MILLIS);
        String nowString = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(now);
        return nowString;
    }

}
