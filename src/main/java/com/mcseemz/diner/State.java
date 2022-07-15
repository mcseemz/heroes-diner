package com.mcseemz.diner;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Location;
import com.mcseemz.diner.model.Trial;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Game state
 */
@Slf4j
@Service
@Getter
public class State {

    @Autowired
    private ResourceLoader resourceLoader;

    /** what skills we have */
    private String[] skills;
    private Trial[] trials;
    private Location[] locations;

    /** what heroes do we have */
    private Hero[] roster;

    /** current turn */
    private int turn = 0;

    /** current stae of the game */

    private GAME_STATE state = GAME_STATE.idle;

    /** latest message in the game, including reports and pre-recorded */
    private String latestMessage;

    /** what state the game can be in */
    public enum GAME_STATE {
        idle,
        waiting,    //waiting for use command
        editProfile,
    }

    public void newGame() throws IOException {
        //make them modifiable
        skills = new ObjectMapper().readValue(resourceLoader.getResource("classpath:skill.json").getInputStream(), String[].class);
        trials = new ObjectMapper().readValue(resourceLoader.getResource("classpath:trial.json").getInputStream(), Trial[].class);
        locations = new ObjectMapper().readValue(resourceLoader.getResource("classpath:location.json").getInputStream(), Location[].class);

        generateRoster();

        latestMessage = "Hey bartender! Do ou have any place of interest here, like, for a _real_ heroes?";
        state = GAME_STATE.waiting;

        log.debug("read {} skills", skills.length);
    }

    /**
     * rules:
     * 12 heroes
     * 2 of each speciality plus 2 with leadership skill
     * 2 of them have negative team skill, but they should have different skills
     */
    private void generateRoster() {
        List<Hero> heroes = new ArrayList<>();

        for (String skill : skills) {
            for (int i = 0; i < 2; i++) {
                Faker faker = new Faker();
                heroes.add(Hero.builder().
                        name(faker.name().fullName())
                        .daysToRest(0)
                        .inTeam(false)
                        .isOut(false)
                        .power("*")
                        .suggestedSkills(new ArrayList<>())
                        .teamWork((int) Math.floor(Math.random() + 0.7))    //increase chance of teamwork
                        .skill(skill)
                        .build());
            }
        }
        //so they are random now
        Collections.shuffle(heroes);

        //let's set 2 heros with negative attitude
        String  skill1 = heroes.get(0).getSkill();
        heroes.get(0).setTeamWork(heroes.get(0).getTeamWork() * -1);

        //we have only 2 of each kind, so it's enough to make one check for next hero
        if (heroes.get(1).getSkill().equals(skill1)) {
            heroes.get(2).setTeamWork(heroes.get(0).getTeamWork() * -1);
        } else {
            heroes.get(1).setTeamWork(heroes.get(0).getTeamWork() * -1);
        }

        //shuffle again to spread negative effects
        Collections.shuffle(heroes);

        heroes.get(0).setInTeam(true);
        heroes.get(1).setInTeam(true);

        Collections.shuffle(heroes);

        roster = heroes.toArray(new Hero[0]);
    }
}