package com.mcseemz.diner.model;

import com.mcseemz.diner.model.adventure.BaseEvent;
import com.mcseemz.diner.model.adventure.ConflictEvent;
import com.mcseemz.diner.model.adventure.ConflictLeadersEvent;
import com.mcseemz.diner.model.adventure.LeaderTreatEvent;
import com.mcseemz.diner.model.adventure.TeamworkEvent;
import com.mcseemz.diner.model.adventure.TrialEvent;
import com.mcseemz.diner.model.adventure.interfaces.EventAfter;
import com.mcseemz.diner.model.adventure.interfaces.EventAfterTrial;
import com.mcseemz.diner.model.adventure.interfaces.EventBefore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Builder
@Slf4j
@AllArgsConstructor
public class Adventure {

    Random random = new Random();

    private List<BaseEvent> adventureEvents = new ArrayList<>();
    private List<EventBefore> eventsBefore = List.of(new LeaderTreatEvent());
    private List<EventAfterTrial> eventsAfterTrial = List.of(new ConflictEvent(), new ConflictLeadersEvent());
    private List<EventAfter> eventsAfter = List.of(new ConflictEvent(), new LeaderTreatEvent(), new ConflictLeadersEvent());

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

        // run EventBefore events. Do we need probabilities?
        if (!eventsBefore.isEmpty()) {
            log.debug("running eventsBefore. Size: {}", eventsBefore.size());
            EventBefore eventBefore = eventsBefore.get(random.nextInt(eventsBefore.size()));
            if (Math.random() < eventBefore.getProbability()/100F) {
                //event happened
                adventureEvents.add(eventBefore.getInitialized(team));
            }
        }

        //e.g. team modifiers event
        //for each Trial
        for (Trial trial : location.getTrialsLoaded()) {
            //choose hero
            Hero hero = team.stream().filter(x -> !x.isActed() && !x.isOut() && x.getSkill().equals(trial.getSkill())).findFirst().orElse(null); //or else nobody

            //  run TrialEvent
            TrialEvent event = TrialEvent.builder().trial(trial).team(team).heroUpdates(new ArrayList<>()).hero(hero).build().run();

            adventureEvents.add(event);
            if (hero != null) { //mark hero as acted
                hero.setActed(true);
            }

            if (!event.isPassed()) {    //failed adventure
                isPassed = false;
                break;
            }

            //  run EventAfterTrial with probability
            if (!eventsAfterTrial.isEmpty()) {
                EventAfterTrial eventAfterTrial = eventsAfterTrial.get(random.nextInt(eventsAfterTrial.size()));

                if (Math.random() < eventAfterTrial.getProbability()/100F) {
                    //event happened
                    adventureEvents.add(eventAfterTrial.getInitialized(team));
                }
            }
        }

        if (isPassed && !location.isPassed()) { //only once per location
            //time to run teamwork
            TeamworkEvent event = TeamworkEvent.builder().location(location).leaderNotes(new ArrayList<>())
                    .heroUpdates(new ArrayList<>()).team(team).build().run();
            adventureEvents.add(event);
        }
        // run EventAfter with probability
        if (!eventsAfter.isEmpty()) {
            EventAfter eventAfter = eventsAfter.get(random.nextInt(eventsAfter.size()));
            if (Math.random() < eventAfter.getProbability()/100F) {
                //event happened
                adventureEvents.add(eventAfter.getInitialized(team));
            }
        }

        return adventureEvents;
    }

}
