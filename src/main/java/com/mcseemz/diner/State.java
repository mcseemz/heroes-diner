package com.mcseemz.diner;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Game state
 */
@Slf4j
@Service
@Getter
public class State {

    @Autowired
    private ResourceLoader resourceLoader;

    /** what specialities we have */
    private String[] specialities;
//    private String[] trials;
//    private String[] locations;

    /** current turn */
    private int turn = 0;

    /** current stae of the game */

    private GAME_STATE state;

    /** latest message in the game, including reports and pre-recorded */
    private String latestMessage;

    /** what state the game can be in */
    public enum GAME_STATE {
        idle,
        waiting,    //waiting for use command
        editProfile,
    }

    public void newGame() throws IOException {
        specialities = new ObjectMapper().readValue(resourceLoader.getResource("classpath:speciality.json").getInputStream(), String[].class);
//        state.trials = new ObjectMapper().readValue(resourceLoader.getResource("classpath:trial.json").getInputStream(), String[].class);
//        state.trials = new ObjectMapper().readValue(resourceLoader.getResource("classpath:location.json").getInputStream(), String[].class);

        latestMessage = "Hey bartender! Do ou have any place of interest here, like, for a _real_ heroes?";
        state = GAME_STATE.waiting;

        log.debug("read {} specialities", specialities.length);
    }
}
