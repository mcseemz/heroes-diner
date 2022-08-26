package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Trial;
import com.mcseemz.diner.model.adventure.interfaces.EventAfter;
import com.mcseemz.diner.model.adventure.interfaces.EventAfterTrial;
import com.mcseemz.diner.model.adventure.interfaces.EventBefore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@Getter
@AllArgsConstructor
public class ConflictEvent extends BaseEvent implements EventAfterTrial, EventAfter {

    Hero badActor;
    Hero goodActor;

    public ConflictEvent() {
        type = Type.heroes_interaction;
    }

    public ConflictEvent run() {
        //find all bad actors, take random
        List<Hero> badActors = team.stream().filter(x -> x.getTeamWork() < 0).collect(Collectors.toList());
        if (badActors.isEmpty()) return null;   //nothing to do

        Collections.shuffle(badActors);
        badActor = badActors.get(0);
        //find any other hero
        List<Hero> otherActors = team.stream().filter(x -> !x.equals(badActor)).collect(Collectors.toList());
        if (otherActors.isEmpty()) return null;   //nothing to do
        Collections.shuffle(otherActors);

        goodActor = otherActors.get(0);

        return this;
    }

    @Override
    public ConflictEvent getInitialized(List<Hero> team) {
        return builder().team(team).build().run();
    }

    @Override
    public int getProbability() {
        return 99;
    }
}
