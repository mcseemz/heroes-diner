package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.adventure.interfaces.EventAfter;
import com.mcseemz.diner.model.adventure.interfaces.EventAfterTrial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * when two leaders are in the team
 */
@SuperBuilder
@Getter
@AllArgsConstructor
public class ConflictLeadersEvent extends BaseEvent implements EventAfterTrial, EventAfter {

    List<Hero> leaders;

    public ConflictLeadersEvent() {
        type = Type.heroes_interaction;
    }

    public ConflictLeadersEvent run() {
        //find all bad actors, take random
        leaders = team.stream().filter(x -> x.getSkill().equals("leadership")).collect(Collectors.toList());
        if (leaders.size() < 2) return null;   //nothing to do

        Collections.shuffle(leaders);

        return this;
    }

    @Override
    public ConflictLeadersEvent getInitialized(List<Hero> team) {
        return builder().team(team).build().run();
    }

    @Override
    public int getProbability() {
        return 99;
    }
}
