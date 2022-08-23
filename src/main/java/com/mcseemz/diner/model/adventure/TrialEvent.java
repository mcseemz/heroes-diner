package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Trial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class TrialEvent extends BaseEvent {

    Trial trial;
    Hero hero;

    boolean isPassed;
    int needRest = 0;

    public TrialEvent() {
        type = Type.encounter;
    }

    public TrialEvent run() {
        //todo can pass one difficulty more but needs rest. Or rest is random?
        //or if the win is due to teamwork boost then rest is required

        if (hero != null && hero.getPower().compareTo(trial.getDifficulty()) >= 0) {
            isPassed = true;

            heroUpdates.add(HeroUpdateRecord.builder()
                    .hero(hero).type(PropertyType.skill_suggest).value(trial.getSkill()).build());
        }

        return this;
    }

    @Override
    public BaseEvent getInitialized(List<Hero> team) {
        throw new RuntimeException("Invalid TrialEvent initialization. Should not happen");
    }
}
