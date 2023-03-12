package com.microsoft;

import com.microsoft.eventhubplugin.*;

public class App {
    public static void main(String[] args) {
        DataGenerator dataGenerator = new DataGenerator();

        int number = dataGenerator.getRandomInt();
        float fNumber = dataGenerator.getRandomFloat();
        String now = dataGenerator.getNow().toString();
        String nowPlus1000 = dataGenerator.getNowPlus1000().toString();

        MessageRenderer messageRender = new MessageRenderer("../../../../../samplesStreamingDataTemplate.liquid");
        String result = messageRender.Render();
    }
}