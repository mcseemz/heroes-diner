package com.mcseemz.diner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HeroesDinerApplication {

    public static void main(String[] args) {

        System.out.println("Heroes Bar, Worchester is loading.");
        System.out.println("Type 'manual' to get game guide.");
        System.out.println("Type 'start' to start the game.");
        System.out.println("Type 'help' to get the list of available commands.");
        SpringApplication.run(HeroesDinerApplication.class, args);
    }

}
