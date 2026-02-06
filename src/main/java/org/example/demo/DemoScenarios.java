package org.example.demo;


import java.util.*;


public class DemoScenarios {



    public static List<Integer> randomScenario(int count) {
        List<Integer> pages = new ArrayList<>();
        Random rand = new Random(42);
        for (int i = 0; i < count; i++) {
            pages.add(rand.nextInt(20));
        }
        return pages;
    }


    public static List<Integer> sequentialScenario(int start, int end) {
        List<Integer> pages = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            pages.add(i);
        }
        return pages;
    }



}