package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.adventure.interfaces.EventAfter;
import com.mcseemz.diner.model.adventure.interfaces.EventBefore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
public class LeaderTreatEvent extends BaseEvent implements EventBefore, EventAfter {

    Hero badActor;
    Hero goodActor;

    public LeaderTreatEvent() {
        type = Type.leadership;
    }

    public LeaderTreatEvent run() {

        if (getLeader() != null) {
            leaderNotes.add(HeroUpdateRecord.builder().type(PropertyType.get_a_treat).build());
            return this;
        }
        return null;
    }

    @Override
    public int getProbability() {
        return 50;
    }

    @Override
    public LeaderTreatEvent getInitialized(List<Hero> team) {
        return builder().team(team).leaderNotes(new ArrayList<>()).build().run();
    }
}
