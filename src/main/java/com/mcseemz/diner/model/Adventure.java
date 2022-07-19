package com.mcseemz.diner.model;

import com.mcseemz.diner.model.adventure.BaseEvent;
import com.mcseemz.diner.model.adventure.TrialEvent;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Data
public class Adventure {

    private List<BaseEvent> adventureEvents = new ArrayList<>();

    @Autowired
    private List<BaseEvent> list;

    List<Hero> team;
    Location location;

    boolean isPassed = true;

    public Adventure(List<Hero> team, Location location) {
        this.team = team;
        this.location = location;
    }

    /**
     * run the team against trials
     * @return list of what happened
     */
    public List<BaseEvent> run() {
        //reset team "acted" status
        team.forEach(x -> x.setActed(false));

        //todo run EventBefore events. Do we need probabilities?
        //e.g. team modifiers event
        //for each Trial
        for (Trial trial : location.getTrialsLoaded()) {
            //choose hero
            Hero hero = team.stream().filter(x -> !x.isActed() && !x.isOut() && x.getSkill().equals(trial.getSkill())).findFirst().orElse(null); //or else nobody

            //  run TrialEvent
            TrialEvent event = TrialEvent.builder().trial(trial).hero(hero).build().run();

            adventureEvents.add(event);
            if (hero != null) { //mark hero as acted
                hero.setActed(true);
            }

            if (!event.isPassed()) {    //failed adventure
                isPassed = false;
                break;
            }

            //  run EventAfterTrial with probability

        }

        //todo run EventAfter with probability
        return adventureEvents;
    }

}
