package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.adventure.interfaces.EventAfter;
import com.mcseemz.diner.model.adventure.interfaces.EventAfterTrial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
public class SameskillEvent extends BaseEvent implements EventAfterTrial, EventAfter {

    List<Hero> team;

    Hero hero1;
    Hero hero2;

    public SameskillEvent() {
        type = Type.heroes_interaction;
    }

    public SameskillEvent run() {
        Map<String, List<Hero>> groups = team.stream().collect(Collectors.groupingBy(Hero::getSkill));

        for (Map.Entry<String, List<Hero>> entry : groups.entrySet()) {
            if (entry.getValue().size() < 2) continue;

            hero1 = entry.getValue().get(0);
            hero2 = entry.getValue().get(1);
        }

        //todo should this be leader notes?
        return this;
    }

    @Override
    public int getProbability() {
        return 99;
    }

    @Override
    public SameskillEvent getInitialized(List<Hero> team) {
        return builder().team(team).build().run();
    }

}
