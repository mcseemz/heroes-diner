package com.mcseemz.diner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Location;
import com.mcseemz.diner.model.SkillSuggestion;
import com.mcseemz.diner.model.Trial;
import com.mcseemz.diner.model.adventure.BaseEvent;
import com.mcseemz.diner.model.adventure.HeroUpdateRecord;
import com.mcseemz.diner.model.adventure.TeamworkEvent;
import com.mcseemz.diner.model.adventure.TrialEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Game state
 */
@Slf4j
@Service
@Getter
public class State {

    @Autowired
    private ResourceLoader resourceLoader;

    ObjectMapper objectMapper = new ObjectMapper();

    /** what skills we have */
    private String[] skills;
    private Trial[] trials;
    private Location[] locations;
    private HashMap<String,String[]> texts;

    /** what heroes do we have */
    private Hero[] roster;

    /** current turn */
    private int turn = 0;

    /** current stae of the game */

    private GAME_STATE state = GAME_STATE.idle;

    /** latest message in the game, including reports and pre-recorded */
    @Setter
    private String latestMessage;

    /** the latest teamwork that happened on a location */
    @Setter
    private int latestTeamwork = -1;

    /** what state the game can be in */
    public enum GAME_STATE {
        idle,
        waiting,    //waiting for use command
        editProfile,
        passed, //game finished
    }

    public void newGame() throws IOException {
        //make them modifiable
        skills = objectMapper.readValue(resourceLoader.getResource("classpath:skill.json").getInputStream(), String[].class);
        trials = objectMapper.readValue(resourceLoader.getResource("classpath:trial.json").getInputStream(), Trial[].class);
        locations = objectMapper.readValue(resourceLoader.getResource("classpath:location.json").getInputStream(), Location[].class);
        for (Location location : locations) {   //map codes to Trial objects
            location.setTrialsLoaded(Arrays.stream(location.getTrials()).map(ltr ->
                    Arrays.stream(trials).filter(tr -> tr.getCode().equals(ltr)).findFirst().orElseThrow()
                    )
                    .collect(Collectors.toList()));
        }

        //reading json as map, not a class
        TypeReference<HashMap<String,String[]>> typeRef = new TypeReference<>() {};
        texts = objectMapper.readValue(resourceLoader.getResource("classpath:text.json").getInputStream(), typeRef);
        //create heroes
        generateRoster();

        latestMessage = "@|italic type \"go\" and press Enter. Choose Paper Oracle |@ \n@|italic type \"help\" to see list of available commands|@";
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
                        .suggestedSkills(new HashSet<>())
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

        for (int i = 0; i < 5; i++) {
            heroes.get(i).setInTeam(true);
        }

        Collections.shuffle(heroes);

        roster = heroes.toArray(new Hero[0]);
    }

    /**
     * update hero and location states based on what happened duting the adventure
     * @param events what happened
     * @param location where it happened
     */
    public void updateGameState(Location location, List<BaseEvent> events) {
        for (BaseEvent baseEvent : events) {

            for (HeroUpdateRecord update : baseEvent.getHeroUpdates()) {
                Hero hero = update.getHero();

                switch (update.getType()) {
                    case skill_suggest: hero.getSuggestedSkills().add(
                            SkillSuggestion.builder().code((String) update.getValue())
                                    .certainty(SkillSuggestion.Certainty.found).build());
                        break;
                    case skill_unsuggest:
                        break;
                    case isOut:
                        break;
                    case needRest: hero.setDaysToRest((Integer) update.getValue());
                        break;
                    case powerup: hero.setPower(hero.getPower() + update.getValue());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + update.getType());
                }
            }

            if (baseEvent instanceof TrialEvent) {
                TrialEvent event = (TrialEvent) baseEvent;

                location.getSkillsKnown().add(SkillSuggestion.builder().code(event.getTrial().getSkill())
                        .certainty(SkillSuggestion.Certainty.found).build());

                //check if it was attempted by hero, and if failed
                if (event.getHero() != null) {
                    event.getHero().getSuggestedSkills().add(SkillSuggestion.builder().code(event.getTrial().getSkill())
                            .certainty(SkillSuggestion.Certainty.found).build());
                } else {
                    //if no hero found for a skill, then non has it.
                    //todo race condition here, as we get team from the state, and it could be updated already
                    // (someone kicked out of team by hero updates above)
                    for (Hero hero : getTeam()) {
                        hero.getSuggestedSkills().add(SkillSuggestion.builder().code(event.getTrial().getSkill())
                                .certainty(SkillSuggestion.Certainty.not_found).build());
                    }
                }
            }
            if (baseEvent instanceof TeamworkEvent) {
                TeamworkEvent event = (TeamworkEvent) baseEvent;
                location.setPassed(location.isPassed() || event.isPassed());
                setLatestTeamwork(event.getTeamWork());

                if (event.isPassed()) { //check if we need a hint
                    for (String bonus : location.getTeamwork().getBonus()) {
                        if (bonus.startsWith("map_")) {
                            String toshow = bonus.split("_")[1];
                            Arrays.stream(locations).filter(x -> x.getCode().equals(toshow)).findFirst().orElseThrow().setVisible(true);
                        }
                        if (bonus.startsWith("hint_")) {    //we found hint to location skills
                            String toshow = bonus.split("_")[1];
                            Location locToHint = Arrays.stream(locations).filter(x -> x.getCode().equals(toshow)).findFirst().orElseThrow();
                            Set<String> skillsRequired = locToHint.getSkillsRequired();
                            Set<SkillSuggestion> skillsKnown = locToHint.getSkillsKnown();
                            //find skills that is not yet known
                            skillsKnown.stream().map(SkillSuggestion::getCode).collect(Collectors.toList()).forEach(skillsRequired::remove);
                            if (!skillsRequired.isEmpty()) {
                                ArrayList<String> strings = new ArrayList<>(skillsRequired);
                                Collections.shuffle(strings);
                                skillsKnown.add(SkillSuggestion.builder().code(strings.get(0)).certainty(SkillSuggestion.Certainty.unsure).build());
                            }
                        }
                    }

                    if (event.getTeamWork() == event.getTeam().size()) {
                        for (Hero hero : event.getTeam()) {
                            hero.getSuggestedSkills().add(SkillSuggestion.builder().code("teamwork")
                                    .certainty(SkillSuggestion.Certainty.found).build());
                        }
                    }
                }
            }
        }

        //run passed
        turn++;

        //if this is oracle it is cannot be passed twice, should go invisible
        if (location.isPassed() && location.isOnlyonce()) {
            location.setVisible(false);
        }

        if (location.isPassed() && location.isTarget()) {
            state = GAME_STATE.passed;
        }
    }

    public List<Hero> getTeam() {
        return Arrays.stream(getRoster()).filter(Hero::isInTeam).collect(Collectors.toList());
    }

}
