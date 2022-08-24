package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.adventure.interfaces.EventAfter;
import com.mcseemz.diner.model.adventure.interfaces.EventBefore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class LeaderTreatEvent extends BaseEvent implements EventBefore, EventAfter {

    List<Hero> team;

    Hero badActor;
    Hero goodActor;

    public LeaderTreatEvent() {
        type = Type.leadership;
    }

    public LeaderTreatEvent run() {

        Hero leader = team.stream().filter(x -> x.getSkill().equals("leadership")).findFirst().orElse(null);
        if (leader != null) {
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
        return builder().team(team).build().run();
    }
}
